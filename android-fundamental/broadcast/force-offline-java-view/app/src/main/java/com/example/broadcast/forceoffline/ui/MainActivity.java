package com.example.broadcast.forceoffline.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.broadcast.forceoffline.R;
import com.example.broadcast.forceoffline.base.BaseActivity;

public class MainActivity extends BaseActivity {
    private TextView tvWelcome;
    private Button btnForceOffline;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupClickListeners();

        // 显示欢迎信息
        String username = getIntent().getStringExtra("username");
        tvWelcome.setText("欢迎，" + (username != null ? username : "用户"));
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        btnForceOffline = findViewById(R.id.btn_force_offline);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupClickListeners() {
        // 模拟强制下线按钮
        btnForceOffline.setOnClickListener(v -> {
            Intent intent = new Intent("com.example.broadcast.force.offline.FORCE_OFFLINE");
            sendBroadcast(intent);
        });

        // 正常退出按钮
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}