package com.example.foregroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // UI组件
    private Button btnStartService;
    private Button btnStopService;
    private Button btnStartDownload;
    private Button btnPauseDownload;
    private Button btnResumeDownload;
    private TextView tvServiceStatus;
    private TextView tvDownloadStatus;
    private TextView tvProgressText;
    private TextView tvDownloadInfo;
    private ProgressBar progressBar;

    // 权限请求
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "通知权限已授予", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "需要通知权限才能显示前台服务", Toast.LENGTH_LONG).show();
                }
            });

    // 广播接收器
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            switch (action) {
                case ForegroundService.ACTION_PROGRESS_UPDATE:
                    int progress = intent.getIntExtra(ForegroundService.EXTRA_PROGRESS, 0);
                    updateProgress(progress);
                    break;

                case ForegroundService.ACTION_DOWNLOAD_STATUS:
                    String status = intent.getStringExtra(ForegroundService.EXTRA_STATUS);
                    String info = intent.getStringExtra(ForegroundService.EXTRA_INFO);
                    updateDownloadStatus(status, info);
                    break;

                case ForegroundService.ACTION_SERVICE_STARTED:
                    tvServiceStatus.setText("服务状态: 运行中");
                    btnStartService.setEnabled(false);
                    btnStopService.setEnabled(true);
                    btnStartDownload.setEnabled(true);
                    break;

                case ForegroundService.ACTION_SERVICE_STOPPED:
                    tvServiceStatus.setText("服务状态: 已停止");
                    btnStartService.setEnabled(true);
                    btnStopService.setEnabled(false);
                    btnStartDownload.setEnabled(false);
                    btnPauseDownload.setEnabled(false);
                    btnResumeDownload.setEnabled(false);
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
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
        setupListeners();
        checkNotificationPermission();

        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(ForegroundService.ACTION_PROGRESS_UPDATE);
        filter.addAction(ForegroundService.ACTION_DOWNLOAD_STATUS);
        filter.addAction(ForegroundService.ACTION_SERVICE_STARTED);
        filter.addAction(ForegroundService.ACTION_SERVICE_STOPPED);
        registerReceiver(broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
    }

    private void initViews() {
        btnStartService = findViewById(R.id.btnStartService);
        btnStopService = findViewById(R.id.btnStopService);
        btnStartDownload = findViewById(R.id.btnStartDownload);
        btnPauseDownload = findViewById(R.id.btnPauseDownload);
        btnResumeDownload = findViewById(R.id.btnResumeDownload);
        tvServiceStatus = findViewById(R.id.tvServiceStatus);
        tvDownloadStatus = findViewById(R.id.tvDownloadStatus);
        tvProgressText = findViewById(R.id.tvProgressText);
        tvDownloadInfo = findViewById(R.id.tvDownloadInfo);
        progressBar = findViewById(R.id.progressBar);

        // 初始状态
        btnStopService.setEnabled(false);
        btnStartDownload.setEnabled(false);
        btnPauseDownload.setEnabled(false);
        btnResumeDownload.setEnabled(false);
    }

    private void setupListeners() {
        btnStartService.setOnClickListener(v -> {
            if (checkNotificationPermission()) {
                startForegroundService();
            }
        });

        btnStopService.setOnClickListener(v -> {
            stopForegroundService();
        });

        btnStartDownload.setOnClickListener(v -> {
            startDownload();
        });

        btnPauseDownload.setOnClickListener(v -> {
            pauseDownload();
        });

        btnResumeDownload.setOnClickListener(v -> {
            resumeDownload();
        });
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return false;
            }
        }
        return true;
    }

    private void startForegroundService() {
        Intent intent = new Intent(this, ForegroundService.class);
        intent.setAction(ForegroundService.ACTION_START_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        Toast.makeText(this, "正在启动前台服务...", Toast.LENGTH_SHORT).show();
    }

    private void stopForegroundService() {
        Intent intent = new Intent(this, ForegroundService.class);
        intent.setAction(ForegroundService.ACTION_STOP_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        Toast.makeText(this, "正在停止前台服务...", Toast.LENGTH_SHORT).show();
    }

    private void startDownload() {
        Intent intent = new Intent(this, ForegroundService.class);
        intent.setAction(ForegroundService.ACTION_START_DOWNLOAD);
        intent.putExtra(ForegroundService.EXTRA_FILE_URL, "https://example.com/largefile.zip");
        intent.putExtra(ForegroundService.EXTRA_FILE_SIZE, 100); // MB

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        btnStartDownload.setEnabled(false);
        btnPauseDownload.setEnabled(true);
        btnResumeDownload.setEnabled(false);
    }

    private void pauseDownload() {
        Intent intent = new Intent(this, ForegroundService.class);
        intent.setAction(ForegroundService.ACTION_PAUSE_DOWNLOAD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        btnPauseDownload.setEnabled(false);
        btnResumeDownload.setEnabled(true);
    }

    private void resumeDownload() {
        Intent intent = new Intent(this, ForegroundService.class);
        intent.setAction(ForegroundService.ACTION_RESUME_DOWNLOAD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        btnPauseDownload.setEnabled(true);
        btnResumeDownload.setEnabled(false);
    }

    private void updateProgress(int progress) {
        progressBar.setProgress(progress);
        tvProgressText.setText(progress + "%");

        // 计算已下载大小
        int downloaded = progress * 100 / 100; // 100MB 文件
        tvDownloadInfo.setText(String.format("已下载: %d MB / 总大小: 100 MB", downloaded));
    }

    private void updateDownloadStatus(String status, String info) {
        tvDownloadStatus.setText("下载状态: " + status);

        if ("完成".equals(status)) {
            btnStartDownload.setEnabled(true);
            btnPauseDownload.setEnabled(false);
            btnResumeDownload.setEnabled(false);
        } else if ("暂停".equals(status)) {
            btnPauseDownload.setEnabled(false);
            btnResumeDownload.setEnabled(true);
        } else if ("下载中".equals(status)) {
            btnPauseDownload.setEnabled(true);
            btnResumeDownload.setEnabled(false);
        }

        if (info != null) {
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}