package com.example.multi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private EditText etInput;
    private Button btnConfirm, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initViews();
        displayRequestData();
        setupClickListeners();
    }

    private void initViews() {
        etInput = findViewById(R.id.et_input);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void displayRequestData() {
        Intent intent = getIntent();
        String requestData = intent.getStringExtra("request_data");
        if (requestData != null) {
            etInput.setHint("处理: " + requestData);
        }
    }

    private void setupClickListeners() {
        btnConfirm.setOnClickListener(v -> returnResult(true));
        btnCancel.setOnClickListener(v -> returnResult(false));
    }

    private void returnResult(boolean confirmed) {
        Intent resultIntent = new Intent();

        if (confirmed) {
            String inputText = etInput.getText().toString();
            if (!inputText.isEmpty()) {
                resultIntent.putExtra("returned_data", inputText);
                resultIntent.putExtra("processed_value", inputText.length());
                setResult(RESULT_OK, resultIntent);
            } else {
                setResult(RESULT_CANCELED);
            }
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();
    }
}