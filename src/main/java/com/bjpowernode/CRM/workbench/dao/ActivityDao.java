package com.bjpowernode.CRM.workbench.dao;

import com.bjpowernode.CRM.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

//市场活动接口
public interface ActivityDao {
    int save(Activity activity);

    int getTotalByCondition(Map<String, Object> map);

    List<Activity> getActivityListByCondition(Map<String, Object> map);
}
