package com.example.memory.leak;

import android.app.Activity;
import android.util.Log;

/**
 * 经典错误：后台 {@link Thread} 的 {@link Runnable} 闭包捕获 {@link Activity}，
 * 在线程结束之前 Activity 无法被回收。
 */
public final class LeakBackgroundThread {

    private static final String TAG = "LeakDemo";

    private static Thread sThread;

    private LeakBackgroundThread() {}

    public static void startHold(Activity activity) {
        clear();
        sThread =
                new Thread(
                        () -> {
                            try {
                                Thread.sleep(120_000L);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                            if (activity != null) {
                                Log.i(TAG, "LeakBackgroundThread: 结束 activityHash=" + activity.hashCode());
                            }
                        },
                        "leak-demo-bg-thread");
        sThread.start();
        Log.w(TAG, "LeakBackgroundThread: 线程已启动并捕获 Activity 引用");
    }

    public static void clear() {
        if (sThread != null) {
            sThread.interrupt();
            sThread = null;
        }
    }
}
