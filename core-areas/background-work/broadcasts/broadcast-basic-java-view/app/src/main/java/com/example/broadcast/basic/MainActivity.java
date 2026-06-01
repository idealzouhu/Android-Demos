package com.example.broadcast.basic;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTimeTextView;
    private TimeTickReceiver mTimeTickReceiver; // 动态注册的接收器
    private NormalBroadcastReceiver normalReceiver;
    private HighPriorityReceiver highPriorityReceiver;
    private LowPriorityReceiver lowPriorityReceiver;

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

        initViews();
        registerTimeReceiver();             // 动态注册时间监听
        registerNormalBroadcastReceiver();  // 注册普通广播接收器
        registerOrderedBroadcastReceivers(); // 注册有序广播接收器
    }

    private void initViews() {
        mTimeTextView = findViewById(R.id.tv_time);

        // 发送普通广播
        findViewById(R.id.btn_send_normal).setOnClickListener(v -> {
            Intent intent = new Intent("com.example.broadcast.basic.ACTION_NORMAL_BROADCAST");
            intent.putExtra("message", "这是一条来自普通广播的消息！");
            intent.setPackage(getPackageName());    // 指定接收的包名
            sendBroadcast(intent); // 关键方法：发送普通广播
        });

        // 发送有序广播
        findViewById(R.id.btn_send_ordered).setOnClickListener(v -> {
            Intent intent = new Intent("com.example.broadcast.basic.ACTION_ORDERED_BROADCAST");
            intent.putExtra("base_message", "请依次处理：");
            intent.setPackage(getPackageName());
            sendOrderedBroadcast(intent, null); // 关键方法：发送有序广播
        });
    }

    /**
     * 动态注册广播接收器（监听系统时间变化）
     */
    private void registerTimeReceiver() {
        mTimeTickReceiver = new TimeTickReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK); // 系统每分钟发送的广播
        registerReceiver(mTimeTickReceiver, filter);
    }

    /**
     * 动态注册普通广播接收器
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private  void  registerNormalBroadcastReceiver(){
        // 创建广播接收器实例
        normalReceiver = new NormalBroadcastReceiver();

        // 创建 IntentFilter
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.broadcast.basic.ACTION_NORMAL_BROADCAST");

        // 动态注册
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(normalReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(normalReceiver, filter);
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerOrderedBroadcastReceivers() {
        // 创建高优先级接收器实例
        highPriorityReceiver = new HighPriorityReceiver();
        // 创建低优先级接收器实例
        lowPriorityReceiver = new LowPriorityReceiver();

        // 创建 IntentFilter 并设置优先级
        IntentFilter highPriorityFilter = new IntentFilter();
        highPriorityFilter.addAction("com.example.broadcast.basic.ACTION_ORDERED_BROADCAST");
        highPriorityFilter.setPriority(100); // 设置高优先级

        IntentFilter lowPriorityFilter = new IntentFilter();
        lowPriorityFilter.addAction("com.example.broadcast.basic.ACTION_ORDERED_BROADCAST");
        lowPriorityFilter.setPriority(50); // 设置低优先级

        // 动态注册接收器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(highPriorityReceiver, highPriorityFilter, Context.RECEIVER_EXPORTED);
            registerReceiver(lowPriorityReceiver, lowPriorityFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(highPriorityReceiver, highPriorityFilter);
            registerReceiver(lowPriorityReceiver, lowPriorityFilter);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 必须注销动态注册的接收器，防止内存泄漏
        if (mTimeTickReceiver != null) {
            unregisterReceiver(mTimeTickReceiver);
        }
        if (normalReceiver != null) {
            unregisterReceiver(normalReceiver);
        }
        if (highPriorityReceiver != null) {
            unregisterReceiver(highPriorityReceiver);
        }
        if (lowPriorityReceiver != null) {
            unregisterReceiver(lowPriorityReceiver);
        }
    }

    /**
     * 动态注册的接收器 - 用于监听时间变化
     */
    private class TimeTickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                mTimeTextView.setText("最新时间: " + currentTime);
                Toast.makeText(context, "时间已更新", Toast.LENGTH_SHORT).show();
            }
        }
    }
}