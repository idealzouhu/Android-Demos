package com.example.memory.leak;

import android.app.Activity;

/**
 * 后台 {@link Thread} 的 Runnable 捕获了 {@link Activity}，线程未结束前 Activity 无法被回收。
 */
public final class LeakThreadHold {

    private static Thread sThread;

    private LeakThreadHold() {}

    public static void startSleepingThread(Activity activity, long sleepMs) {
        clear();
        sThread =
                new Thread(
                        () -> {
                            try {
                                Thread.sleep(sleepMs);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            if (activity != null) {
                                activity.hashCode();
                            }
                        },
                        "memory-leak-demo-thread");
        sThread.start();
    }

    public static void clear() {
        if (sThread != null && sThread.isAlive()) {
            sThread.interrupt();
        }
        sThread = null;
    }
}
