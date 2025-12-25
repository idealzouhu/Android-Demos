package com.example.handler.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI组件
    private TextView tvStatus;
    private TextView tvMessage;
    private TextView tvLog;
    private TextView tvCounter;
    private ProgressBar progressBar;

    // Handler相关
    private MainHandler mainHandler;
    private HandlerThread workerThread;
    private Handler workerHandler;

    // 计数器
    private int counter = 0;

    // 使用静态内部类+弱引用避免内存泄漏
    private static class MainHandler extends Handler {
        private final WeakReference<MainActivity> activityRef;

        MainHandler(MainActivity activity) {
            super(Looper.getMainLooper());
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            switch (msg.what) {
                case MessageWhat.MSG_UPDATE_TEXT:
                    // 更新文本显示
                    String text = (String) msg.obj;
                    activity.tvMessage.setText(text);
                    activity.logMessage("收到文本消息: " + text);
                    break;

                case MessageWhat.MSG_UPDATE_PROGRESS:
                    int progress = msg.arg1;
                    activity.progressBar.setProgress(progress);

                    // 显示进度文本
                    TextView tvProgress = activity.findViewById(R.id.tv_progress);
                    if (tvProgress != null) {
                        tvProgress.setText(progress + "%");
                    }

                    // 显示进度条和文本
                    if (progress > 0 && progress < 100) {
                        activity.progressBar.setVisibility(View.VISIBLE);
                        LinearLayout progressContainer = activity.findViewById(R.id.progress_container);
                        if (progressContainer != null) {
                            progressContainer.setVisibility(View.VISIBLE);
                        }
                    }

                    if (progress >= 100) {
                        activity.progressBar.setVisibility(View.GONE);
                        activity.tvStatus.setText("任务完成");
                        // activity.logMessage("进度完成: 100%");

                        // 隐藏进度容器
                        LinearLayout progressContainer = activity.findViewById(R.id.progress_container);
                        if (progressContainer != null) {
                            progressContainer.setVisibility(View.GONE);
                        }
                    } else {
                        activity.tvStatus.setText("处理中: " + progress + "%");
                    }
                    break;

                case MessageWhat.MSG_UPDATE_COUNTER:
                    // 更新计数器
                    activity.counter++;
                    activity.tvCounter.setText("计数器: " + activity.counter);
                    break;

                case MessageWhat.MSG_SHOW_TOAST:
                    // 显示Toast
                    String toastMsg = (String) msg.obj;
                    Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT).show();
                    activity.logMessage("显示Toast: " + toastMsg);
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

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
        initHandlers();
        setupClickListeners();

        logMessage("应用启动，主线程ID: " + Thread.currentThread().getId());
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvMessage = findViewById(R.id.tv_message);
        tvLog = findViewById(R.id.tv_log);
        tvCounter = findViewById(R.id.tv_counter);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initHandlers() {
        // 创建主线程Handler
        mainHandler = new MainHandler(this);

        // 创建工作线程和Handler
        workerThread = new HandlerThread("WorkerThread");
        workerThread.start();
        workerHandler = new Handler(workerThread.getLooper());

        logMessage("Handler初始化完成");
    }

    private void setupClickListeners() {
        // 发送Message
        findViewById(R.id.btn_send_message).setOnClickListener(v -> sendMessageExample());

        // 发送Runnable
        findViewById(R.id.btn_post_runnable).setOnClickListener(v -> postRunnableExample());

        // 发送延时消息
        findViewById(R.id.btn_delayed).setOnClickListener(v -> delayedMessageExample());

        // HandlerThread测试
        findViewById(R.id.btn_handler_thread).setOnClickListener(v -> handlerThreadExample());

        // 清除消息
        findViewById(R.id.btn_clear_log).setOnClickListener(v -> clearMessages());
    }

    /**
     * 示例1：发送Message对象
     */
    private void sendMessageExample() {
        new Thread(() -> {
            // 模拟耗时操作
            logMessage("子线程开始耗时操作，线程ID: " + Thread.currentThread().getId());
            simulateWork(1000);

            // 准备消息
            Message message = Message.obtain();
            message.what = MessageWhat.MSG_UPDATE_TEXT;
            message.obj = "来自子线程的消息\n时间: " + getCurrentTime();

            // 发送消息到主线程
            mainHandler.sendMessage(message);
            logMessage("Message已发送到主线程");

        }).start();
    }

    /**
     * 示例2：发送Runnable
     */
    private void postRunnableExample() {
        new Thread(() -> {
            logMessage("子线程开始Runnable操作");
            simulateWork(800);

            // 在主线程执行UI更新
            mainHandler.post(() -> {
                tvMessage.setText("通过Runnable更新UI\n线程ID: " + Thread.currentThread().getId());
                logMessage("Runnable在主线程执行完成");

                // 嵌套发送另一个Runnable
                mainHandler.post(() -> {
                    Toast.makeText(MainActivity.this, "嵌套Runnable", Toast.LENGTH_SHORT).show();
                });
            });

        }).start();
    }

    /**
     * 示例3：延时消息
     */
    private void delayedMessageExample() {
        logMessage("发送延时消息，3秒后执行");
        tvStatus.setText("等待延时消息...");

        // 发送延时Message
        Message msg = Message.obtain();
        msg.what = MessageWhat.MSG_UPDATE_TEXT;
        msg.obj = "这是3秒后的消息";
        mainHandler.sendMessageDelayed(msg, 3000);

        // 发送延时Runnable
        mainHandler.postDelayed(() -> {
            tvMessage.setText("这是2秒后的Runnable");
            logMessage("延时Runnable执行完成");
        }, 2000);

        // 每隔1s计数器自动增加，验证延时方法的运行
        for (int i = 1; i <= 5; i++) {
            final int count = i;
            mainHandler.postDelayed(() -> {
                Message counterMsg = Message.obtain();
                counterMsg.what = MessageWhat.MSG_UPDATE_COUNTER;
                mainHandler.sendMessage(counterMsg);
            }, i * 1000L);
        }
    }

    /**
     * 示例4：HandlerThread使用
     */
    private void handlerThreadExample() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        progressBar.setProgress(0);
        tvStatus.setText("HandlerThread任务开始");

        // 在工作线程执行耗时任务
        workerHandler.post(() -> {
            logMessage("HandlerThread开始工作，线程: " + Thread.currentThread().getName());

            try {
                for (int i = 1; i <= 10; i++) {
                    // 模拟工作
                    Thread.sleep(300);

                    // 更新进度到主线程
                    Message progressMsg = Message.obtain();
                    progressMsg.what = MessageWhat.MSG_UPDATE_PROGRESS;
                    progressMsg.arg1 = i * 10;
                    mainHandler.sendMessage(progressMsg);

                    logMessage("HandlerThread进度: " + (i * 10) + "%");
                }

                // 任务完成
                Message doneMsg = Message.obtain();
                doneMsg.what = MessageWhat.MSG_SHOW_TOAST;
                doneMsg.obj = "HandlerThread任务完成";
                mainHandler.sendMessage(doneMsg);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 清除所有消息
     */
    private void clearMessages() {
        // 移除主线程Handler的所有消息和Runnable
        mainHandler.removeCallbacksAndMessages(null);

        // 移除工作线程Handler的所有消息
        if (workerHandler != null) {
            workerHandler.removeCallbacksAndMessages(null);
        }

        tvMessage.setText("消息已清除");
        tvStatus.setText("就绪");
        progressBar.setVisibility(android.view.View.GONE);
        tvLog.setText("日志记录：\n");
        // logMessage("所有消息已清除");
    }

    /**
     * 模拟耗时操作
     */
    private void simulateWork(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录日志
     */
    void logMessage(String message) {
        String time = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
                .format(new Date());
        String log = time + " " + message + "\n";

        runOnUiThread(() -> tvLog.append(log));
    }

    /**
     * 获取当前时间字符串
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 清除所有消息，避免内存泄漏
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }

        if (workerHandler != null) {
            workerHandler.removeCallbacksAndMessages(null);
        }

        if (workerThread != null) {
            workerThread.quitSafely();
        }

        logMessage("Activity销毁，资源已释放");
    }
}