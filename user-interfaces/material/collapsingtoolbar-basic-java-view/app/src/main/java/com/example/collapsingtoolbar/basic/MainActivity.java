package com.example.collapsingtoolbar.basic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private Button btnEnterDetail;
    private MaterialCardView materialCardView;

    private Toolbar toolbar;

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
        setupToolbar();
        setupClickListeners();
    }

    private void initViews() {
        btnEnterDetail = findViewById(R.id.btn_enter_detail);
        materialCardView = findViewById(R.id.card_view);
    }

    private void setupToolbar() {
        // 将Toolbar设置为当前 Activity 的 ActionBar
        setSupportActionBar(toolbar);

        // 可选：设置标题（如果不在XML中设置的话）
        // if (getSupportActionBar() != null) {
        //     getSupportActionBar().setTitle("主页");
        // }

        // 可选：显示返回按钮（如果不需要可以省略）
        // if (getSupportActionBar() != null) {
        //     getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // }
    }

    private void setupClickListeners() {
        // 为按钮设置点击监听
        btnEnterDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDetail();
            }
        });

        // 为整个卡片设置点击监听，提升交互友好度
        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDetail();
            }
        });
    }

    private void navigateToDetail() {
        // 创建Intent，从主页面跳转到详情页
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        // 可以通过Intent传递数据，例如标题或图片资源ID
        intent.putExtra("TITLE", "Material Design 演示");
        intent.putExtra("IMAGE_RES_ID", R.drawable.header_bg);
        startActivity(intent);

        // 可以添加页面切换动画 (可选)
        // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}