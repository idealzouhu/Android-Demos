package com.example.notification.basic.utils;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Arrays;
import java.util.List;

/**
 * 通知渠道管理器
 * 统一管理所有通知渠道
 */
public class NotificationChannelManager {

    // 渠道ID常量
    public static final String CHANNEL_ID_BASIC = "channel_basic";
    public static final String CHANNEL_ID_IMPORTANT = "channel_important";
    public static final String CHANNEL_ID_PROGRESS = "channel_progress";
    public static final String CHANNEL_ID_MEDIA = "channel_media";

    // 渠道组ID
    public static final String CHANNEL_GROUP_ID = "notification_demo_group";
    public static final String CHANNEL_GROUP_NAME = "通知演示组";

    private static volatile NotificationChannelManager instance;
    private final NotificationManager notificationManager;
    private final Context context;

    private NotificationChannelManager(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static synchronized NotificationChannelManager getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationChannelManager(context);
        }
        return instance;
    }

    /**
     * 创建所有通知渠道
     * 需要在应用启动时调用
     */
    public void createAllChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroup();

            // 创建所有渠道
            createBasicChannel();
            createImportantChannel();
            createProgressChannel();
            createMediaChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannelGroup() {
        NotificationChannelGroup group = new NotificationChannelGroup(
                CHANNEL_GROUP_ID,
                CHANNEL_GROUP_NAME
        );
        notificationManager.createNotificationChannelGroup(group);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createBasicChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_BASIC,
                "基本通知",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("用于发送普通通知");
        channel.setGroup(CHANNEL_GROUP_ID);
        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{0, 300, 200, 300});
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createImportantChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_IMPORTANT,
                "重要通知",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("用于发送重要通知");
        channel.setGroup(CHANNEL_GROUP_ID);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{0, 100, 100, 100, 100, 100});
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createProgressChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_PROGRESS,
                "进度通知",
                NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription("用于显示进度通知");
        channel.setGroup(CHANNEL_GROUP_ID);
        channel.setShowBadge(false);
        channel.enableVibration(false);
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createMediaChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_MEDIA,
                "媒体通知",
                NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription("用于媒体播放控制");
        channel.setGroup(CHANNEL_GROUP_ID);
        channel.enableVibration(false);
        channel.setSound(null, null);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * 获取所有渠道ID列表
     */
    public List<String> getAllChannelIds() {
        return Arrays.asList(
                CHANNEL_ID_BASIC,
                CHANNEL_ID_IMPORTANT,
                CHANNEL_ID_PROGRESS,
                CHANNEL_ID_MEDIA
        );
    }

    /**
     * 检查渠道是否存在
     */
    public boolean channelExists(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            return channel != null;
        }
        return true; // Android 8.0以下不需要渠道
    }

    /**
     * 删除渠道
     */
    public void deleteChannel(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId);
        }
    }
}