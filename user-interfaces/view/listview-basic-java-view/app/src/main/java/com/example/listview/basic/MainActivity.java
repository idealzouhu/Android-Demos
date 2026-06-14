package com.example.listview.basic;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 列表视图
 * <p>
 * ListView渲染关键知识点：
 * 1. 当ListView绑定适配器时，系统会自动调用适配器的 {@link FruitAdapter#getView} 方法来渲染可见的列表项
 * 2. 当数据发生改变时，需要主动调用 {@link FruitAdapter#notifyDataSetChanged()} 通知ListView刷新界面，
 *    系统会重新调用 {@link FruitAdapter#getView} 方法来更新受影响的列表项视图
 */
public class MainActivity extends AppCompatActivity {

    private ListView fruitListView;
    private TextView emptyView;
    private TextView itemCountText;
    private Button btnAdd, btnClear;
    private FruitAdapter adapter;
    private List<Fruit> fruitList;

    // 模拟水果数据
    private final String[] fruitNames = {"苹果", "香蕉", "橙子", "西瓜", "葡萄", "草莓", "菠萝", "芒果"};
    private final String[] fruitDescriptions = {
            "新鲜红富士苹果，甜脆多汁",
            "海南香蕉，香甜软糯",
            "赣南脐橙，酸甜可口",
            "宁夏西瓜，沙甜多汁",
            "新疆葡萄，无籽香甜",
            "丹东草莓，鲜红饱满",
            "海南菠萝，香气浓郁",
            "广西芒果，细腻香甜"
    };
    private final int[] fruitImages = {
            R.drawable.ic_fruit_default,  // 使用统一的默认图片
            R.drawable.ic_fruit_default,
            R.drawable.ic_fruit_default,
            R.drawable.ic_fruit_default,
            R.drawable.ic_fruit_default,
            R.drawable.ic_fruit_default,
            R.drawable.ic_fruit_default,
            R.drawable.ic_fruit_default
    };
    private final double[] basePrices = {8.8, 4.5, 6.8, 2.5, 12.8, 25.6, 9.9, 15.8};

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
        initData();
        setupListView();
        setupClickListeners();
    }

    private void initViews() {
        fruitListView = findViewById(R.id.fruit_list_view);
        emptyView = findViewById(R.id.empty_view);
        itemCountText = findViewById(R.id.item_count);
        btnAdd = findViewById(R.id.btn_add);
        btnClear = findViewById(R.id.btn_clear);

        // 设置空视图
        fruitListView.setEmptyView(emptyView);
    }

    private void initData() {
        fruitList = new ArrayList<>();
        // 添加一些初始数据
        addSampleFruits(3);
    }

    /**
     * 绑定列表适配器
     * <p>
     * 在绑定适配器时，会调用 getView 方法，将数据绑定到视图组件上
     */
    private void setupListView() {
        adapter = new FruitAdapter(this, fruitList);
        fruitListView.setAdapter(adapter);
        updateItemCount();
    }

    private void setupClickListeners() {
        // 列表项点击事件
        fruitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fruit fruit = fruitList.get(position);
                showFruitDetail(fruit);

                // 添加点击反馈
                view.setPressed(true);
                view.postDelayed(() -> view.setPressed(false), 100);
            }
        });

        // 列表项长按事件
        fruitListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });

        // 添加水果按钮点击事件
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRandomFruit();
            }
        });

        // 清空列表按钮点击事件
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearAllDialog();
            }
        });
    }

    private void addSampleFruits(int count) {
        Random random = new Random();
        for (int i = 0; i < count && i < fruitNames.length; i++) {
            int index = random.nextInt(fruitNames.length);
            Fruit fruit = new Fruit(
                    fruitNames[index],
                    fruitImages[index % fruitImages.length], // 防止数组越界
                    fruitDescriptions[index],
                    basePrices[index] + random.nextDouble() * 5
            );
            fruitList.add(fruit);
        }
        updateItemCount();
    }

    private void addRandomFruit() {
        Random random = new Random();
        int index = random.nextInt(fruitNames.length);

        Fruit fruit = new Fruit(
                fruitNames[index],
                fruitImages[index % fruitImages.length],
                fruitDescriptions[index],
                basePrices[index] + random.nextDouble() * 5
        );

        adapter.addFruit(fruit);
        updateItemCount();

        Toast.makeText(this, "添加了: " + fruit.getName(), Toast.LENGTH_SHORT).show();
    }

    private void showFruitDetail(Fruit fruit) {
        new AlertDialog.Builder(this)
                .setTitle(fruit.getName())
                .setMessage("描述: " + fruit.getDescription() +
                        "\n价格: ¥" + String.format("%.2f/斤", fruit.getPrice()))
                .setPositiveButton("确定", null)
                .show();
    }

    private void showDeleteDialog(final int position) {
        Fruit fruit = fruitList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("删除水果")
                .setMessage("确定要删除 " + fruit.getName() + " 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    adapter.removeFruit(position);
                    updateItemCount();
                    Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showClearAllDialog() {
        if (fruitList.isEmpty()) {
            Toast.makeText(this, "列表已经是空的", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("清空列表")
                .setMessage("确定要清空所有水果吗？")
                .setPositiveButton("清空", (dialog, which) -> {
                    fruitList.clear();
                    adapter.notifyDataSetChanged();
                    updateItemCount();
                    Toast.makeText(MainActivity.this, "已清空列表", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateItemCount() {
        itemCountText.setText("共" + fruitList.size() + "件商品");
    }
}