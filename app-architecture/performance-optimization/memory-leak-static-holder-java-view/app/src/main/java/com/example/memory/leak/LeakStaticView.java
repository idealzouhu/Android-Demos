package com.example.memory.leak;

import android.content.Context;
import android.widget.TextView;

/**
 * 静态引用 {@link android.view.View}；View 通过 Context 关联到 Activity，形成间接泄漏。
 */
public final class LeakStaticView {

    private static TextView sView;

    private LeakStaticView() {}

    public static void holdWithActivityContext(Context activityContext) {
        sView = new TextView(activityContext);
        sView.setText(R.string.leak_static_view_caption);
    }

    public static void clear() {
        sView = null;
    }
}
