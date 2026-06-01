package com.example.lifecycle.basic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LifecycleDemo";
    private TextView tvCurrentState;
    private TextView tvStateDescription;
    private Button btnTriggerEvent;
    private Button btnShowCurrentState;

    private MyLifecycleObserver lifecycleObserver;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int customEventCount = 0;

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

        Log.i(TAG, "=== MainActivity 创建开始 ===");
        Log.i(TAG, "onCreate() 被调用");
        Log.i(TAG, "savedInstanceState: " + (savedInstanceState != null ? "有状态" : "无状态"));

        initViews();
        setupListeners();
        registerLifecycleObserver();

        Log.i(TAG, "=== MainActivity 创建完成 ===");
    }

    private void initViews() {
        tvCurrentState = findViewById(R.id.tvCurrentState);
        tvStateDescription = findViewById(R.id.tvStateDescription);
        btnTriggerEvent = findViewById(R.id.btnTriggerEvent);
        btnShowCurrentState = findViewById(R.id.btnShowCurrentState);
    }

    private void setupListeners() {
        btnTriggerEvent.setOnClickListener(v -> triggerCustomEvent());
        btnShowCurrentState.setOnClickListener(v -> showCurrentLifecycleState());

        // 长按显示详细状态
        btnShowCurrentState.setOnLongClickListener(v -> {
            showDetailedStateInfo();
            return true;
        });
    }

    private void registerLifecycleObserver() {
        lifecycleObserver = new MyLifecycleObserver(tvCurrentState, tvStateDescription);
        getLifecycle().addObserver(lifecycleObserver);
        Log.i(TAG, "生命周期观察者已注册");
    }

    private void triggerCustomEvent() {
        customEventCount++;
        String eventName = "自定义事件 #" + customEventCount;

        Log.i(TAG, "🎯 手动触发: " + eventName);
        Toast.makeText(this, "触发: " + eventName, Toast.LENGTH_SHORT).show();

        // 更新UI显示
        tvCurrentState.setText("事件触发");
        tvStateDescription.setText("已触发 " + eventName);

        // 2秒后恢复状态
        handler.postDelayed(() -> updateStateFromLifecycle(), 2000);
    }

    private void updateStateFromLifecycle() {
        Lifecycle.State currentState = getLifecycle().getCurrentState();
        String stateName = getStateName(currentState);

        tvCurrentState.setText(stateName);
        tvStateDescription.setText(getStateDescription(stateName));

        Log.i(TAG, "当前生命周期状态: " + stateName);
    }

    private String getStateName(Lifecycle.State state) {
        switch (state) {
            case CREATED: return "Created";
            case STARTED: return "Started";
            case RESUMED: return "Resumed";
            case DESTROYED: return "Destroyed";
            default: return "Unknown";
        }
    }

    private String getStateDescription(String state) {
        switch (state) {
            case "Created": return "Activity 已创建但不可见";
            case "Started": return "Activity 可见但未获取焦点";
            case "Resumed": return "Activity 获得焦点，可交互";
            case "Destroyed": return "Activity 已被销毁";
            default: return "未知状态";
        }
    }

    private void showCurrentLifecycleState() {
        Lifecycle.State currentState = getLifecycle().getCurrentState();
        String info = String.format("当前状态: %s\n生命周期: %s",
                getStateName(currentState),
                getLifecycle().getCurrentState().name());

        Toast.makeText(this, info, Toast.LENGTH_LONG).show();
        Log.i(TAG, "查看状态: " + info);
    }

    private void showDetailedStateInfo() {
        String info = String.format("生命周期信息\n状态: %s\n观察者数量: %d\n自定义事件: %d",
                getLifecycle().getCurrentState().name(),
                getLifecycle().getCurrentState().ordinal(),
                customEventCount);

        Toast.makeText(this, info, Toast.LENGTH_LONG).show();
        Log.i(TAG, "详细状态: " + info);
    }

    // 重写生命周期方法，添加额外的日志记录
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart() 被调用");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume() 被调用");
        updateStateFromLifecycle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause() 被调用");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop() 被调用");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy() 被调用");
        Log.i(TAG, "=== MainActivity 销毁完成 ===");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart() 被调用 - Activity 重新启动");
        Toast.makeText(this, "Activity 重新启动", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("customEventCount", customEventCount);
        Log.i(TAG, "onSaveInstanceState() 被调用 - 保存事件计数: " + customEventCount);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        customEventCount = savedInstanceState.getInt("customEventCount", 0);
        Log.i(TAG, "onRestoreInstanceState() 被调用 - 恢复事件计数: " + customEventCount);
    }
}