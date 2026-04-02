package com.example.memory.leak;

import android.content.Context;

/**
 * 单例长期存活，若保存了 {@link Activity}（或带 UI 的 {@code Context}），会连带持有整棵 View 树。
 */
public final class LeakSingletonContext {

    private static final LeakSingletonContext INSTANCE = new LeakSingletonContext();

    private Context context;

    private LeakSingletonContext() {}

    public static LeakSingletonContext get() {
        return INSTANCE;
    }

    /** 错误示范：传入 {@code this}（Activity）而非 {@code getApplicationContext()}。 */
    public void setContext(Context activityContext) {
        this.context = activityContext;
    }

    public void clear() {
        context = null;
    }
}
