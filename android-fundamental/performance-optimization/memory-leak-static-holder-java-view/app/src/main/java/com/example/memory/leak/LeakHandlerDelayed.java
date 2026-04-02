package com.example.memory.leak;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

/**
 * {@link Handler} 的 {@code Message}/{@code Runnable} 队列持有 Runnable；Runnable 若闭包捕获了
 * {@link Activity}，在延迟到达前 Activity 无法被回收。
 */
public final class LeakHandlerDelayed {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private static Runnable pending;

    private LeakHandlerDelayed() {}

    public static void postDelayedActivityRef(Activity activity, long delayMs) {
        clear();
        pending =
                () -> {
                    if (activity != null) {
                        activity.hashCode();
                    }
                };
        HANDLER.postDelayed(pending, delayMs);
    }

    public static void clear() {
        if (pending != null) {
            HANDLER.removeCallbacks(pending);
            pending = null;
        }
    }
}
