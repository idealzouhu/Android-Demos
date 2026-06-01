package com.example.workmanager.basic;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkInfo;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String WORK_TAG = "upload_work";
    private WorkManager workManager;
    private Button startWorkButton;
    private Button startPeriodicWorkButton;
    private Button cancelWorkButton;
    private TextView statusText;
    // 记录上一次的状态，用于判断状态是否真正从运行中变为完成
    private WorkInfo.State previousState = null;

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

        // 初始化 WorkManager
        workManager = WorkManager.getInstance(this);

        // 初始化视图
        initViews();

        // 设置按钮点击事件
        setupClickListeners();

        // 观察工作状态
        observeWorkStatus();
    }

    private void initViews() {
        startWorkButton = findViewById(R.id.start_work_button);
        startPeriodicWorkButton = findViewById(R.id.start_periodic_work_button);
        cancelWorkButton = findViewById(R.id.cancel_work_button);
        statusText = findViewById(R.id.status_text);
    }

    private void setupClickListeners() {
        // 启动一次性任务
        startWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOneTimeWork();
            }
        });

        // 启动周期性任务
        startPeriodicWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPeriodicWork();
            }
        });

        // 取消所有任务
        cancelWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAllWork();
            }
        });
    }

    private void startOneTimeWork() {
        // 创建约束条件（可选）
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();

        // 创建一次性工作请求
        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class)
                .setConstraints(constraints)
                .addTag(WORK_TAG)
                .build();

        // 将任务加入队列
        workManager.enqueue(uploadWorkRequest);

        // 状态文本会由 LiveData 观察者自动更新，这里只显示 Toast
        Toast.makeText(this, "一次性任务已启动", Toast.LENGTH_SHORT).show();
    }

    private void startPeriodicWork() {
        // 创建约束条件
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();

        // 创建周期性工作请求（最小间隔为15分钟）
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                UploadWorker.class,
                15,
                TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(WORK_TAG)
                .build();

        // 将任务加入队列
        workManager.enqueue(periodicWorkRequest);

        // 状态文本会由 LiveData 观察者自动更新，这里只显示 Toast
        Toast.makeText(this, "周期性任务已启动（每15分钟执行一次）", Toast.LENGTH_SHORT).show();
    }

    private void cancelAllWork() {
        workManager.cancelAllWorkByTag(WORK_TAG);
        // 状态文本会由 LiveData 观察者自动更新，这里只显示 Toast
        Toast.makeText(this, "所有任务已取消", Toast.LENGTH_SHORT).show();
    }

    /**
     * 观察任务状态
     * <p>
     * 显示策略：
     * 1. 优先显示活跃任务状态（RUNNING、ENQUEUED、BLOCKED）- 用户最关心的当前状态
     * 2. 如果没有活跃任务，显示最新任务的状态（包括 SUCCEEDED、FAILED、CANCELLED）- 让用户知道最近的任务结果
     * 3. 如果没有任何任务，显示 READY
     */
    private void observeWorkStatus() {
        workManager.getWorkInfosByTagLiveData(WORK_TAG).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) {
                // 没有任务时显示就绪状态
                statusText.setText("READY");
                return;
            }

            // 查找活跃任务（正在运行、等待执行或被阻塞的任务）
            WorkInfo activeWorkInfo = null;
            for (WorkInfo workInfo : workInfos) {
                WorkInfo.State state = workInfo.getState();
                if (state == WorkInfo.State.RUNNING ||
                    state == WorkInfo.State.ENQUEUED ||
                    state == WorkInfo.State.BLOCKED) {
                    activeWorkInfo = workInfo;
                    break;
                }
            }

            if (activeWorkInfo != null) {
                // 有活跃任务时，显示活跃任务状态
                updateStatusText(activeWorkInfo.getState());
                return;
            }

            // 没有活跃任务时，显示最新任务的状态（通常是最近完成的任务）
            // WorkManager 返回的列表按时间排序，最后一个是最新的
            WorkInfo latestWorkInfo = workInfos.get(workInfos.size() - 1);
            updateStatusText(latestWorkInfo.getState());
        });
    }
    
    /**
     * 更新状态文本并处理完成提示
     */
    private void updateStatusText(WorkInfo.State state) {
        // 直接显示状态枚举名称
        statusText.setText(state.name());
        
        // 只在状态从运行中（RUNNING、ENQUEUED、BLOCKED）变为完成（SUCCEEDED、FAILED）时才显示 Toast
        // 这样可以避免应用重启时显示旧状态的 Toast
        boolean isStateChanged = previousState != state;
        boolean wasRunning = previousState == WorkInfo.State.RUNNING ||
                            previousState == WorkInfo.State.ENQUEUED ||
                            previousState == WorkInfo.State.BLOCKED;
        boolean isCompleted = state == WorkInfo.State.SUCCEEDED || state == WorkInfo.State.FAILED;
        
        if (isStateChanged && wasRunning && isCompleted) {
            if (state == WorkInfo.State.SUCCEEDED) {
                Log.d("MainActivity", "任务执行成功");
                Toast.makeText(this, "任务执行成功", Toast.LENGTH_SHORT).show();
            } else if (state == WorkInfo.State.FAILED) {
                Log.d("MainActivity", "任务执行失败");
                Toast.makeText(this, "任务执行失败", Toast.LENGTH_SHORT).show();
            }
        }
        
        // 更新上一次的状态
        previousState = state;
    }
}