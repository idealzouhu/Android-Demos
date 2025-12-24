package com.example.notification.basic;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notification.basic.utils.NotificationChannelManager;
import com.example.notification.basic.utils.NotificationUtils;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 常量
    private static final int NOTIFICATION_ID_SIMPLE = 1001;
    private static final int NOTIFICATION_ID_BIGTEXT = 1002;
    private static final int NOTIFICATION_ID_BIGPICTURE = 1003;
    private static final int NOTIFICATION_ID_PROGRESS = 1004;
    private static final int NOTIFICATION_ID_ACTION = 1005;
    private static final int NOTIFICATION_ID_REPLY = 1006;
    private static final int PERMISSION_REQUEST_CODE = 3001;

    // UI组件
    private Button btnSimpleNotification;
    private Button btnBigTextNotification;
    private Button btnBigPictureNotification;
    private Button btnStartProgress;
    private Button btnCancelProgress;
    private Button btnActionNotification;
    private Button btnReplyNotification;
    private Button btnCancelNotification;
    private Button btnCancelAll;
    private SeekBar seekBarProgress;
    private TextView tvStatus;

    // 工具类实例
    private NotificationUtils notificationUtils;
    private NotificationChannelManager channelManager;

    // 控制变量
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isProgressRunning = false;
    private int currentProgress = 0;
    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (isProgressRunning && currentProgress <= 100) {
                // 更新进度通知
                notificationUtils.sendProgressNotification(
                        "文件下载中",
                        currentProgress,
                        100,
                        false,
                        NOTIFICATION_ID_PROGRESS
                );

                // 更新UI进度条
                seekBarProgress.setProgress(currentProgress);

                currentProgress += 5;
                if (currentProgress <= 100) {
                    handler.postDelayed(this, 500);
                } else {
                    isProgressRunning = false;
                    notificationUtils.sendProgressNotification(
                            "下载完成",
                            100,
                            100,
                            false,
                            NOTIFICATION_ID_PROGRESS
                    );
                    updateStatus("进度通知完成");
                }
            }
        }
    };

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

        // 初始化UI组件
        initViews();

        // 初始化工具类
        initNotificationUtils();

        // 设置事件监听
        setupListeners();

        // 检查通知权限
        checkNotificationPermission();
    }

    private void initViews() {
        btnSimpleNotification = findViewById(R.id.btn_simple_notification);
        btnBigTextNotification = findViewById(R.id.btn_bigtext_notification);
        btnBigPictureNotification = findViewById(R.id.btn_bigpicture_notification);
        btnStartProgress = findViewById(R.id.btn_start_progress);
        btnCancelProgress = findViewById(R.id.btn_cancel_progress);
        btnActionNotification = findViewById(R.id.btn_action_notification);
        btnReplyNotification = findViewById(R.id.btn_reply_notification);
        btnCancelNotification = findViewById(R.id.btn_cancel_notification);
        btnCancelAll = findViewById(R.id.btn_cancel_all);
        seekBarProgress = findViewById(R.id.seekbar_progress);
        tvStatus = findViewById(R.id.tv_status);

        // 初始化进度条
        seekBarProgress.setEnabled(false);
    }

    private void initNotificationUtils() {
        // 初始化工具类
        notificationUtils = NotificationUtils.getInstance(this);
        channelManager = NotificationChannelManager.getInstance(this);

        // 创建通知渠道
        channelManager.createAllChannels();

        updateStatus("通知工具初始化完成");
    }

    private void checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "通知权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "通知权限被拒绝，部分功能可能无法使用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupListeners() {
        // 简单通知按钮
        btnSimpleNotification.setOnClickListener(v -> sendSimpleNotification());

        // 大文本通知按钮
        btnBigTextNotification.setOnClickListener(v -> sendBigTextNotification());

        // 大图片通知按钮
        btnBigPictureNotification.setOnClickListener(v -> sendBigPictureNotification());

        // 开始进度通知
        btnStartProgress.setOnClickListener(v -> startProgressNotification());

        // 取消进度通知
        btnCancelProgress.setOnClickListener(v -> cancelProgressNotification());

        // 带操作的通知
        btnActionNotification.setOnClickListener(v -> sendActionNotification());

        // 可回复的通知
        btnReplyNotification.setOnClickListener(v -> sendReplyNotification());

        // 取消指定通知
        btnCancelNotification.setOnClickListener(v -> {
            notificationUtils.cancelNotification(NOTIFICATION_ID_SIMPLE);
            updateStatus("已取消通知ID: " + NOTIFICATION_ID_SIMPLE);
            Toast.makeText(MainActivity.this, "指定通知已取消", Toast.LENGTH_SHORT).show();
        });

        // 取消所有通知
        btnCancelAll.setOnClickListener(v -> {
            notificationUtils.cancelAllNotifications();
            updateStatus("已取消所有通知");
            Toast.makeText(MainActivity.this, "所有通知已取消", Toast.LENGTH_SHORT).show();
        });

        // 进度条监听
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isProgressRunning) {
                    currentProgress = progress;
                    notificationUtils.sendProgressNotification(
                            "文件下载中",
                            currentProgress,
                            100,
                            false,
                            NOTIFICATION_ID_PROGRESS
                    );
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void sendSimpleNotification() {
        // 创建PendingIntent
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("source", "notification_click");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.sendSimpleNotification(
                "简单通知",
                "这是一个简单的通知示例",
                NOTIFICATION_ID_SIMPLE
        );

        updateStatus("简单通知已发送");
        Toast.makeText(this, "简单通知已发送", Toast.LENGTH_SHORT).show();
    }

    private void sendBigTextNotification() {
        String longText = "这是一条大文本通知，可以显示非常多的内容。\n" +
                "这是第二行内容，可以继续添加更多的文本信息。\n" +
                "这是第三行内容，大文本通知非常适合显示详细信息。\n" +
                "这是第四行内容，用户可以通过展开通知查看完整内容。\n" +
                "这是最后一行内容，通知内容到此结束。";

        notificationUtils.sendBigTextNotification(
                "大文本通知",
                "点击展开查看详细内容",
                longText,
                NOTIFICATION_ID_BIGTEXT
        );

        updateStatus("大文本通知已发送");
        Toast.makeText(this, "大文本通知已发送", Toast.LENGTH_SHORT).show();
    }

    private void sendBigPictureNotification() {
        notificationUtils.sendBigPictureNotification(
                "大图片通知",
                "包含大图片的通知示例",
                android.R.drawable.ic_dialog_map,
                NOTIFICATION_ID_BIGPICTURE
        );

        updateStatus("大图片通知已发送");
        Toast.makeText(this, "大图片通知已发送", Toast.LENGTH_SHORT).show();
    }

    private void startProgressNotification() {
        if (isProgressRunning) {
            Toast.makeText(this, "进度通知已在运行", Toast.LENGTH_SHORT).show();
            return;
        }

        isProgressRunning = true;
        currentProgress = 0;
        seekBarProgress.setEnabled(true);
        seekBarProgress.setProgress(0);

        // 开始模拟进度更新
        handler.post(progressRunnable);

        updateStatus("进度通知已启动");
    }

    private void cancelProgressNotification() {
        isProgressRunning = false;
        handler.removeCallbacks(progressRunnable);
        notificationUtils.cancelNotification(NOTIFICATION_ID_PROGRESS);
        seekBarProgress.setEnabled(false);
        updateStatus("进度通知已取消");
        Toast.makeText(this, "进度通知已取消", Toast.LENGTH_SHORT).show();
    }

    private void sendActionNotification() {
        // 创建动作按钮
        List<NotificationCompat.Action> actions = new ArrayList<>();

        // 按钮1 -- 使用显式 Intent
        Intent action1Intent = new Intent(this, NotificationActionReceiver.class);
        action1Intent.setAction("ACTION_BUTTON_1");
        android.app.PendingIntent action1PendingIntent = android.app.PendingIntent.getBroadcast(
                this, 0, action1Intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(
                R.drawable.ic_notification,
                "操作1",
                action1PendingIntent
        ).build();

        // 按钮2
        Intent action2Intent = new Intent(this, NotificationActionReceiver.class);
        action2Intent.setAction("ACTION_BUTTON_2");
        android.app.PendingIntent action2PendingIntent = android.app.PendingIntent.getBroadcast(
                this, 1, action2Intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(
                R.drawable.ic_notification,
                "操作2",
                action2PendingIntent
        ).build();

        actions.add(action1);
        actions.add(action2);

        notificationUtils.sendActionNotification(
                "带操作的通知",
                "点击按钮执行相应操作",
                actions,
                NOTIFICATION_ID_ACTION
        );

        updateStatus("带操作的通知已发送");
        Toast.makeText(this, "带操作的通知已发送", Toast.LENGTH_SHORT).show();
    }

    private void sendReplyNotification() {
        // 创建回复PendingIntent
        PendingIntent replyPendingIntent = notificationUtils.createReplyPendingIntent(
                NOTIFICATION_ID_REPLY,
                0
        );

        notificationUtils.sendReplyNotification(
                "可回复的通知",
                "收到一条新消息，请回复",
                "请输入回复内容",
                NOTIFICATION_ID_REPLY,
                replyPendingIntent
        );

        updateStatus("可回复的通知已发送");
        Toast.makeText(this, "可回复的通知已发送", Toast.LENGTH_SHORT).show();
    }

    private void updateStatus(String message) {
        tvStatus.setText("状态: " + message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
        handler.removeCallbacks(progressRunnable);
        notificationUtils.cancelAllNotifications();
    }
}