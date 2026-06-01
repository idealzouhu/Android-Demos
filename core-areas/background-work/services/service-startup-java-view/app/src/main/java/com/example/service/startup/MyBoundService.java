package com.example.service.startup;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MyBoundService extends Service {
    private static final String TAG = "BoundService";
    private final IBinder binder = new LocalBinder();
    private int bindCount  = 0;    // 绑定总次数

    private int activeConnections  = 0; // 当前活跃连接数

    // 内部Binder类，用于客户端与Service通信
    public class LocalBinder extends Binder {
        MyBoundService getService() {
            return MyBoundService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logMessage("MyBoundService: onCreate() - Service被创建");
    }

    @Override
    public IBinder onBind(Intent intent) {
        bindCount++;
        activeConnections++;

        logMessage(String.format(Locale.getDefault(),
                "MyBoundService: onBind() - 第%d次绑定", bindCount));
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        activeConnections--;

        logMessage("MyBoundService: onUnbind() - Service被解绑");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMessage("MyBoundService: onDestroy() - Service被销毁");
    }

    // Service提供的业务方法
    public String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = sdf.format(new Date());

        String message = String.format(Locale.getDefault(),
                "MyBoundService: 获取时间被调用 - 当前时间: %s", time);
        logMessage(message);

        return time;
    }

    public String getServiceInfo() {
        return String.format(Locale.getDefault(),
                "MyBoundService信息 - 绑定次数:%d  活跃连接: %d",
                bindCount, activeConnections);
    }

    private void logMessage(String message) {
        Log.d(TAG, message);
    }
}