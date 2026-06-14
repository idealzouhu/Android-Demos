package com.example.recycleview.basic;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 循环列表视图示例
 * <p>
 * 使用 RecyclerView + 循环适配器实现：列表可无限向下/向上滚动，数据按顺序循环展示。
 * 与 listview-basic 功能一致：展示水果列表、添加、清空、点击查看详情、长按删除。
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView fruitCycleView;
    private TextView emptyView;
    private TextView itemCountText;
    private Button btnAdd, btnClear;
    private FruitAdapter adapter;
    private List<Fruit> fruitList;

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
            R.drawable.ic_fruit_default,
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
        setupCycleView();
        setupClickListeners();
    }

    private void initViews() {
        fruitCycleView = findViewById(R.id.fruit_cycle_view);
        emptyView = findViewById(R.id.empty_view);
        itemCountText = findViewById(R.id.item_count);
        btnAdd = findViewById(R.id.btn_add);
        btnClear = findViewById(R.id.btn_clear);
    }

    private void initData() {
        fruitList = new ArrayList<>();
        addSampleFruits(3);
    }

    private void setupCycleView() {
        adapter = new FruitAdapter(this, fruitList);
        fruitCycleView.setLayoutManager(new LinearLayoutManager(this));

        // ItemDecoration 示例：自定义分割线（也可使用系统 DividerItemDecoration）
        int dividerHeightPx = (int) (1 * getResources().getDisplayMetrics().density);
        Drawable divider = ContextCompat.getDrawable(this, R.drawable.divider_list_item);
        if (divider != null) {
            fruitCycleView.addItemDecoration(new ListItemDecoration(divider, dividerHeightPx));
        }

        // ItemAnimator 示例：显式设置默认增删动画，便于观察添加/删除时的过渡效果
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(300);
        animator.setRemoveDuration(300);
        fruitCycleView.setItemAnimator(animator);

        fruitCycleView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, realPosition) -> {
            Fruit fruit = fruitList.get(realPosition);
            showFruitDetail(fruit);
            view.setPressed(true);
            view.postDelayed(() -> view.setPressed(false), 100);
        });
        adapter.setOnItemLongClickListener((view, realPosition) -> {
            showDeleteDialog(realPosition);
            return true;
        });

        updateEmptyVisibility();
        updateItemCount();
    }

    private void setupClickListeners() {
        btnAdd.setOnClickListener(v -> addRandomFruit());
        btnClear.setOnClickListener(v -> showClearAllDialog());
    }

    private void addSampleFruits(int count) {
        Random random = new Random();
        for (int i = 0; i < count && i < fruitNames.length; i++) {
            int index = random.nextInt(fruitNames.length);
            Fruit fruit = new Fruit(
                    fruitNames[index],
                    fruitImages[index % fruitImages.length],
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
        updateEmptyVisibility();
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
                    updateEmptyVisibility();
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
                    updateEmptyVisibility();
                    updateItemCount();
                    Toast.makeText(MainActivity.this, "已清空列表", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateEmptyVisibility() {
        if (fruitList.isEmpty()) {
            fruitCycleView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            fruitCycleView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void updateItemCount() {
        itemCountText.setText("共" + fruitList.size() + "件商品");
    }
}
