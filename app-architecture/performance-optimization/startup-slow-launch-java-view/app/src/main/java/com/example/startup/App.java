package com.example.startup;

import android.app.Application;
import android.os.SystemClock;

/**
 * 故意在 Application 启动早期执行重活，用于演示冷启动被拉长的典型错误写法。
 */
public class App extends Application {

    private static final long STARTUP_DELAY_MS = 1800L;

    private static long appOnCreateCostMs;
    private static long processStartElapsedRealtime;

    @Override
    public void onCreate() {
        processStartElapsedRealtime = SystemClock.elapsedRealtime();
        long start = SystemClock.elapsedRealtime();
        super.onCreate();

        // 演示用重活：主线程睡眠 + 大量字符串拼接，故意阻塞首屏前的启动关键路径。
        SystemClock.sleep(STARTUP_DELAY_MS);
        String text = "";
        for (int i = 0; i < 3000; i++) {
            text = text + i;
        }

        appOnCreateCostMs = SystemClock.elapsedRealtime() - start;
    }

    public static long getAppOnCreateCostMs() {
        return appOnCreateCostMs;
    }

    public static long getProcessStartElapsedRealtime() {
        return processStartElapsedRealtime;
    }
}
