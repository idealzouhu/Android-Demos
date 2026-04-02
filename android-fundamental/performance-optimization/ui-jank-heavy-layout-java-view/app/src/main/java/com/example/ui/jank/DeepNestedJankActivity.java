package com.example.ui.jank;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

/** 纵向极深 LinearLayout 嵌套，放大 measure/layout 成本。 */
public class DeepNestedJankActivity extends AppCompatActivity {

    private static final int DEPTH = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_deep_nested_jank);
        JankToolbarHelper.setupBack(this, R.string.title_deep_nested);

        LinearLayout root = findViewById(R.id.deep_nested_root);
        LinearLayout current = root;
        for (int d = 0; d < DEPTH; d++) {
            LinearLayout child = new LinearLayout(this);
            child.setOrientation(LinearLayout.VERTICAL);
            int p = 4;
            child.setPadding(p, p, p, p);
            current.addView(
                    child,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            current = child;
        }

        TextView leaf = new TextView(this);
        leaf.setText(getString(R.string.hint_deep_nested));
        current.addView(
                leaf,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
    }
}
