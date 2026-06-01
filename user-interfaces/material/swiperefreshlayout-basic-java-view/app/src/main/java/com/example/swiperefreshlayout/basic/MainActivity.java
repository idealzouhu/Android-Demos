package com.example.swiperefreshlayout.basic;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeContainer;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataItems;

    // 模拟数据源
    private static final String[] SAMPLE_DATA = {
            "Henry IV", "Henry VIII", "Richard II", "Richard III", "King Lear",
            "数据项 1", "数据项 2", "数据项 3", "数据项 4", "数据项 5"
    };

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

        // 初始化Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化数据
        initializeData();

        // 初始化ListView适配器
        initializeListView();

        // 配置SwipeRefreshLayout
        setupSwipeRefreshLayout();
    }

    private void initializeData() {
        dataItems = new ArrayList<>(Arrays.asList(SAMPLE_DATA));
    }

    private void initializeListView() {
        listView = findViewById(R.id.lvItems);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, dataItems);
        listView.setAdapter(adapter);
    }

    /**
     * 配置 SwipeRefreshLayout
     */
    private void setupSwipeRefreshLayout() {
        swipeContainer = findViewById(R.id.swipeContainer);

        // 设置刷新监听器, 在这里执行刷新任务
        swipeContainer.setOnRefreshListener(this::refreshData);

        // 配置刷新动画颜色
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        // 设置下拉进度圈背景颜色
        swipeContainer.setProgressBackgroundColorSchemeResource(android.R.color.background_light);
    }

    private void refreshData() {
        // 模拟网络请求延迟
        new Handler().postDelayed(() -> {
            // 在UI线程执行数据更新
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 添加新数据
                    addNewData();

                    // 通知适配器数据已更新
                    adapter.notifyDataSetChanged();

                    // 停止刷新动画
                    swipeContainer.setRefreshing(false);

                    Toast.makeText(MainActivity.this,
                            "数据已更新，新增 " + 3 + " 条记录",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }, 2000); // 2秒延迟模拟网络请求
    }

    private void addNewData() {
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            String newItem = "新数据项 " + System.currentTimeMillis() +
                    " - " + random.nextInt(100);
            dataItems.add(0, newItem); // 添加到开头
        }
    }
}