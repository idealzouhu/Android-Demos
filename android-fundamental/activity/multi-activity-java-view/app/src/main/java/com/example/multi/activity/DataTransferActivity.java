package com.example.multi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.multi.model.User;

public class DataTransferActivity extends AppCompatActivity {

    private TextView tvProcessedData;
    private Button btnProcess;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);

        initViews();
        processReceivedData();
        setupClickListeners();
    }

    private void initViews() {
        tvProcessedData = findViewById(R.id.tv_processed_data);
        btnProcess = findViewById(R.id.btn_process);
        btnBack = findViewById(R.id.btn_back);
    }

    private void processReceivedData() {
        Intent intent = getIntent();
        StringBuilder sb = new StringBuilder();
        sb.append("接收到的数据:\n\n");

        // 处理各种类型的数据
        String stringData = intent.getStringExtra("string_data");
        int intData = intent.getIntExtra("int_data", 0);

        sb.append("字符串数据: ").append(stringData).append("\n");
        sb.append("整数数据: ").append(intData).append("\n\n");

        tvProcessedData.setText(sb.toString());
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnProcess.setOnClickListener(v -> {
            // 处理数据并返回结果
            Intent resultIntent = new Intent();
            resultIntent.putExtra("processing_result", "数据已成功处理");
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}