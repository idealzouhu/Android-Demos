package com.example.customview.basic;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customview.signalmeterview.SignalMeterView;

public class MainActivity extends AppCompatActivity {

    private ValueAnimator meterAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SignalMeterView meter = findViewById(R.id.signal_meter);
        float max = meter.getMaxValue();
        meterAnimator = ValueAnimator.ofFloat(0f, max);
        meterAnimator.setDuration(2800);
        meterAnimator.setRepeatCount(ValueAnimator.INFINITE);
        meterAnimator.setRepeatMode(ValueAnimator.REVERSE);
        meterAnimator.setInterpolator(new LinearInterpolator());
        meterAnimator.addUpdateListener(a -> meter.setCurrentValue((Float) a.getAnimatedValue()));
        meterAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (meterAnimator != null) {
            meterAnimator.cancel();
            meterAnimator = null;
        }
    }
}