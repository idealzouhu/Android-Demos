package com.example.startup;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        long now = SystemClock.elapsedRealtime();
        long appOnCreateCostMs = App.getAppOnCreateCostMs();
        long processVisibleCostMs = now - App.getProcessStartElapsedRealtime();

        ((TextView) findViewById(R.id.tv_startup_summary))
                .setText(getString(R.string.startup_summary_value, appOnCreateCostMs));
        ((TextView) findViewById(R.id.tv_visible_summary))
                .setText(getString(R.string.visible_summary_value, processVisibleCostMs));
    }
}