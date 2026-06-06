package com.example.handler.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * 演示自定义工作线程的Handler
 */
public class WorkerThread extends Thread {

    private Handler workerHandler;
    private WeakReference<MainActivity> activityRef;

    public WorkerThread(MainActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    public Handler getWorkerHandler() {
        return workerHandler;
    }

    @Override
    public void run() {

        // 创建工作线程的Looper
        Looper.prepare();

        // 创建工作线程的Handler
        workerHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
            @Override
            public void handleMessage(Message msg) {
                MainActivity activity = activityRef.get();
                if (activity == null || activity.isFinishing()) {
                    return;
                }

                switch (msg.what) {
                    case MessageWhat.MSG_UPDATE_TEXT:
                        // 在工作线程处理消息
                        String data = (String) msg.obj;

                        // 模拟处理
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // 处理完成后通知主线程
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            activity.logMessage("工作线程处理完成: " + data);
                            Toast.makeText(activity,
                                    "处理完成: " + data,
                                    Toast.LENGTH_SHORT).show();
                        });
                        break;
                }
            }
        };

        // 启动工作线程的Looper
        Looper.loop();
    }

    public void quit() {
        if (workerHandler != null) {
            workerHandler.getLooper().quitSafely();
        }
    }
}