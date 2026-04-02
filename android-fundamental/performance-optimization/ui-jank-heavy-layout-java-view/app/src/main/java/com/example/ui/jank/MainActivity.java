package com.example.ui.jank;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_overdraw)
                .setOnClickListener(v -> startActivity(new Intent(this, OverdrawJankActivity.class)));
        findViewById(R.id.btn_deep_nested)
                .setOnClickListener(
                        v -> startActivity(new Intent(this, DeepNestedJankActivity.class)));
        findViewById(R.id.btn_recycler)
                .setOnClickListener(v -> startActivity(new Intent(this, RecyclerJankActivity.class)));
        findViewById(R.id.btn_heavy_ondraw)
                .setOnClickListener(
                        v -> startActivity(new Intent(this, HeavyOnDrawJankActivity.class)));
        findViewById(R.id.btn_layout_thrash)
                .setOnClickListener(
                        v -> startActivity(new Intent(this, LayoutThrashJankActivity.class)));
    }
}
