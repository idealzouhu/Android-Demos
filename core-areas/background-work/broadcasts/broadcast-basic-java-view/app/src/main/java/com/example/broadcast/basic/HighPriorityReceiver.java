package com.example.broadcast.basic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HighPriorityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BroadcastTest", "HighPriorityReceiver onReceive. Priority should be: 100");
        String originalMsg = intent.getStringExtra("base_message");
        String newMsg = originalMsg + " [高优先级处理完毕]";

        // 将处理结果存入Bundle，传递给下一个接收器
        Bundle resultBundle = new Bundle();
        resultBundle.putString("result", newMsg);
        setResultExtras(resultBundle);

        Toast.makeText(context, "高优先级接收器：首先处理", Toast.LENGTH_SHORT).show();

        // 可以在此调用 abortBroadcast() 来中止广播，阻止低优先级接收器接收
        // abortBroadcast();
    }
}