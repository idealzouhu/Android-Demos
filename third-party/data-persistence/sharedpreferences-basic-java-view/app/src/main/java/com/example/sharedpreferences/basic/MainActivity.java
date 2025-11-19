package com.example.sharedpreferences.basic;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private CheckBox cbRemember;
    private Button btnSave;
    private Button btnLoad;
    private Button btnClear;
    private SettingsManager settingsManager;

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
        settingsManager = new SettingsManager(this); // 初始化 SettingsManager
        loadSavedSettings(); // 自动加载保存的设置
        setupClickListeners();
    }

    /**
     * 初始化所有界面控件
     */
    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);
        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);
        btnClear = findViewById(R.id.btnClear);
    }

    private void setupClickListeners() {
        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> saveSettings());

        // 加载按钮点击事件
        btnLoad.setOnClickListener(v -> {
            loadSavedSettings();
            Toast.makeText(MainActivity.this, "配置已加载", Toast.LENGTH_SHORT).show();
        });

        // 清除按钮点击事件
        btnClear.setOnClickListener(v -> clearSettings());
    }


    /**
     * 保存设置到SharedPreferences
     */
    private void saveSettings() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        boolean rememberMe = cbRemember.isChecked();

        // 输入验证
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rememberMe && password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 使用 SettingsManager 保存，而非直接操作 SharedPreferences
        settingsManager.saveUserCredentials(username, password, rememberMe);

        String message = rememberMe ? "用户名和密码已保存" : "用户名已保存";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载保存的设置
     * <p>
     * 读取保存的设置，并填充到界面控件中 （用户名、密码、记住密码复选框）
     * 注意：当勾选“记住我”时才填充密码框
     */
    private void loadSavedSettings() {
        // 使用 SettingsManager 读取
        String savedUsername = settingsManager.getUsername();
        boolean rememberMe = settingsManager.isRememberPassword();

        etUsername.setText(savedUsername);
        cbRemember.setChecked(rememberMe);

        if (rememberMe) {
            // 只有勾选“记住我”时才填充密码框
            String savedPassword = settingsManager.getPassword();
            etPassword.setText(savedPassword);
        } else {
            etPassword.setText(""); // 确保不记住时密码框为空
        }
    }

    private void clearSettings() {
        // 使用 SettingsManager 清除
        settingsManager.clearUserCredentials();
        etUsername.setText("");
        etPassword.setText("");
        cbRemember.setChecked(false);
        Toast.makeText(this, "所有数据已清除", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 可选：在Activity停止时自动保存设置
        if (cbRemember.isChecked()) {
            saveSettings();
        }
    }
}