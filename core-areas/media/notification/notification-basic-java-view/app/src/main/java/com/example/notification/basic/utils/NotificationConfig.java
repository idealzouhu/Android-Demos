package com.example.notification.basic.utils;

import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Style;
import java.util.List;

/**
 * 通知配置类
 * <p>
 * 用于统一配置通知的各项参数
 */
public class NotificationConfig {

    // 必填字段
    private String channelId;
    private int smallIcon;
    private String title;
    private String content;

    // 可选字段
    private int largeIcon = 0;
    private PendingIntent pendingIntent = null;
    private boolean autoCancel = true;
    private boolean ongoing = false;
    private int priority = NotificationCompat.PRIORITY_DEFAULT;
    private Style style = null;
    private List<NotificationCompat.Action> actions = null;
    private String tickerText = null;
    private int color = 0;
    private boolean onlyAlertOnce = false;

    // 私有构造函数，通过Builder构建
    private NotificationConfig(Builder builder) {
        this.channelId = builder.channelId;
        this.smallIcon = builder.smallIcon;
        this.title = builder.title;
        this.content = builder.content;
        this.largeIcon = builder.largeIcon;
        this.pendingIntent = builder.pendingIntent;
        this.autoCancel = builder.autoCancel;
        this.ongoing = builder.ongoing;
        this.priority = builder.priority;
        this.style = builder.style;
        this.actions = builder.actions;
        this.tickerText = builder.tickerText;
        this.color = builder.color;
        this.onlyAlertOnce = builder.onlyAlertOnce;
    }

    // Getter 方法
    public String getChannelId() { return channelId; }
    public int getSmallIcon() { return smallIcon; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getLargeIcon() { return largeIcon; }
    public PendingIntent getPendingIntent() { return pendingIntent; }
    public boolean isAutoCancel() { return autoCancel; }
    public boolean isOngoing() { return ongoing; }
    public int getPriority() { return priority; }
    public Style getStyle() { return style; }
    public List<NotificationCompat.Action> getActions() { return actions; }
    public String getTickerText() { return tickerText; }
    public int getColor() { return color; }
    public boolean isOnlyAlertOnce() { return onlyAlertOnce; }

    /**
     * Builder 模式
     */
    public static class Builder {
        // 必填字段
        private String channelId;
        private int smallIcon;
        private String title;
        private String content;

        // 可选字段
        private int largeIcon = 0;
        private PendingIntent pendingIntent = null;
        private boolean autoCancel = true;
        private boolean ongoing = false;
        private int priority = NotificationCompat.PRIORITY_DEFAULT;
        private Style style = null;
        private List<NotificationCompat.Action> actions = null;
        private String tickerText = null;
        private int color = 0;
        private boolean onlyAlertOnce = false;

        public Builder(String channelId, int smallIcon, String title, String content) {
            this.channelId = channelId;
            this.smallIcon = smallIcon;
            this.title = title;
            this.content = content;
        }

        public Builder setLargeIcon(int largeIcon) {
            this.largeIcon = largeIcon;
            return this;
        }

        public Builder setPendingIntent(PendingIntent pendingIntent) {
            this.pendingIntent = pendingIntent;
            return this;
        }

        public Builder setAutoCancel(boolean autoCancel) {
            this.autoCancel = autoCancel;
            return this;
        }

        public Builder setOngoing(boolean ongoing) {
            this.ongoing = ongoing;
            return this;
        }

        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder setStyle(Style style) {
            this.style = style;
            return this;
        }

        public Builder setActions(List<NotificationCompat.Action> actions) {
            this.actions = actions;
            return this;
        }

        public Builder setTickerText(String tickerText) {
            this.tickerText = tickerText;
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setOnlyAlertOnce(boolean onlyAlertOnce) {
            this.onlyAlertOnce = onlyAlertOnce;
            return this;
        }

        public NotificationConfig build() {
            if (channelId == null || channelId.isEmpty()) {
                throw new IllegalArgumentException("channelId不能为空");
            }
            if (title == null || title.isEmpty()) {
                throw new IllegalArgumentException("title不能为空");
            }
            if (content == null || content.isEmpty()) {
                throw new IllegalArgumentException("content不能为空");
            }
            return new NotificationConfig(this);
        }
    }
}