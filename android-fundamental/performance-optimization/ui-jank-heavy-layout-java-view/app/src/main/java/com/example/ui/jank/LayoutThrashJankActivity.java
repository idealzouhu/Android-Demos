package com.example.ui.jank;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

/** 滑动时反复改变 header 高度并 requestLayout，制造 layout thrashing。 */
public class LayoutThrashJankActivity extends AppCompatActivity {

    private static final int FILLER_LINES = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_layout_thrash_jank);
        JankToolbarHelper.setupBack(this, R.string.title_layout_thrash);

        LinearLayout content = findViewById(R.id.thrash_content);
        for (int i = 0; i < FILLER_LINES; i++) {
            TextView line = new TextView(this);
            line.setText(getString(R.string.thrash_line, i));
            line.setPadding(0, 8, 0, 8);
            content.addView(
                    line,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        ScrollView scrollView = findViewById(R.id.thrash_scroll);

        final FrameLayout header = findViewById(R.id.thrash_header);
        final int baseHeightPx =
                (int)
                        TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                120f,
                                getResources().getDisplayMetrics());
        final int bounceRangePx =
                (int)
                        TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                72f,
                                getResources().getDisplayMetrics());

        scrollView.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    int bounce = bounceRangePx > 0 ? scrollY % bounceRangePx : 0;
                    LinearLayout.LayoutParams lp =
                            (LinearLayout.LayoutParams) header.getLayoutParams();
                    lp.height = baseHeightPx + bounce;
                    header.setLayoutParams(lp);
                    header.requestLayout();
                });
    }
}
