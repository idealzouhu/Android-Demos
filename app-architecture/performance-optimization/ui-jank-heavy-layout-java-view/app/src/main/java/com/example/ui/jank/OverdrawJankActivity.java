package com.example.ui.jank;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

/** 多层不透明子 View 全屏叠加，便于观察 GPU 过度绘制。 */
public class OverdrawJankActivity extends AppCompatActivity {

    private static final int LAYER_COUNT = 22;

    private static final int[] LAYER_COLORS = {
        Color.parseColor("#E57373"),
        Color.parseColor("#F06292"),
        Color.parseColor("#BA68C8"),
        Color.parseColor("#9575CD"),
        Color.parseColor("#7986CB"),
        Color.parseColor("#64B5F6"),
        Color.parseColor("#4FC3F7"),
        Color.parseColor("#4DD0E1"),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_overdraw_jank);
        JankToolbarHelper.setupBack(this, R.string.title_overdraw);

        FrameLayout container = findViewById(R.id.overdraw_container);
        for (int i = 0; i < LAYER_COUNT; i++) {
            FrameLayout layer = new FrameLayout(this);
            layer.setBackgroundColor(LAYER_COLORS[i % LAYER_COLORS.length]);
            container.addView(
                    layer,
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));
        }
    }
}
