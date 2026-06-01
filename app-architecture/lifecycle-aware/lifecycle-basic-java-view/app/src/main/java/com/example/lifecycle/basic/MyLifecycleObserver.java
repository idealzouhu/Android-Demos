package com.example.lifecycle.basic;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class MyLifecycleObserver implements DefaultLifecycleObserver {

    private static final String TAG = "LifecycleObserver";
    private TextView stateTextView;
    private TextView descriptionTextView;

    public MyLifecycleObserver(TextView stateTextView, TextView descriptionTextView) {
        this.stateTextView = stateTextView;
        this.descriptionTextView = descriptionTextView;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        Log.i(TAG, "onCreate() 被调用 - Activity 正在创建");
        updateUI("Created", "Activity 已创建");
        Toast.makeText(stateTextView.getContext(), "Activity 已创建", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        Log.i(TAG, "onStart() 被调用 - Activity 变为可见状态");
        updateUI("Started", "Activity 可见但未获取焦点");
        Toast.makeText(stateTextView.getContext(), "Activity 已开始", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        Log.i(TAG, "onResume() 被调用 - Activity 获得焦点，可交互");
        updateUI("Resumed", "Activity 获得焦点，可交互");
        Toast.makeText(stateTextView.getContext(), "Activity 已恢复", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        Log.i(TAG, "onPause() 被调用 - Activity 失去焦点");
        updateUI("Paused", "Activity 失去焦点");
        Toast.makeText(stateTextView.getContext(), "Activity 已暂停", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        Log.i(TAG, "onStop() 被调用 - Activity 完全不可见");
        updateUI("Stopped", "Activity 不可见");
        Toast.makeText(stateTextView.getContext(), "Activity 已停止", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        Log.i(TAG, "onDestroy() 被调用 - Activity 正在销毁");
        updateUI("Destroyed", "Activity 正在销毁");
        // 注意：不能在此处显示 Toast
    }

    private void updateUI(String state, String description) {
        if (stateTextView != null) {
            stateTextView.setText(state);
            updateStateColor(state);
        }

        if (descriptionTextView != null) {
            descriptionTextView.setText(description);
        }
    }

    private void updateStateColor(String state) {
        int colorId = android.R.color.black;

        switch (state) {
            case "Created":
                colorId = R.color.state_created;
                break;
            case "Started":
                colorId = R.color.state_started;
                break;
            case "Resumed":
                colorId = R.color.state_resumed;
                break;
            case "Paused":
                colorId = R.color.state_paused;
                break;
            case "Stopped":
                colorId = R.color.state_stopped;
                break;
            case "Destroyed":
                colorId = R.color.state_destroyed;
                break;
        }

        stateTextView.setTextColor(stateTextView.getContext().getColor(colorId));
    }

    /**
     * 更新流程图中的状态指示
     */
    public void updateLifecycleFlow(String state) {
        // 重置所有状态指示
        resetAllStateIndicators();

        // 激活当前状态
        switch (state) {
            case "Created":
                highlightState(R.id.tvStateCreated);
                break;
            case "Started":
                highlightState(R.id.tvStateStarted);
                break;
            case "Resumed":
                highlightState(R.id.tvStateResumed);
                break;
            case "Paused":
                highlightState(R.id.tvStatePaused);
                break;
            case "Stopped":
                highlightState(R.id.tvStateStopped);
                break;
        }
    }

    private void resetAllStateIndicators() {
        // 在实际实现中，这里会重置所有状态指示器的背景
    }

    private void highlightState(int stateViewId) {
        // 在实际实现中，这里会高亮指定的状态指示器
    }
}