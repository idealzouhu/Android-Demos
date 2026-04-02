package com.example.ui.jank;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

/** 子页面统一：标题 + 返回。 */
final class JankToolbarHelper {

    private JankToolbarHelper() {}

    static void setupBack(AppCompatActivity activity, int titleRes) {
        MaterialToolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle(titleRes);
        toolbar.setNavigationOnClickListener(
                v -> activity.getOnBackPressedDispatcher().onBackPressed());
    }
}
