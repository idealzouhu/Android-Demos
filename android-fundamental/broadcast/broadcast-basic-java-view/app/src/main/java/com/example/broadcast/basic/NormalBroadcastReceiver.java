package com.example.broadcast.basic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


/**
 * 普通广播接收器
 * 用于接收异步发送的普通广播消息
 */
public class NormalBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "NormalBroadcastReceiver";

    /**
     * 当接收到广播时自动调用此方法
     *
     * @param context 上下文对象
     * @param intent 包含广播数据的Intent对象
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取广播的Action用于识别广播类型
        String action = intent.getAction();

        if ("com.example.broadcast.basic.ACTION_NORMAL_BROADCAST".equals(action)) {
            // 从Intent中提取附加数据
            String message = intent.getStringExtra("message");
            long timestamp = intent.getLongExtra("timestamp", 0);

            // 处理接收到的广播数据
            Log.d(TAG, "接收到普通广播: " + message + ", 时间戳: " + timestamp);

            // 显示Toast通知用户
            Toast.makeText(context, "收到广播: " + message, Toast.LENGTH_SHORT).show();

            // 可以在这里执行其他操作，如更新UI、保存数据等
            // 注意：onReceive()方法运行在主线程，不要执行耗时操作
        }

        // else if ("com.example.broadcast.basic.ACTION_BOOT_COMPLETED".equals(action)) {
        //     // 处理设备启动完成广播
        //     Toast.makeText(context, "设备启动完成", Toast.LENGTH_SHORT).show();
        // }
    }

}