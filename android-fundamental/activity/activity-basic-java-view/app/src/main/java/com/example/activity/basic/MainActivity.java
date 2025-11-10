package com.example.activity.basic;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView toolbarTitle;
    private TextView tvStatus;
    private ImageButton btnMenu;
    private Button btnShowToast;
    private Button btnChangeTitle;

    private int clickCount = 0;

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

        // 初始化视图
        initViews();

        // 设置点击监听器
        setupClickListeners();

        // 更新状态显示
        updateStatus("Activity 已创建 (onCreate)");

        Toast.makeText(this, "Activity Created", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvStatus = findViewById(R.id.tv_status);
        btnMenu = findViewById(R.id.btn_menu);
        btnShowToast = findViewById(R.id.btn_show_toast);
        btnChangeTitle = findViewById(R.id.btn_change_title);
    }

    private void setupClickListeners() {
        // 菜单按钮点击事件
        btnMenu.setOnClickListener(this::showPopupMenu);

        // 显示Toast按钮
        btnShowToast.setOnClickListener(v -> {
            clickCount++;
            Toast.makeText(MainActivity.this,
                    "按钮被点击了 " + clickCount + " 次！",
                    Toast.LENGTH_SHORT).show();
            updateStatus("Toast 消息已显示，点击次数：" + clickCount);
        });

        // 修改标题按钮
        btnChangeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                String[] titles = {
                        "主页面",
                        "Activity 演示",
                        "Java + View",
                        "Android 开发"
                };
                String newTitle = titles[clickCount % titles.length];
                toolbarTitle.setText(newTitle);
                updateStatus("标题已修改为：" + newTitle);
            }
        });
    }

    /**
     * 显示弹出菜单（替代选项菜单）
     */
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        popupMenu.show();
    }

    // /**
    //  * 创建选项菜单（ ActionBar 菜单）
    //  */
    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    //     getMenuInflater().inflate(R.menu.main_menu, menu);
    //     return true;
    // }

    /**
     * 处理菜单项点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_search) {
            showToast("搜索功能");
            updateStatus("点击了搜索菜单");
            return true;
        } else if (itemId == R.id.action_share) {
            showToast("分享功能");
            updateStatus("点击了分享菜单");
            return true;
        } else if (itemId == R.id.action_settings) {
            showToast("设置功能");
            updateStatus("点击了设置菜单");
            return true;
        } else if (itemId == R.id.action_about) {
            showToast("关于我们");
            updateStatus("点击了关于菜单");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示Toast消息
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 更新状态显示
     */
    private void updateStatus(String status) {
        tvStatus.setText("当前状态：" + status);
    }

    // ========== Activity 生命周期方法 ==========

    @Override
    protected void onStart() {
        super.onStart();
        updateStatus("Activity 已启动 (onStart)");
        Toast.makeText(this, "Activity Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus("Activity 已恢复 (onResume)");
        Toast.makeText(this, "Activity Resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateStatus("Activity 已暂停 (onPause)");
        Toast.makeText(this, "Activity Paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateStatus("Activity 已停止 (onStop)");
        Toast.makeText(this, "Activity Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateStatus("Activity 重新启动 (onRestart)");
        Toast.makeText(this, "Activity Restarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Activity Destroyed", Toast.LENGTH_SHORT).show();
    }
}