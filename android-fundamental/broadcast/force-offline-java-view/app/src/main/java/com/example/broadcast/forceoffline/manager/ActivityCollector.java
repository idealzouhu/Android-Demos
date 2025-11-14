package com.example.broadcast.forceoffline.manager;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityCollector {

    // 饿汉式单例：在类加载时就创建实例
    private static final ActivityCollector INSTANCE = new ActivityCollector();

    // 存储Activity的列表
    private final List<Activity> activities;

    // 私有构造函数，防止外部实例化
    private ActivityCollector() {
        activities = new ArrayList<>();
    }

    // 提供获取单例实例的公共方法
    public static ActivityCollector getInstance() {
        return INSTANCE;
    }

    public  void addActivity(Activity activity) {
        activities.add(activity);
    }

    public  void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    // 最关键的方法：一键关闭所有Activity
    public  void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }
}
