package com.example.service.startup;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.util.Locale;

public class HybridService extends Service {
    private static final String TAG = "HybridService";
    private final IBinder binder = new HybridBinder();
    private int downloadProgress = 0;
    private boolean isDownloading = false;

    public class HybridBinder extends Binder {
        HybridService getService() {
            return HybridService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logMessage("HybridService: onCreate() - Service被创建");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logMessage(String.format(Locale.getDefault(),
                "HybridService: onStartCommand() - 启动任务, startId: %d", startId));

        // 启动独立的后台任务
        if (intent != null && "start_download".equals(intent.getAction())) {
            startDownloadTask(startId);
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        logMessage("HybridService: onBind() - Service被绑定");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        logMessage("HybridService: onUnbind() - Service被解绑");
        return true; // 返回true表示希望重新绑定
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        logMessage("HybridService: onRebind() - Service被重新绑定");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMessage("HybridService: onDestroy() - Service被销毁");
        isDownloading = false;
    }

    // 启动式任务：模拟下载
    private void startDownloadTask(int startId) {
        if (isDownloading) {
            logMessage("HybridService: 下载任务正在进行中...");
            return;
        }

        isDownloading = true;
        downloadProgress = 0;

        new Thread(() -> {
            try {
                for (int i = 0; i <= 100 && isDownloading; i += 10) {
                    downloadProgress = i;
                    logMessage(String.format(Locale.getDefault(),
                            "HybridService: 下载进度: %d%%", i));

                    // 发送广播通知进度
                    Intent progressIntent = new Intent("HYBRID_SERVICE_PROGRESS");
                    progressIntent.putExtra("progress", i);
                    sendBroadcast(progressIntent);

                    Thread.sleep(1000);
                }

                if (isDownloading) {
                    logMessage("HybridService: 下载完成");

                    // 发送完成广播
                    Intent completeIntent = new Intent("HYBRID_SERVICE_COMPLETE");
                    sendBroadcast(completeIntent);
                } else {
                    logMessage("HybridService: 下载被取消");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                isDownloading = false;

                // 停止服务
                stopSelf(startId);
            }
        }).start();
    }

    // 绑定式方法：获取下载进度
    public int getDownloadProgress() {
        return downloadProgress;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void cancelDownload() {
        isDownloading = false;
        logMessage("HybridService: 下载被取消");
    }

    private void logMessage(String message) {
        Log.d(TAG, message);
    }
}