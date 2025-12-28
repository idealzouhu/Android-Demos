package com.example.foregroundservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.util.Locale;
import java.util.Random;

public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";

    // 通知相关
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    // 广播 Action
    public static final String ACTION_PROGRESS_UPDATE = "com.example.foregroundservice.PROGRESS_UPDATE";
    public static final String ACTION_DOWNLOAD_STATUS = "com.example.foregroundservice.DOWNLOAD_STATUS";
    public static final String ACTION_SERVICE_STARTED = "com.example.foregroundservice.SERVICE_STARTED";
    public static final String ACTION_SERVICE_STOPPED = "com.example.foregroundservice.SERVICE_STOPPED";

    // Intent Action
    public static final String ACTION_START_FOREGROUND_SERVICE = "START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE";
    public static final String ACTION_START_DOWNLOAD = "START_DOWNLOAD";
    public static final String ACTION_PAUSE_DOWNLOAD = "PAUSE_DOWNLOAD";
    public static final String ACTION_RESUME_DOWNLOAD = "RESUME_DOWNLOAD";

    // Extra keys
    public static final String EXTRA_PROGRESS = "progress";
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_INFO = "info";
    public static final String EXTRA_FILE_URL = "file_url";
    public static final String EXTRA_FILE_SIZE = "file_size";

    // 下载状态
    private boolean isDownloading = false;
    private boolean isPaused = false;
    private int downloadProgress = 0;
    private int fileSize = 100; // MB

    private Handler handler;
    private Runnable downloadRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "ForegroundService onCreate()");

        handler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            Log.i(TAG, "onStartCommand, action: " + action);

            if (action != null) {
                switch (action) {
                    case ACTION_START_FOREGROUND_SERVICE:
                        startForegroundService();
                        break;

                    case ACTION_STOP_FOREGROUND_SERVICE:
                        stopForegroundService();
                        break;

                    case ACTION_START_DOWNLOAD:
                        fileSize = intent.getIntExtra(EXTRA_FILE_SIZE, 100);
                        startDownload();
                        break;

                    case ACTION_PAUSE_DOWNLOAD:
                        pauseDownload();
                        break;

                    case ACTION_RESUME_DOWNLOAD:
                        resumeDownload();
                        break;
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // 不提供绑定功能
    }

    private void startForegroundService() {
        // 创建通知
        Notification notification = createNotification("服务准备就绪", "点击返回应用");

        // 启动为前台服务
        startForeground(NOTIFICATION_ID, notification);

        // 发送广播通知服务已启动
        sendBroadcast(new Intent(ACTION_SERVICE_STARTED));

        Log.i(TAG, "前台服务已启动");
    }

    private void stopForegroundService() {
        stopDownload();
        stopForeground(true);
        stopSelf();

        // 发送广播通知服务已停止
        sendBroadcast(new Intent(ACTION_SERVICE_STOPPED));

        Log.i(TAG, "前台服务已停止");
    }

    private void startDownload() {
        if (isDownloading) {
            sendStatusBroadcast("错误", "下载已在运行");
            return;
        }

        isDownloading = true;
        isPaused = false;
        downloadProgress = 0;

        // 更新通知
        updateNotification("开始下载", "0% 完成");

        // 发送状态广播
        sendStatusBroadcast("下载中", "开始下载文件");

        // 模拟下载任务
        startDownloadTask();
    }

    private void pauseDownload() {
        isPaused = true;
        sendStatusBroadcast("暂停", "下载已暂停");
        updateNotification("下载暂停", downloadProgress + "% 完成");
    }

    private void resumeDownload() {
        isPaused = false;
        sendStatusBroadcast("下载中", "继续下载");
        startDownloadTask();
    }

    private void stopDownload() {
        isDownloading = false;
        isPaused = false;

        if (downloadRunnable != null && handler != null) {
            handler.removeCallbacks(downloadRunnable);
        }
    }

    private void startDownloadTask() {
        if (downloadRunnable != null && handler != null) {
            handler.removeCallbacks(downloadRunnable);
        }

        downloadRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isDownloading || isPaused) {
                    return;
                }

                // 模拟下载进度
                downloadProgress += new Random().nextInt(5) + 1;
                if (downloadProgress > 100) {
                    downloadProgress = 100;
                }

                // 更新UI
                sendProgressBroadcast(downloadProgress);
                updateNotification("正在下载", downloadProgress + "% 完成");

                if (downloadProgress < 100) {
                    // 继续下载
                    if (handler != null) {
                        handler.postDelayed(this, 500);
                    }
                } else {
                    // 下载完成
                    isDownloading = false;
                    sendStatusBroadcast("完成", "文件下载完成");
                    updateNotification("下载完成", "100% 完成");
                }
            }
        };

        if (handler != null) {
            handler.post(downloadRunnable);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "前台服务通知",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("显示前台服务的运行状态");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification(String title, String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }

    private void updateNotification(String title, String text) {
        Notification notification = createNotification(title, text);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void sendProgressBroadcast(int progress) {
        Intent intent = new Intent(ACTION_PROGRESS_UPDATE);
        intent.putExtra(EXTRA_PROGRESS, progress);
        sendBroadcast(intent);
    }

    private void sendStatusBroadcast(String status, String info) {
        Intent intent = new Intent(ACTION_DOWNLOAD_STATUS);
        intent.putExtra(EXTRA_STATUS, status);
        intent.putExtra(EXTRA_INFO, info);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDownload();
        Log.i(TAG, "ForegroundService onDestroy()");
    }
}