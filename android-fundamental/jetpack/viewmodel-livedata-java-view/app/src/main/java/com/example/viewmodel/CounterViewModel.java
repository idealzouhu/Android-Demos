package com.example.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CounterViewModel extends ViewModel {
    private final MutableLiveData<Integer> counter = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isResetEnabled = new MutableLiveData<>();

    public CounterViewModel() {
        // 初始化数据
        counter.setValue(0);
        message.setValue("欢迎使用计数器");
        isResetEnabled.setValue(false);
    }

    public LiveData<Integer> getCounter() {
        return counter;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<Boolean> getIsResetEnabled() {
        return isResetEnabled;
    }

    public void increment() {
        int currentValue = counter.getValue() != null ? counter.getValue() : 0;
        counter.setValue(currentValue + 1);
        updateMessage(currentValue + 1);
        updateResetButtonState(currentValue + 1);
    }

    public void decrement() {
        int currentValue = counter.getValue() != null ? counter.getValue() : 0;
        counter.setValue(currentValue - 1);
        updateMessage(currentValue - 1);
        updateResetButtonState(currentValue - 1);
    }

    public void reset() {
        counter.setValue(0);
        message.setValue("计数器已重置");
        isResetEnabled.setValue(false);
    }

    private void updateMessage(int value) {
        if (value > 0) {
            message.setValue("当前计数: " + value + " (正数)");
        } else if (value < 0) {
            message.setValue("当前计数: " + value + " (负数)");
        } else {
            message.setValue("当前计数: 0 (零)");
        }
    }

    private void updateResetButtonState(int value) {
        isResetEnabled.setValue(value != 0);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // 清理资源
        message.setValue("ViewModel 已被清理");
    }
}