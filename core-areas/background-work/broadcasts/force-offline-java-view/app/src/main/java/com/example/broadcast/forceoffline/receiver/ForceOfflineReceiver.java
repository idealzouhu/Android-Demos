package com.example.broadcast.forceoffline.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.broadcast.forceoffline.manager.ActivityCollector;
import com.example.broadcast.forceoffline.ui.LoginActivity;

import java.util.Objects;

public class ForceOfflineReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("ForceOfflineReceiver", "收到强制下线广播");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("警告")
                .setMessage("您的账号已在其他设备登录，将被强制下线。")
                .setCancelable(false)
                .setPositiveButton("确定", (dialog, which) -> {
                    // 1. 销毁所有Activity
                    ActivityCollector.getInstance().finishAll();
                    // 2. 跳转到登录界面，并清除当前任务栈
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(loginIntent);
                });

        // 获取对话框并设置其窗口类型，确保它可以在广播接收器中正常显示
        AlertDialog alertDialog = builder.create();
        // Objects.requireNonNull(alertDialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        alertDialog.show();
    }
}
