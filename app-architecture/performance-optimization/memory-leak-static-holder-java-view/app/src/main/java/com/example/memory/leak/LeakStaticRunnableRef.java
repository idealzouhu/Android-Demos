package com.example.memory.leak;

/**
 * 静态变量持有 {@link Runnable}；若 Runnable 为匿名内部类/非静态内部类，会隐式持有外部
 * {@code Activity}。
 */
public final class LeakStaticRunnableRef {

    private static Runnable sTask;

    private LeakStaticRunnableRef() {}

    public static void hold(Runnable task) {
        sTask = task;
    }

    public static void clear() {
        sTask = null;
    }
}
