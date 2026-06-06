package com.example.logging.custom;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.logging.custom.utils.LogUtil;
import com.example.logging.custom.utils.NetworkUtil;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView tvCurrentStatus;
    private TextView tvModeInfo;
    private Button btnTestLog;
    private Button btnTestNetwork;
    private Button btnTestJson;
    private Button btnTestError;
    private Button btnToggleLog;

    private boolean logEnabled = true;

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

        LogUtil.d(TAG, "Activity 创建");
        initViews();
        setupListeners();
        updateModeInfo();
    }

    private void initViews() {
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
        tvModeInfo = findViewById(R.id.tvModeInfo);
        btnTestLog = findViewById(R.id.btnTestLog);
        btnTestNetwork = findViewById(R.id.btnTestNetwork);
        btnTestJson = findViewById(R.id.btnTestJson);
        btnTestError = findViewById(R.id.btnTestError);
        btnToggleLog = findViewById(R.id.btnToggleLog);
    }

    private void updateModeInfo() {
        String modeText = String.format(
                "模式: %s\n日志级别: %s",
                logEnabled ? "调试模式" : "生产模式",
                getLogLevelName(LogUtil.LOG_LEVEL)
        );
        tvModeInfo.setText(modeText);
    }

    private String getLogLevelName(int level) {
        switch (level) {
            case LogUtil.VERBOSE: return "VERBOSE";
            case LogUtil.DEBUG: return "DEBUG";
            case LogUtil.INFO: return "INFO";
            case LogUtil.WARN: return "WARN";
            case LogUtil.ERROR: return "ERROR";
            default: return "UNKNOWN";
        }
    }

    private void setupListeners() {
        btnTestLog.setOnClickListener(v -> {
            LogUtil.d(TAG, "测试日志按钮被点击");
            testDifferentLogLevels();
        });

        btnTestNetwork.setOnClickListener(v -> {
            LogUtil.d(TAG, "测试网络按钮被点击");
            testNetworkRequest();
        });

        btnTestJson.setOnClickListener(v -> {
            LogUtil.d(TAG, "测试JSON按钮被点击");
            testJsonLogging();
        });

        btnTestError.setOnClickListener(v -> {
            LogUtil.d(TAG, "测试错误按钮被点击");
            testErrorLogging();
        });

        btnToggleLog.setOnClickListener(v -> {
            logEnabled = !logEnabled;
            LogUtil.setDebug(logEnabled);

            String status = logEnabled ? "启用" : "禁用";
            tvCurrentStatus.setText("日志输出已" + status);
            updateModeInfo();

            Toast.makeText(this, "日志输出已" + status, Toast.LENGTH_SHORT).show();
            LogUtil.d(TAG, "日志输出已" + status);
        });
    }

    /**
     * 测试不同日志级别
     */
    private void testDifferentLogLevels() {
        tvCurrentStatus.setText("正在测试不同日志级别...");

        LogUtil.v(TAG, "这是一条Verbose级别日志");
        LogUtil.d(TAG, "这是一条Debug级别日志");
        LogUtil.i(TAG, "这是一条Info级别日志");
        LogUtil.w(TAG, "这是一条Warning级别日志");
        LogUtil.e(TAG, "这是一条Error级别日志");

        tvCurrentStatus.setText("不同级别日志已输出");
        Toast.makeText(this, "请查看Logcat", Toast.LENGTH_SHORT).show();
    }

    /**
     * 测试网络请求
     */
    private void testNetworkRequest() {
        tvCurrentStatus.setText("正在测试网络请求...");

        // 检查网络
        boolean isNetworkAvailable = NetworkUtil.isNetworkAvailable(this);
        LogUtil.i(TAG, "网络状态: " + (isNetworkAvailable ? "可用" : "不可用"));

        if (isNetworkAvailable) {
            // 模拟 POST 请求
            Map<String, String> params = new HashMap<>();
            params.put("username", "testuser");
            params.put("password", "123456"); // 敏感信息会被过滤
            params.put("email", "test@example.com");

            NetworkUtil.postRequest("https://api.example.com/login", params,
                    new NetworkUtil.NetworkCallback() {
                        @Override
                        public void onSuccess(String response) {
                            runOnUiThread(() -> {
                                tvCurrentStatus.setText("网络请求成功");
                                Toast.makeText(MainActivity.this,
                                        "请求成功，请查看Logcat", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                tvCurrentStatus.setText("网络请求失败");
                                Toast.makeText(MainActivity.this,
                                        "请求失败: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
        } else {
            tvCurrentStatus.setText("网络不可用");
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 测试 JSON 日志
     */
    private void testJsonLogging() {
        tvCurrentStatus.setText("正在测试JSON日志...");

        try {
            // 模拟 JSON 响应
            JSONObject json = new JSONObject();
            json.put("status", "success");
            json.put("data", new JSONObject()
                    .put("id", 1)
                    .put("name", "张三")
                    .put("age", 25)
                    .put("email", "zhangsan@example.com")
                    .put("address", new JSONObject()
                            .put("city", "北京")
                            .put("street", "朝阳区")
                    )
            );
            json.put("timestamp", System.currentTimeMillis());

            String jsonString = json.toString();
            LogUtil.json(TAG, jsonString);

            // 测试无效 JSON
            LogUtil.json(TAG, "{invalid json");

            tvCurrentStatus.setText("JSON日志已输出");
            Toast.makeText(this, "JSON已格式化输出到Logcat", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            LogUtil.e(TAG, "JSON 生成失败", e);
            tvCurrentStatus.setText("JSON生成失败");
        }
    }

    /**
     * 测试错误日志
     */
    private void testErrorLogging() {
        tvCurrentStatus.setText("正在测试错误日志...");

        try {
            // 模拟异常
            String str = null;
            int length = str.length(); // 这里会抛 NullPointerException

        } catch (Exception e) {
            LogUtil.e(TAG, "捕获到空指针异常", e);
            tvCurrentStatus.setText("异常已捕获并记录");
            Toast.makeText(this, "异常已记录到Logcat", Toast.LENGTH_SHORT).show();
        }

        // 测试性能追踪
        testPerformanceTracking();
    }

    private void testPerformanceTracking() {
        LogUtil.PerformanceTracker tracker = LogUtil.trackPerformance(TAG, "模拟耗时操作");

        new Thread(() -> {
            try {
                // 模拟耗时操作
                Thread.sleep(1500);

                runOnUiThread(() -> {
                    tracker.finish();
                    tvCurrentStatus.setText("性能追踪完成");
                    Toast.makeText(this,
                            "性能追踪完成，请查看Logcat", Toast.LENGTH_SHORT).show();
                });
            } catch (InterruptedException e) {
                LogUtil.e(TAG, "线程中断", e);
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "Activity 开始");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "Activity 恢复");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "Activity 暂停");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "Activity 停止");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "Activity 销毁");
    }
}