package com.example.memory.leak;

import android.app.Activity;

/**
 * 经典错误：静态变量持有 {@link Activity}，使该实例在「离开界面」后仍无法被回收。
 */
public final class LeakStaticActivityRef {

    private static Activity sHeldActivity;

    private LeakStaticActivityRef() {}

    public static void hold(Activity activity) {
        sHeldActivity = activity;
    }

    public static void clear() {
        sHeldActivity = null;
    }
}
