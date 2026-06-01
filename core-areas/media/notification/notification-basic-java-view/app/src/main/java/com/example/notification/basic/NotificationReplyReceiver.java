package com.example.notification.basic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.RemoteInput;

import android.util.Log;
import android.widget.Toast;

import com.example.notification.basic.utils.NotificationUtils;

import java.util.Locale;

/**
 * 回复接收器
 * <p>
 * 回复接收器处理用户回复的广播
 */
public class NotificationReplyReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReplyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "收到回复广播");

        if (intent == null || intent.getAction() == null) {
            Log.w(TAG, "intent或action为null");
            return;
        }

        // 检查是否是回复广播
        if (NotificationUtils.ACTION_REPLY.equals(intent.getAction())) {
            Log.d(TAG, "处理回复action");

            // 获取通知ID
            int notificationId = intent.getIntExtra(
                    NotificationUtils.EXTRA_NOTIFICATION_ID,
                    -1
            );
            Log.d(TAG, "通知ID: " + notificationId);

            // 从RemoteInput获取回复内容
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                String replyText = remoteInput.getString(NotificationUtils.KEY_TEXT_REPLY);
                Log.d(TAG, "回复文本: " + replyText);

                if (replyText != null && !replyText.trim().isEmpty()) {
                    // 处理回复
                    handleReply(context, notificationId, replyText);
                } else {
                    Log.w(TAG, "回复内容为空");
                }
            } else {
                Log.w(TAG, "RemoteInput为null");
            }

            // 可以取消通知，也可以更新通知显示回复内容
            if (notificationId != -1) {
                // 选项1：取消通知
                NotificationUtils.getInstance(context).cancelNotification(notificationId);

                // 选项2：更新通知显示回复已发送
                // sendReplySentNotification(context, notificationId, replyText);
            }
        } else {
            Log.w(TAG, "未知的action: " + intent.getAction());
        }
    }

    private void handleReply(Context context, int notificationId, String replyText) {
        // 1. 显示Toast提示
        String message = String.format(
                Locale.getDefault(),
                "回复已收到\n通知ID: %d\n内容: %s",
                notificationId,
                replyText
        );
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        // 2. 发送广播通知其他组件
        Intent broadcastIntent = new Intent("com.example.notification.REPLY_RECEIVED");
        broadcastIntent.putExtra("notification_id", notificationId);
        broadcastIntent.putExtra("reply_text", replyText);
        broadcastIntent.putExtra("timestamp", System.currentTimeMillis());
        context.sendBroadcast(broadcastIntent);

        // 3. 记录日志
        Log.i(TAG, "处理回复 - ID: " + notificationId + ", 内容: " + replyText);

        // 4. 可以在这里保存到数据库
        // saveReplyToDatabase(context, notificationId, replyText);

        // 5. 可以发送到服务器
        // sendReplyToServer(context, notificationId, replyText);
    }

    /**
     * 可选：发送回复已发送的确认通知
     */
    private void sendReplySentNotification(Context context, int originalNotificationId, String replyText) {
        NotificationUtils utils = NotificationUtils.getInstance(context);

        // 创建一个新通知显示回复已发送
        String shortReply = replyText.length() > 20 ?
                replyText.substring(0, 20) + "..." : replyText;

        utils.sendSimpleNotification(
                "回复已发送",
                "回复内容: " + shortReply,
                originalNotificationId + 1000 // 使用新的通知ID
        );
    }
}