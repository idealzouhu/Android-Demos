package com.example.service.startup;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 启动式 Service 案例
 *
 */
public class MyStartedService extends Service {
    private static final String TAG = "StartedService";
    private int taskCounter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        logMessage("MyStartedService: onCreate() - Service被创建");
        Toast.makeText(this, "Service被创建", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        taskCounter++;
        logMessage(String.format(Locale.getDefault(),
                "MyStartedService: onStartCommand() - 任务#%d开始执行, startId: %d",
                taskCounter, startId));

        // 模拟耗时任务
        new Thread(() -> {
            try {
                // 模拟3秒的任务
                Thread.sleep(3000);

                String message = String.format(Locale.getDefault(),
                        "任务#%d完成，耗时3秒", taskCounter);
                logMessage("MyStartedService: " + message);

                // 发送广播通知任务完成
                Intent broadcastIntent = new Intent("STARTED_SERVICE_TASK_COMPLETED");
                broadcastIntent.putExtra("task_id", taskCounter);
                sendBroadcast(broadcastIntent);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // START_STICKY: Service被杀死后会重新创建
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 启动式Service不需要绑定，返回null
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMessage("MyStartedService: onDestroy() - Service被销毁");
        Toast.makeText(this, "Service被销毁", Toast.LENGTH_SHORT).show();
    }

    private void logMessage(String message) {
        Log.d(TAG, message);
        // 这里应该发送广播或通过其他方式将日志传递给Activity
        // 简化示例，实际应用中应该通过广播或Handler传递
    }

    // 公共方法，可以被其他组件调用
    public int getTaskCount() {
        return taskCounter;
    }
}