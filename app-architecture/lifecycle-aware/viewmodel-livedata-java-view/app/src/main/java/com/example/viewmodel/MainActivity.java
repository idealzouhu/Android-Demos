package com.example.viewmodel;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CounterViewModel viewModel;
    private TextView tvCounter;
    private TextView tvMessage;
    private Button btnIncrement;
    private Button btnDecrement;
    private Button btnReset;
    private ConstraintLayout rootLayout;

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

        initializeViews();
        initializeViewModel();
        setupObservers();
        setupClickListeners();
    }

    private void initializeViews() {
        tvCounter = findViewById(R.id.tv_counter);
        tvMessage = findViewById(R.id.tv_message);
        btnIncrement = findViewById(R.id.btn_increment);
        btnDecrement = findViewById(R.id.btn_decrement);
        btnReset = findViewById(R.id.btn_reset);
        rootLayout = findViewById(R.id.main);
    }

    private void initializeViewModel() {
        ViewModelProvider.Factory factory = new CounterViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(CounterViewModel.class);

        // 显示 ViewModel 状态
        Toast.makeText(this, "ViewModel 已初始化", Toast.LENGTH_SHORT).show();
    }

    private void setupObservers() {
        // 观察计数器变化
        viewModel.getCounter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer value) {
                tvCounter.setText(String.valueOf(value));
                updateBackgroundColor(value);
            }
        });

        // 观察消息变化
        viewModel.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                tvMessage.setText(message);
            }
        });

        // 观察重置按钮状态
        viewModel.getIsResetEnabled().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isEnabled) {
                btnReset.setEnabled(isEnabled);
                btnReset.setAlpha(isEnabled ? 1.0f : 0.5f);
            }
        });
    }

    private void setupClickListeners() {
        btnIncrement.setOnClickListener(v -> {
            viewModel.increment();
            showToast("增加计数");
        });

        btnDecrement.setOnClickListener(v -> {
            viewModel.decrement();
            showToast("减少计数");
        });

        btnReset.setOnClickListener(v -> {
            viewModel.reset();
            showToast("计数器已重置");
        });
    }

    private void updateBackgroundColor(int value) {
        int colorRes;
        if (value > 0) {
            colorRes = R.color.positive_green;
        } else if (value < 0) {
            colorRes = R.color.negative_red;
        } else {
            colorRes = R.color.neutral_gray;
        }
        rootLayout.setBackgroundColor(getResources().getColor(colorRes));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 测试配置变化时 ViewModel 的存活
        if (isChangingConfigurations()) {
            Toast.makeText(this, "配置变化中，ViewModel 存活", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Activity 被销毁，ViewModel 将被清理", Toast.LENGTH_SHORT).show();
        }
    }
}