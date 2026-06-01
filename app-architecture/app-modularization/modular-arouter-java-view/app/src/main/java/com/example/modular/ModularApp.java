package com.example.modular;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import dagger.hilt.android.HiltAndroidApp;

/**
 * 应用层：全局初始化，在此初始化 ARouter；Hilt 依赖注入入口。
 */
@HiltAndroidApp
public class ModularApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }
}
