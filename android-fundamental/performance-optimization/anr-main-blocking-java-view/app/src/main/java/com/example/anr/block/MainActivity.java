package com.example.anr.block;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 各按钮在主线程（或系统调度的广播主线程）上故意执行耗时操作，用于对照 README 中的 ANR 典型场景。
 * 仅用于本地调试与学习。
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AnrDemo";

    /** 易触发「输入分发超时」的休眠时长（秒级）。 */
    private static final long MAIN_THREAD_SLEEP_MS = 15_000L;

    /** 主线程密集计算时长目标。 */
    private static final long CPU_SPIN_TARGET_MS = 8_000L;

    private static final String PREFS_NAME = "anr_demo_prefs";

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

        findViewById(R.id.btn_sleep).setOnClickListener(v -> mainThreadSleep());
        findViewById(R.id.btn_cpu).setOnClickListener(v -> mainThreadCpuSpin());
        findViewById(R.id.btn_io).setOnClickListener(v -> mainThreadDiskIo());
        findViewById(R.id.btn_prefs_commit).setOnClickListener(v -> mainThreadPrefsCommit());
        findViewById(R.id.btn_package_manager).setOnClickListener(v -> mainThreadPackageManagerQuery());
        findViewById(R.id.btn_deadlock).setOnClickListener(v -> mainThreadDeadlock());
        findViewById(R.id.btn_broadcast).setOnClickListener(v -> sendHeavyBroadcast());
    }

    /** 主线程 {@link Thread#sleep(long)}，典型「输入分发超时」诱因。 */
    private void mainThreadSleep() {
        Log.w(TAG, "mainThreadSleep: start");
        try {
            Thread.sleep(MAIN_THREAD_SLEEP_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Log.w(TAG, "mainThreadSleep: end (若前面未 ANR 才会打印)");
    }

    /** 主线程长时间 CPU 占用（无 sleep），界面无法及时响应输入与绘制。 */
    private void mainThreadCpuSpin() {
        Log.w(TAG, "mainThreadCpuSpin: start");
        long deadline = SystemClock.elapsedRealtime() + CPU_SPIN_TARGET_MS;
        long x = 0L;
        while (SystemClock.elapsedRealtime() < deadline) {
            x ^= (x << 13) ^ (x >>> 7) ^ (x << 17);
            x++;
        }
        Log.w(TAG, "mainThreadCpuSpin: end, x=" + x);
        Toast.makeText(this, R.string.toast_cpu_spin_done, Toast.LENGTH_SHORT).show();
    }

    /** 主线程同步磁盘读写 + {@link java.io.FileDescriptor#sync()}，模拟 I/O 阻塞。 */
    private void mainThreadDiskIo() {
        Log.w(TAG, "mainThreadDiskIo: start");
        byte[] buf = new byte[8192];
        File file = new File(getFilesDir(), "anr_demo_io_blob");
        try {
            try (FileOutputStream out = new FileOutputStream(file)) {
                for (int i = 0; i < 2048; i++) {
                    out.write(buf);
                }
                out.getFD().sync();
            }
            try (FileInputStream in = new FileInputStream(file)) {
                while (in.read(buf) != -1) {
                    // drain
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "mainThreadDiskIo", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        Log.w(TAG, "mainThreadDiskIo: end");
        Toast.makeText(this, R.string.toast_io_done, Toast.LENGTH_SHORT).show();
    }

    /**
     * 连续 {@link SharedPreferences.Editor#commit()}：每次提交会阻塞直至落盘，
     * 主线程上大量调用易触发 ANR（演示向；新项目应使用 {@code apply()} 或 DataStore）。
     */
    private void mainThreadPrefsCommit() {
        Log.w(TAG, "mainThreadPrefsCommit: start");
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        for (int i = 0; i < 500; i++) {
            boolean ok = prefs.edit().putInt("k" + i, i).commit();
            if (!ok) {
                Log.e(TAG, "commit failed at i=" + i);
                break;
            }
        }
        Log.w(TAG, "mainThreadPrefsCommit: end");
        Toast.makeText(this, R.string.toast_prefs_done, Toast.LENGTH_SHORT).show();
    }

    /**
     * 主线程同步查询已安装应用列表，可能触发跨进程 Binder 等待，设备/应用数量大时更明显。
     */
    private void mainThreadPackageManagerQuery() {
        Log.w(TAG, "mainThreadPackageManagerQuery: start");
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Log.w(TAG, "mainThreadPackageManagerQuery: count=" + apps.size());
        Toast.makeText(
                        this,
                        getString(R.string.toast_pkg_count, apps.size()),
                        Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * 主线程持锁 A 等待 B，后台线程持 B 并在主线程等待 A，形成死锁，界面永久无响应直至系统 ANR/杀进程。
     */
    private void mainThreadDeadlock() {
        Log.w(TAG, "mainThreadDeadlock: start");
        final Object lockA = new Object();
        final Object lockB = new Object();

        Thread background = new Thread(() -> {
            synchronized (lockB) {
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                runOnUiThread(() -> {
                    synchronized (lockA) {
                        Log.w(TAG, "UI thread entered lockA (should not happen if deadlocked)");
                    }
                });
            }
        }, "anr-deadlock-bg");
        background.start();

        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        synchronized (lockA) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            synchronized (lockB) {
                Log.w(TAG, "main holds A and B (should not happen if deadlocked)");
            }
        }
    }

    /** 发送显式广播，由 {@link HeavyBroadcastReceiver} 在主线程长时间阻塞。 */
    private void sendHeavyBroadcast() {
        Intent intent = new Intent(this, HeavyBroadcastReceiver.class);
        sendBroadcast(intent);
        Toast.makeText(this, R.string.toast_broadcast_sent, Toast.LENGTH_SHORT).show();
    }
}
