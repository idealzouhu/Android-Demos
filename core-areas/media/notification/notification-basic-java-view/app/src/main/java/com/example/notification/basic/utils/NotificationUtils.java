package com.example.notification.basic.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import java.util.List;
import android.util.Log;

import com.example.notification.basic.R;

/**
 * 通知工具类
 * 统一管理应用的所有通知发送
 */
public class NotificationUtils {

    private static final String TAG = "NotificationUtils";

    // 单例模式
    private static volatile NotificationUtils instance;
    private final NotificationManager notificationManager;
    private final Context context;

    // 添加回复相关的常量
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    public static final String ACTION_REPLY = "com.example.notification.ACTION_REPLY";
    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";

    private NotificationUtils(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static synchronized NotificationUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (NotificationUtils.class) {
                if (instance == null) {
                    instance = new NotificationUtils(context);
                }
            }
        }
        return instance;
    }

    // ==================== 公共方法 ====================

    /**
     * 发送通知
     * @param config 通知配置
     * @param notificationId 通知ID
     */
    public void sendNotification(@NonNull NotificationConfig config, int notificationId) {
        try {
            Notification notification = buildNotification(config);
            notificationManager.notify(notificationId, notification);
            Log.d(TAG, "通知发送成功, ID: " + notificationId);
        } catch (Exception e) {
            Log.e(TAG, "发送通知失败: " + e.getMessage(), e);
        }
    }

    /**
     * 发送简单通知
     */
    public void sendSimpleNotification(String title, String content, int notificationId) {
        NotificationConfig config = new NotificationConfig.Builder(
                NotificationChannelManager.CHANNEL_ID_BASIC,
                R.drawable.ic_notification,
                title,
                content
        ).build();

        sendNotification(config, notificationId);
    }

    /**
     * 发送重要通知
     */
    public void sendImportantNotification(String title, String content, int notificationId) {
        NotificationConfig config = new NotificationConfig.Builder(
                NotificationChannelManager.CHANNEL_ID_IMPORTANT,
                R.drawable.ic_notification,
                title,
                content
        ).setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        sendNotification(config, notificationId);
    }

    /**
     * 发送进度通知
     */
    public void sendProgressNotification(String title, int progress, int maxProgress,
                                         boolean indeterminate, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, NotificationChannelManager.CHANNEL_ID_PROGRESS
        )
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setProgress(maxProgress, progress, indeterminate)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager.notify(notificationId, builder.build());
    }

    /**
     * 发送大文本通知
     */
    public void sendBigTextNotification(String title, String summary,
                                        String bigText, int notificationId) {
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle()
                .bigText(bigText)
                .setSummaryText(summary);

        NotificationConfig config = new NotificationConfig.Builder(
                NotificationChannelManager.CHANNEL_ID_BASIC,
                R.drawable.ic_notification,
                title,
                summary
        ).setStyle(style)
                .build();

        sendNotification(config, notificationId);
    }

    /**
     * 发送带操作按钮的通知
     */
    public void sendActionNotification(String title, String content,
                                       List<NotificationCompat.Action> actions,
                                       int notificationId) {
        NotificationConfig config = new NotificationConfig.Builder(
                NotificationChannelManager.CHANNEL_ID_IMPORTANT,
                R.drawable.ic_notification,
                title,
                content
        ).setActions(actions)
                .setAutoCancel(false)
                .setOngoing(false)
                .build();

        sendNotification(config, notificationId);
    }

    /**
     * 发送可回复的通知
     */
    public void sendReplyNotification(String title, String content,
                                      String replyLabel, String replyKey,
                                      PendingIntent replyIntent, int notificationId) {
        RemoteInput remoteInput = new RemoteInput.Builder(replyKey)
                .setLabel(replyLabel)
                .build();

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_notification,
                "回复",
                replyIntent
        ).addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        NotificationConfig config = new NotificationConfig.Builder(
                NotificationChannelManager.CHANNEL_ID_IMPORTANT,
                R.drawable.ic_notification,
                title,
                content
        ).setActions(java.util.Collections.singletonList(replyAction))
                .setAutoCancel(true)
                .build();

        sendNotification(config, notificationId);
    }

    /**
     * 发送可回复的通知（增强版）
     */
    public void sendReplyNotification(String title, String content,
                                      String replyLabel, int notificationId,
                                      PendingIntent replyIntent) {
        // 使用统一的KEY
        sendReplyNotification(title, content, replyLabel, KEY_TEXT_REPLY,
                replyIntent, notificationId);
    }

    /**
     * 创建回复接收器的Intent
     */
    public Intent createReplyReceiverIntent(int notificationId) {
        Intent intent = new Intent(ACTION_REPLY);
        intent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        return intent;
    }

    /**
     * 创建回复PendingIntent
     */
    public PendingIntent createReplyPendingIntent(int notificationId, int requestCode) {
        Intent intent = createReplyReceiverIntent(notificationId);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_MUTABLE; // 回复通知需要MUTABLE
        }

        return PendingIntent.getBroadcast(context, requestCode, intent, flags);
    }


    /**
     * 发送大图片通知
     */
    public void sendBigPictureNotification(String title, String content,
                                           int bigPictureResId, int notificationId) {
        Bitmap bigPicture = BitmapFactory.decodeResource(context.getResources(), bigPictureResId);

        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                .bigPicture(bigPicture)
                .setSummaryText(content);

        NotificationConfig config = new NotificationConfig.Builder(
                NotificationChannelManager.CHANNEL_ID_BASIC,
                R.drawable.ic_notification,
                title,
                content
        ).setStyle(style)
                .build();

        sendNotification(config, notificationId);
    }

    // ==================== 管理方法 ====================

    /**
     * 取消指定通知
     */
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
        Log.d(TAG, "取消通知, ID: " + notificationId);
    }

    /**
     * 取消所有通知
     */
    public void cancelAllNotifications() {
        notificationManager.cancelAll();
        Log.d(TAG, "取消所有通知");
    }

    /**
     * 检查通知是否被允许
     */
    public boolean areNotificationsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return notificationManager.areNotificationsEnabled();
        }
        return true; // Android 7.0以下默认允许
    }

    /**
     * 检查特定渠道的通知是否被允许
     */
    public boolean areChannelNotificationsEnabled(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel != null) {
                int importance = channel.getImportance();
                return importance != NotificationManager.IMPORTANCE_NONE;
            }
        }
        return areNotificationsEnabled();
    }

    // ==================== 私有方法 ====================

    /**
     * 构建通知对象
     */
    private Notification buildNotification(NotificationConfig config) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, config.getChannelId())
                .setSmallIcon(config.getSmallIcon())
                .setContentTitle(config.getTitle())
                .setContentText(config.getContent())
                .setAutoCancel(config.isAutoCancel())
                .setOngoing(config.isOngoing())
                .setPriority(config.getPriority())
                .setOnlyAlertOnce(config.isOnlyAlertOnce());

        // 设置大图标
        if (config.getLargeIcon() != 0) {
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), config.getLargeIcon());
            builder.setLargeIcon(largeIcon);
        }

        // 设置PendingIntent
        if (config.getPendingIntent() != null) {
            builder.setContentIntent(config.getPendingIntent());
        }

        // 设置样式
        if (config.getStyle() != null) {
            builder.setStyle(config.getStyle());
        }

        // 设置操作按钮
        if (config.getActions() != null && !config.getActions().isEmpty()) {
            for (NotificationCompat.Action action : config.getActions()) {
                builder.addAction(action);
            }
        }

        // 设置Ticker文本
        if (config.getTickerText() != null) {
            builder.setTicker(config.getTickerText());
        }

        // 设置颜色
        if (config.getColor() != 0) {
            builder.setColor(config.getColor());
        }

        return builder.build();
    }

    /**
     * 创建默认的PendingIntent
     */
    public PendingIntent createDefaultPendingIntent(Class<?> targetClass, int requestCode) {
        Intent intent = new Intent(context, targetClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        flags |= PendingIntent.FLAG_IMMUTABLE;

        return PendingIntent.getActivity(context, requestCode, intent, flags);
    }
}