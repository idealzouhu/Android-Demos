package com.example.anr.block;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 在 {@link #onReceive} 主线程路径中长时间阻塞，用于演示广播执行超时类 ANR。
 * 仅用于调试，勿用于生产环境。
 */
public class HeavyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "HeavyBroadcastReceiver";

    /** 超过系统对 {@code onReceive} 的典型时限（约 10s）以便稳定复现。 */
    private static final long BLOCK_MS = 12_000L;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG, "onReceive: blocking main thread for " + BLOCK_MS + " ms");
        try {
            Thread.sleep(BLOCK_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
