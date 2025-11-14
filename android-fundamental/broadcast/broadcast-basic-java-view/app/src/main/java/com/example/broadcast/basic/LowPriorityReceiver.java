package com.example.broadcast.basic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LowPriorityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BroadcastTest", "LowPriorityReceiver onReceive. Priority should be: 50");

        // 获取上一个接收器设置的结果
        Bundle resultBundle = getResultExtras(true);
        String messageFromHigh = resultBundle.getString("result");

        Toast.makeText(context, "低优先级接收器收到消息: " + messageFromHigh, Toast.LENGTH_LONG).show();
    }
}