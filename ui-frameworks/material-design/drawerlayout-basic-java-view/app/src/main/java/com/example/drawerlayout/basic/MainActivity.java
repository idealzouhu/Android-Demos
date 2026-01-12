package com.example.drawerlayout.basic;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.drawerlayout.basic.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupToolbar();
        setupNavigationView();

        setupBackPressedCallback();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        contentText = findViewById(R.id.content_text);
    }

    /**
     * 设置工具栏配置
     * 配置工具栏的基本功能，包括启用操作栏、设置返回按钮显示和自定义菜单图标
     */
    private void setupToolbar() {
        // 将布局中定义的 Toolbar 实例设置为当前 Activity 的 ActionBar
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            // 启用并显示 ActionBar 左上角的“返回”或“导航”按钮
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // 设置导航按钮的图标
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 处理导航菜单项选择事件
     * 当用户点击侧滑菜单中的某个导航项时被调用
     *
     * @param item 被选中的菜单项对象
     * @return 返回true表示事件已处理，false表示未处理
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        String title = item.getTitle().toString();

        if (itemId == R.id.nav_home) {
            contentText.setText("首页内容区域");
        } else if (itemId == R.id.nav_profile) {
            contentText.setText("个人资料页面");
        } else if (itemId == R.id.nav_settings) {
            contentText.setText("系统设置选项");
        } else if (itemId == R.id.nav_share) {
            showSnackbar("分享功能已触发");
        } else if (itemId == R.id.nav_help) {
            contentText.setText("帮助与反馈页面");
        }

        // 更新Toolbar标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        // 关闭侧滑菜单
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 处理选项菜单项的点击事件
     *
     *
     * @param item 被点击的菜单项
     * @return 如果处理了点击事件则返回true，否则返回父类的处理结果
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 处理侧滑菜单开关逻辑
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackbar(String message) {
        Snackbar.make(drawerLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 设置返回键回调处理
     * 用于处理抽屉布局的返回键逻辑，当抽屉打开时关闭抽屉，否则执行默认返回操作
     */
    private void setupBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 处理返回按键逻辑
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // 如果希望执行默认的返回操作，需要禁用回调并触发返回
                    if (isEnabled()) {
                        setEnabled(false);
                        MainActivity.super.onBackPressed();
                    }
                }
            }
        };

        // 注册回调
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}