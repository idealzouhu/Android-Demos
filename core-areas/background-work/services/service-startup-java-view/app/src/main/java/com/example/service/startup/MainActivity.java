package com.example.service.startup;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // 启动式Service相关
    private Button btnStartService;
    private Button btnStopService;
    private TextView tvStartedStatus;

    // 绑定式Service相关
    private Button btnBindService;
    private Button btnUnbindService;
    private Button btnCallService;
    private TextView tvBoundStatus;
    private TextView tvServiceResult;

    // 混合式Service相关
    private Button btnStartHybrid;
    private Button btnBindHybrid;
    private TextView tvHybridStatus;
    private TextView tvHybridProgress;

    // Service实例
    private MyBoundService boundService;
    private HybridService hybridService;
    private boolean isBound = false;
    private boolean isHybridBound = false;

    // MyBoundService 连接
    private final ServiceConnection boundConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBoundService.LocalBinder binder = (MyBoundService.LocalBinder) service;
            boundService = binder.getService();
            isBound = true;

            Log.i(TAG, "MyBoundService 绑定成功");
            updateBoundStatus("已绑定");
            btnCallService.setEnabled(true);
            btnBindService.setEnabled(false);
            btnUnbindService.setEnabled(true);
            Toast.makeText(MainActivity.this, "MyBoundService绑定成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            boundService = null;

            Log.w(TAG, "MyBoundService 连接断开");
            updateBoundStatus("连接断开");
            btnCallService.setEnabled(false);
        }
    };

    // HybridService连接
    private final ServiceConnection hybridConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            HybridService.HybridBinder binder = (HybridService.HybridBinder) service;
            hybridService = binder.getService();
            isHybridBound = true;

            Log.i(TAG, "HybridService 绑定成功");
            updateHybridStatus("已绑定");
            Toast.makeText(MainActivity.this, "HybridService绑定成功", Toast.LENGTH_SHORT).show();

            // 轮询查询进度
            startProgressPolling();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isHybridBound = false;
            hybridService = null;

            Log.w(TAG, "HybridService 连接断开");
            updateHybridStatus("连接断开");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity onCreate()");

        initViews();
        setupListeners();
    }

    private void initViews() {
        // 启动式Service视图
        btnStartService = findViewById(R.id.btnStartService);
        btnStopService = findViewById(R.id.btnStopService);
        tvStartedStatus = findViewById(R.id.tvStartedStatus);

        // 绑定式Service视图
        btnBindService = findViewById(R.id.btnBindService);
        btnUnbindService = findViewById(R.id.btnUnbindService);
        btnCallService = findViewById(R.id.btnCallService);
        tvBoundStatus = findViewById(R.id.tvBoundStatus);
        tvServiceResult = findViewById(R.id.tvServiceResult);

        // 混合式Service视图
        btnStartHybrid = findViewById(R.id.btnStartHybrid);
        btnBindHybrid = findViewById(R.id.btnBindHybrid);
        tvHybridStatus = findViewById(R.id.tvHybridStatus);
        tvHybridProgress = findViewById(R.id.tvHybridProgress);

        // 初始化状态
        updateStartedStatus("未启动");
        updateBoundStatus("未绑定");
        updateHybridStatus("未启动");
        tvHybridProgress.setText("进度: 0%");

        // 初始化按钮状态
        btnCallService.setEnabled(false);
        btnUnbindService.setEnabled(false);
    }

    private void setupListeners() {
        // 启动式Service按钮监听
        btnStartService.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyStartedService.class);
            startService(intent);

            Log.i(TAG, "启动 MyStartedService");
            updateStartedStatus("运行中");
            Toast.makeText(this, "MyStartedService已启动", Toast.LENGTH_SHORT).show();
        });

        btnStopService.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyStartedService.class);
            stopService(intent);

            Log.i(TAG, "停止 MyStartedService");
            updateStartedStatus("已停止");
            Toast.makeText(this, "MyStartedService已停止", Toast.LENGTH_SHORT).show();
        });

        // 绑定式Service按钮监听
        btnBindService.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyBoundService.class);
            boolean result = bindService(intent, boundConnection, Context.BIND_AUTO_CREATE);

            Log.i(TAG, "绑定 MyBoundService, 结果: " + result);
            if (result) {
                Toast.makeText(this, "开始绑定MyBoundService...", Toast.LENGTH_SHORT).show();
            }
        });

        btnUnbindService.setOnClickListener(v -> {
            if (isBound) {
                unbindService(boundConnection);
                isBound = false;
                boundService = null;

                Log.i(TAG, "解绑 MyBoundService");
                updateBoundStatus("已解绑");
                tvServiceResult.setText("调用结果: ");
                btnCallService.setEnabled(false);
                btnBindService.setEnabled(true);
                btnUnbindService.setEnabled(false);
                Toast.makeText(this, "MyBoundService已解绑", Toast.LENGTH_SHORT).show();
            }
        });

        btnCallService.setOnClickListener(v -> {
            if (isBound && boundService != null) {
                // 调用Service的方法
                String time = boundService.getCurrentTime();
                String info = boundService.getServiceInfo();

                String result = String.format(Locale.getDefault(),
                        "当前时间: %s\n%s",
                        time, info);

                tvServiceResult.setText("调用结果:\n" + result);

                Log.i(TAG, String.format(Locale.getDefault(),
                        "调用Service方法 - 时间: %s", time));

                Toast.makeText(this, "调用Service方法成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Service未绑定或已断开", Toast.LENGTH_SHORT).show();
            }
        });

        // 混合式Service按钮监听
        btnStartHybrid.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HybridService.class);
            intent.setAction("start_download");
            startService(intent);

            Log.i(TAG, "启动 HybridService 下载任务");
            updateHybridStatus("下载中");
            Toast.makeText(this, "开始下载任务...", Toast.LENGTH_SHORT).show();
        });

        btnBindHybrid.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HybridService.class);
            boolean result = bindService(intent, hybridConnection, Context.BIND_AUTO_CREATE);

            Log.i(TAG, "绑定 HybridService, 结果: " + result);
            if (result) {
                Toast.makeText(this, "开始绑定HybridService查询进度...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStartedStatus(String status) {
        tvStartedStatus.setText("状态: " + status);
    }

    private void updateBoundStatus(String status) {
        tvBoundStatus.setText("状态: " + status);
    }

    private void updateHybridStatus(String status) {
        tvHybridStatus.setText("状态: " + status);
    }

    private void startProgressPolling() {
        new Thread(() -> {
            while (isHybridBound && hybridService != null) {
                try {
                    Thread.sleep(500);  // 每500ms查询一次进度

                    int progress = hybridService.getDownloadProgress();
                    boolean isDownloading = hybridService.isDownloading();

                    runOnUiThread(() -> {
                        tvHybridProgress.setText("进度: " + progress + "%");

                        if (!isDownloading && progress >= 100) {
                            updateHybridStatus("下载完成");
                            // 下载完成后解绑
                            if (isHybridBound) {
                                unbindService(hybridConnection);
                                isHybridBound = false;
                                Log.i(TAG, "HybridService 下载完成，自动解绑");
                                Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                            }
                        } else if (!isDownloading) {
                            updateHybridStatus("已停止");
                        }
                    });

                } catch (InterruptedException e) {
                    Log.w(TAG, "进度轮询被中断");
                    break;
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "MainActivity onDestroy()");

        // 确保解绑所有Service
        if (isBound) {
            unbindService(boundConnection);
            isBound = false;
            Log.i(TAG, "自动解绑 MyBoundService");
        }

        if (isHybridBound) {
            unbindService(hybridConnection);
            isHybridBound = false;
            Log.i(TAG, "自动解绑 HybridService");
        }
    }
}