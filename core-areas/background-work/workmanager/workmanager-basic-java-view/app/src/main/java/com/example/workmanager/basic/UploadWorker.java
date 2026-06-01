package com.example.workmanager.basic;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadWorker extends Worker {

    private static final String TAG = "UploadWorker";

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // 这里执行后台任务
        Log.d(TAG, "开始执行后台任务");
        
        try {
            // 模拟上传操作
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000); // 模拟耗时操作
                Log.d(TAG, "上传进度: " + (i + 1) * 10 + "%");
            }
            
            Log.d(TAG, "后台任务执行完成");
            return Result.success();
        } catch (InterruptedException e) {
            Log.e(TAG, "任务被中断", e);
            return Result.retry();
        } catch (Exception e) {
            Log.e(TAG, "任务执行失败", e);
            return Result.failure();
        }
    }
}

