package com.example.multi.activity;

import static android.content.pm.PackageManager.MATCH_ALL;
import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.test.core.app.ApplicationProvider;

import com.example.multi.model.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_DATA_TRANSFER = 1001;
    private static final int REQUEST_CODE_RESULT = 1002;

    private TextView tvResult;

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
        setupClickListeners();
    }

    private void initViews() {
        tvResult = findViewById(R.id.tv_result);
    }

    private void setupClickListeners() {
        // 1. 显式Intent - 启动程序内Activity
        findViewById(R.id.btn_explicit).setOnClickListener(v -> {
            startInternalActivity();
        });

        // 2. 隐式Intent - 启动浏览器（外部应用）
        findViewById(R.id.btn_implicit_browser).setOnClickListener(v -> {
            openWebPage();
        });

        // 3. 隐式Intent - 启动程序内的WebViewActivity（响应相同协议）
        findViewById(R.id.btn_implicit_internal).setOnClickListener(v -> {
            openInternalWebView();
        });

        // 4. 隐式Intent - 拨打电话
        findViewById(R.id.btn_call).setOnClickListener(v -> {
            makePhoneCall();
        });

        // 5. 启动Activity并传递数据
        findViewById(R.id.btn_data_transfer).setOnClickListener(v -> {
            startDataTransferActivity();
        });

        // 6. 启动Activity并期待返回结果
        findViewById(R.id.btn_start_for_result).setOnClickListener(v -> {
            startActivityForResult();
        });
    }

    // ========== 1. 显式Intent - 启动程序内Activity ==========
    private void startInternalActivity() {
        // 显式Intent：明确指定目标Activity类
        Intent intent = new Intent(MainActivity.this, InternalActivity.class);

        // 传递数据
        intent.putExtra("message", "来自主页面的问候");
        intent.putExtra("timestamp", System.currentTimeMillis());
        intent.putExtra("user_id", 12345);

        // 传递复杂数据
        Bundle bundle = new Bundle();
        bundle.putString("user_name", "张三");
        bundle.putInt("user_age", 25);
        intent.putExtras(bundle);

        startActivity(intent);
        Toast.makeText(this, "使用显式Intent启动内部页面", Toast.LENGTH_SHORT).show();
    }

    // ========== 2. 隐式Intent - 启动外部浏览器 ==========
    @SuppressLint("QueryPermissionsNeeded")
    private void openWebPage() {
        // 隐式Intent：描述要执行的操作，系统选择处理程序
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.baidu.com"));

        // 验证是否有应用可以处理
        try {
            // 方法1：尝试使用选择器
            Intent chooser = Intent.createChooser(intent, "选择浏览器");
            startActivity(chooser);
        } catch (ActivityNotFoundException e1) {
            try {
                // 方法2：选择器失败，尝试直接启动
                startActivity(intent);
            } catch (ActivityNotFoundException e2) {
                try {
                    // 方法3：尝试使用系统默认浏览器
                    intent.setPackage(null); // 清除包名限制
                    startActivity(intent);
                } catch (ActivityNotFoundException e3) {
                    // 最终处理：确实没有浏览器
                    new AlertDialog.Builder(this)
                            .setTitle("无法打开网页")
                            .setMessage("您的设备上没有安装浏览器应用。您可以选择：")
                            .setPositiveButton("确定", null)
                            .show();
                }
            }
        }
    }

    // ========== 3. 隐式Intent - 启动程序内WebViewActivity ==========
    private void openInternalWebView() {
        // 隐式Intent：使用相同的ACTION_VIEW和http协议
        // 系统会显示选择器，用户可以选择我们的应用或浏览器
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://developer.android.com"));

        // 调试方法
        debugIntentResolution();

        // 创建选择器标题
        Intent chooser = Intent.createChooser(intent, "选择打开方式");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            Toast.makeText(this, "没有应用可以处理该请求", Toast.LENGTH_SHORT).show();
        }
    }

    // 调试方法：检查哪些应用能处理此 Intent
    private void debugIntentResolution() {
        PackageManager pm = getPackageManager();

        // 创建用于测试的Intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://developer.android.com"));

        // 创建新的Intent用于当前应用查询
        Intent selfIntent = new Intent(intent);
        selfIntent.setPackage(getPackageName());
        List<ResolveInfo> selfActivities = pm.queryIntentActivities(selfIntent, MATCH_DEFAULT_ONLY );
        Log.d("IntentDebug", "当前应用可处理的数量: " + selfActivities.size());
        for (ResolveInfo info : selfActivities) {
            Log.d("IntentDebug", "应用: " + info.activityInfo.packageName);
        }

        // 创建新的Intent用于全局查询
        Intent globalIntent = new Intent(intent);
        globalIntent.setPackage(null);
        // List<ResolveInfo> allActivities = pm.queryIntentActivities(globalIntent, MATCH_ALL );
        List<ResolveInfo> allActivities = pm.queryIntentActivities(globalIntent, MATCH_DEFAULT_ONLY );
        Log.d("IntentDebug", "全局可处理的数量: " + allActivities.size());
        for (ResolveInfo info : allActivities) {
            Log.d("IntentDebug", "应用: " + info.activityInfo.packageName);
        }
    }

    // ========== 4. 隐式Intent - 拨打电话 ==========
    private void makePhoneCall() {
        // 方法1：直接拨号（需要权限）
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:13800138000"));

            // 检查权限
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            } else {
                // 如果没有权限，请求权限
                requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                Toast.makeText(this, "请授权拨打电话权限", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "拨打电话权限被拒绝", Toast.LENGTH_SHORT).show();
        }
    }

    // 安全的拨号方法（不需要权限）
    private void dialPhoneNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:13800138000"));
        startActivity(intent);
    }

    // ========== 5. 启动Activity并传递数据 ==========
    private void startDataTransferActivity() {
        Intent intent = new Intent(this, DataTransferActivity.class);

        // 传递各种类型的数据
        intent.putExtra("string_data", "Hello World");
        intent.putExtra("int_data", 42);
        intent.putExtra("boolean_data", true);
        intent.putExtra("double_data", 3.14159);

        // 传递数组
        String[] stringArray = {"苹果", "香蕉", "橙子"};
        intent.putExtra("string_array", stringArray);

        // 传递对象（需要实现Serializable或Parcelable）
        User user = new User("李四", "lisi@example.com", 30);
        intent.putExtra("user_object", user);

        startActivityForResult(intent, REQUEST_CODE_DATA_TRANSFER);
    }

    // ========== 6. 启动Activity并期待返回结果 ==========
    private void startActivityForResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("request_data", "请处理这个数据并返回结果");
        startActivityForResult(intent, REQUEST_CODE_RESULT);
    }

    // ========== 处理返回结果 ==========
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_DATA_TRANSFER:
                    String result = data.getStringExtra("processing_result");
                    tvResult.setText("数据传递结果: " + result);
                    break;

                case REQUEST_CODE_RESULT:
                    String returnedData = data.getStringExtra("returned_data");
                    int processedValue = data.getIntExtra("processed_value", 0);
                    tvResult.setText(String.format("返回结果: %s (值: %d)", returnedData, processedValue));
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            tvResult.setText("用户取消了操作");
        }
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // 权限被授予，重新拨号
            makePhoneCall();
        }
    }
}