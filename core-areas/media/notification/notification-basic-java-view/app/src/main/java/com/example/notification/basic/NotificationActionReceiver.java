package com.example.notification.basic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * 通知操作按钮广播接收器
 * 处理通知栏中操作按钮的点击事件
 */
public class NotificationActionReceiver extends BroadcastReceiver {

    /**
     * 接收广播消息
     * 当用户点击通知中的操作按钮时调用
     *
     * @param context 上下文
     * @param intent  包含操作信息的Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationActionReceiver", "接收到广播消息");
        String action = intent.getAction();
        int actionId = intent.getIntExtra("action_id", 0);

        if (action != null) {
            switch (action) {
                case "ACTION_BUTTON_1":
                    Toast.makeText(context, "播放按钮被点击", Toast.LENGTH_SHORT).show();
                    break;
                case "ACTION_BUTTON_2":
                    Toast.makeText(context, "暂停按钮被点击", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}