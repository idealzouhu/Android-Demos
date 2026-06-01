package com.example.broadcast.forceoffline.base;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.broadcast.forceoffline.R;
import com.example.broadcast.forceoffline.manager.ActivityCollector;
import com.example.broadcast.forceoffline.receiver.ForceOfflineReceiver;
import com.example.broadcast.forceoffline.ui.LoginActivity;

/**
 * 所有Activity的基类
 * <p>
 * 关键点如下：
 * 1.确保每一个Activity都能监听强制下线广播，并且只有栈顶Activity会处理该广播。
 * 2.在Activity的onResume()方法中注册广播接收器，在onPause()方法中注销广播接收器。
 */
public class BaseActivity  extends AppCompatActivity {

    private ForceOfflineReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 将当前Activity加入管理器
        ActivityCollector.getInstance().addActivity(this);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        // 注册广播接收器，只有栈顶Activity才接收广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.broadcast.force.offline.FORCE_OFFLINE"); // 自定义的广播Action
        receiver = new ForceOfflineReceiver();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当Activity失去栈顶位置时，注销接收器，避免不必要的弹窗
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 从管理器中移除当前Activity
        ActivityCollector.getInstance().removeActivity(this);
    }

    // class ForceOfflineReceiver extends BroadcastReceiver {
    //     @Override
    //     public void onReceive(final Context context, Intent intent) {
    //         AlertDialog.Builder builder = new AlertDialog.Builder(context);
    //         builder.setTitle("警告")
    //                 .setMessage("您的账号已在其他设备登录，请重新登录。")
    //                 .setCancelable(false)
    //                 .setPositiveButton("确定", (dialog, which) -> {
    //                     // 强制跳转到登录界面
    //                     ActivityCollector.finishAll();
    //                     Intent loginIntent = new Intent(context, LoginActivity.class);
    //                     loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //                     startActivity(loginIntent);
    //                 });
    //         AlertDialog alertDialog = builder.create();
    //         alertDialog.show();
    //     }
    // }
}
