package com.bjowernode.CRM.settings.service.impl;

import com.bjowernode.CRM.settings.dao.UserDao;
import com.bjowernode.CRM.settings.domain.User;
import com.bjowernode.CRM.settings.exception.LoginException;
import com.bjowernode.CRM.settings.service.UserService;
import com.bjowernode.CRM.utils.DateTimeUtil;
import com.bjowernode.CRM.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    //这里是业务层，调用dao层，也就是把dao层对象创建出来(用工具)
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);
        //调用dao层
        User user=userDao.login(map);
        if (user == null) {
            //空了就代表没有这个账户，自然就得抛异常
            throw new LoginException("账号不存在或者密码错误");
        }
        //如果能到这里，代表账号密码没错了，如果throws了，后续代码自动停止

        //继续向下验证其他三项信息，包括失效时间，锁定状态，ip地址
        //验证失效时间,从数据库获取失效时间，从工具类里面拿到系统时间,最后验证比较
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if (expireTime.compareTo(currentTime)<0) {//小于0了代表超过系统时间
            //此时可以抛出异常
            throw new LoginException("账户时间超时");
        }

        //验证锁定状态,拿到锁定状态然后判断
        String lockstates = user.getLockState();
        if ("0".equals(lockstates)) {
            //if的"0".equals(lockstates)一定是把0放在前面，如果把lockstates放在前面，一旦lockstate==null
            //直接空指针报错
            throw new LoginException("账户状态锁定，请联系管理员处理");
        }

        //验证ip地址，还是先把ip拿出来，然后在库中进行比较，如果包含就放行
        String allowIps = user.getAllowIps();
        if (!allowIps.contains(ip)) {
            throw new LoginException("Ip地址访问受限，请联系管理员处理");
        }
        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> userList = (List<User>) userDao.getUserList();
        //调用dao层
        return userList;
    }
}