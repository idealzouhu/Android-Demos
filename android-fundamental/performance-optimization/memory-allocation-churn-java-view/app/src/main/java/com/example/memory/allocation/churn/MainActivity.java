package com.example.memory.allocation.churn;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 后台一条热点（循环 + 拼 String）+ 可选 onDraw 每帧分配。建议 Memory Profiler 录制分配观察。
 */
public class MainActivity extends AppCompatActivity {

    private static final int STRING_CONCAT_ITERATIONS = 1_200;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        BadAllocationOnDrawView drawView = findViewById(R.id.bad_on_draw_view);
        MaterialSwitch switchChurn = findViewById(R.id.switch_on_draw_churn);
        switchChurn.setOnCheckedChangeListener(
                (buttonView, isChecked) -> drawView.setChurnInOnDraw(isChecked));

        findViewById(R.id.btn_string_concat_loop)
                .setOnClickListener(
                        v ->
                                runOnBackground(
                                        getString(R.string.demo_string_concat),
                                        () ->
                                                AllocationChurnDemos.stringConcatPlusInLoop(
                                                        STRING_CONCAT_ITERATIONS)));
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

    private void runOnBackground(String label, Runnable work) {
        executor.execute(
                () -> {
                    try {
                        work.run();
                    } catch (Throwable t) {
                        mainHandler.post(
                                () ->
                                        Toast.makeText(
                                                        MainActivity.this,
                                                        label + " 异常: " + t.getMessage(),
                                                        Toast.LENGTH_LONG)
                                                .show());
                        return;
                    }
                    mainHandler.post(
                            () ->
                                    Toast.makeText(
                                                    MainActivity.this,
                                                    getString(R.string.toast_demo_done, label),
                                                    Toast.LENGTH_SHORT)
                                            .show());
                });
    }
}
