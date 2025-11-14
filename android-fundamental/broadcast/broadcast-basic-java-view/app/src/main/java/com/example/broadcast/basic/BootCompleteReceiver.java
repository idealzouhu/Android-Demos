package com.example.broadcast.basic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

/**
 * 设备启动完成广播接收器
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // 设备启动完成后，发送一条通知（而不是直接启动Activity，更符合Android规范）
            sendBootNotification(context);
        }
    }

    /**
     * 发送开机启动通知
     *
     * @param context 应用上下文，用于获取系统服务和构建通知
     */
    private void sendBootNotification(Context context) {
        // 创建通知渠道（Android 8.0+ 必需）
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "boot_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "启动通知", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // 创建一个点击后打开应用的PendingIntent
        Intent launchIntent = new Intent(context, MainActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE);

        // 构建并显示通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // 请确保有对应的图标资源
                .setContentTitle("系统监控App")
                .setContentText("设备已启动，点击打开应用。")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());

        Toast.makeText(context, "已发送开机启动通知", Toast.LENGTH_SHORT).show();
    }
}