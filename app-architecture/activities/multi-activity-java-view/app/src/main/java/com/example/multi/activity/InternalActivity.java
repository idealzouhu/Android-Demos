package com.example.multi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InternalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal);

        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 接收传递过来的数据
        Intent intent = getIntent();
        displayReceivedData(intent);
    }

    private void displayReceivedData(Intent intent) {
        TextView tvData = findViewById(R.id.tv_received_data);

        StringBuilder sb = new StringBuilder();
        sb.append("接收到的数据:\n\n");

        // 获取简单数据
        String message = intent.getStringExtra("message");
        long timestamp = intent.getLongExtra("timestamp", 0);
        int userId = intent.getIntExtra("user_id", 0);

        sb.append("消息: ").append(message).append("\n");
        sb.append("时间戳: ").append(timestamp).append("\n");
        sb.append("用户ID: ").append(userId).append("\n\n");

        // 获取Bundle数据
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String userName = bundle.getString("user_name");
            int userAge = bundle.getInt("user_age", 0);
            sb.append("用户名: ").append(userName).append("\n");
            sb.append("用户年龄: ").append(userAge).append("\n");
        }

        tvData.setText(sb.toString());
    }

    // 处理返回按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 自定义返回逻辑
            finish(); // 或其他处理
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}