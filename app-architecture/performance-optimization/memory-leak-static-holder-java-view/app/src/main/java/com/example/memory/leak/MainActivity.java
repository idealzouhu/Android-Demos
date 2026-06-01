package com.example.memory.leak;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

/**
 * 各按钮注册一类典型泄漏；返回或旋转后，在 Debug 构建中可由 LeakCanary 提示引用链。
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    /** Handler 延迟任务：足够长以便离开 Activity 后仍排队在主线程 Looper 中。 */
    private static final long HANDLER_DELAY_MS = 120_000L;

    /** 后台线程睡眠时长，期间持有 Activity 引用。 */
    private static final long THREAD_SLEEP_MS = 120_000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);
        logLife("onCreate");

        findViewById(R.id.btn_finish_activity).setOnClickListener(v -> finish());

        findViewById(R.id.btn_static_activity)
                .setOnClickListener(
                        v -> {
                            LeakStaticActivityRef.hold(this);
                            toastRegistered();
                        });

        findViewById(R.id.btn_singleton_context)
                .setOnClickListener(
                        v -> {
                            LeakSingletonContext.get().setContext(this);
                            toastRegistered();
                        });

        findViewById(R.id.btn_handler_delayed)
                .setOnClickListener(
                        v -> {
                            LeakHandlerDelayed.postDelayedActivityRef(this, HANDLER_DELAY_MS);
                            toastRegistered();
                        });

        findViewById(R.id.btn_thread_hold)
                .setOnClickListener(
                        v -> {
                            LeakThreadHold.startSleepingThread(this, THREAD_SLEEP_MS);
                            toastRegistered();
                        });

        findViewById(R.id.btn_static_view)
                .setOnClickListener(
                        v -> {
                            LeakStaticView.holdWithActivityContext(this);
                            toastRegistered();
                        });

        findViewById(R.id.btn_static_runnable)
                .setOnClickListener(
                        v -> {
                            LeakStaticRunnableRef.hold(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.this.getTitle();
                                        }
                                    });
                            toastRegistered();
                        });

        findViewById(R.id.btn_clear_all)
                .setOnClickListener(
                        v -> {
                            clearAllLeaks();
                            Toast.makeText(this, R.string.toast_cleared, Toast.LENGTH_SHORT).show();
                        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        logLife("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logLife("onResume");
    }

    @Override
    protected void onPause() {
        logLife(
                "onPause, isFinishing="
                        + isFinishing()
                        + ", isChangingConfigurations="
                        + isChangingConfigurations());
        super.onPause();
    }

    @Override
    protected void onStop() {
        logLife("onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        logLife("onDestroy");
        super.onDestroy();
    }

    private void logLife(String event) {
        Log.d(TAG, event + ", instance=" + Integer.toHexString(System.identityHashCode(this)));
    }

    private void toastRegistered() {
        Toast.makeText(this, R.string.toast_registered, Toast.LENGTH_SHORT).show();
    }

    private void clearAllLeaks() {
        LeakStaticActivityRef.clear();
        LeakSingletonContext.get().clear();
        LeakHandlerDelayed.clear();
        LeakThreadHold.clear();
        LeakStaticView.clear();
        LeakStaticRunnableRef.clear();
    }
}
