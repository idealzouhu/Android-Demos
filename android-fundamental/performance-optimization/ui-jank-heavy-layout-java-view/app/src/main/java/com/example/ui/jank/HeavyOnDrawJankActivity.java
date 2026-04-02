package com.example.ui.jank;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class HeavyOnDrawJankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_heavy_ondraw_jank);
        JankToolbarHelper.setupBack(this, R.string.title_heavy_ondraw);
    }
}
