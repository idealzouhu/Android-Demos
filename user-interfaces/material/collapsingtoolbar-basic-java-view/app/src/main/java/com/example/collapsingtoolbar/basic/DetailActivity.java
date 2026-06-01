package com.example.collapsingtoolbar.basic;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class DetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView headerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews();
        setupToolbar();
        setupSystemUi();
        receiveDataFromIntent();
    }

    private void initViews() {
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        headerImage = findViewById(R.id.header_image);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        // 显示返回箭头
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 配置CollapsingToolbarLayout
        collapsingToolbar.setTitle("详情页面"); // 默认标题
        // 设置标题在展开和折叠状态下的颜色
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
    }

    private void setupSystemUi() {
        // 实现沉浸式状态栏（Android 5.0及以上）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void receiveDataFromIntent() {
        // 接收从MainActivity传递过来的数据
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("TITLE", "默认标题");
            int imageResId = extras.getInt("IMAGE_RES_ID", -1);

            collapsingToolbar.setTitle(title); // 设置从主页面传来的标题
            if (imageResId != -1) {
                headerImage.setImageResource(imageResId); // 设置从主页面传来的图片
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 处理工具栏上返回箭头的点击事件
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}