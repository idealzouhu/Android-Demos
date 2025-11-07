## 一、代码实现

项目具体结构为：

```
app/
├── java/com/example/fruitmarket/
│   ├── MainActivity.java          # 主界面
│   ├── Fruit.java                 # 数据模型
│   └── FruitAdapter.java    	   # 详情页面
├── res/layout/
│   ├── activity_main.xml          # 主布局
│   └── item_fruit.xml        	   # 列表项布局
```



### 1.1 创建自定义适配器

```java
public class FruitAdapter extends BaseAdapter {
    private Context context;
    private List<Fruit> fruitList;
    private LayoutInflater inflater;

    public FruitAdapter(Context context, List<Fruit> fruitList) {
        this.context = context;
        this.fruitList = fruitList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fruitList.size();
    }

    @Override
    public Object getItem(int position) {
        return fruitList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取指定位置的列表项视图
     *
     * @param position 列表项在数据源中的位置索引
     * @param convertView 之前创建的视图实例，可用于视图复用
     * @param parent 父级ViewGroup容器
     * @return 配置好数据的View对象，用于显示在列表中
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // 视图复用逻辑：如果convertView为空，则创建新视图；否则复用已有视图
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_fruit, parent, false);
            holder = new ViewHolder();
            holder.fruitImage = convertView.findViewById(R.id.fruit_image);
            holder.fruitName = convertView.findViewById(R.id.fruit_name);
            holder.fruitDescription = convertView.findViewById(R.id.fruit_description);
            holder.fruitPrice = convertView.findViewById(R.id.fruit_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Fruit fruit = fruitList.get(position);

        // 设置数据
        holder.fruitImage.setImageResource(fruit.getImageId());
        holder.fruitName.setText(fruit.getName());
        holder.fruitDescription.setText(fruit.getDescription());
        holder.fruitPrice.setText(String.format(Locale.getDefault(), "¥%.2f/斤", fruit.getPrice()));

        return convertView;
    }

    // 更新数据
    public void updateData(List<Fruit> newFruitList) {
        this.fruitList.clear();
        this.fruitList.addAll(newFruitList);
        notifyDataSetChanged();
    }

    // 添加单个水果
    public void addFruit(Fruit fruit) {
        this.fruitList.add(fruit);
        notifyDataSetChanged();
    }

    // 删除水果
    public void removeFruit(int position) {
        if (position >= 0 && position < fruitList.size()) {
            this.fruitList.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * ViewHolder模式的实现类，用于存储列表项中的视图组件引用
     * 通过缓存findViewById的结果来提高列表滚动性能
     */
    static class ViewHolder {
        ImageView fruitImage;
        TextView fruitName;
        TextView fruitDescription;
        TextView fruitPrice;
    }

}
```



### 1.2 列表视图

关键点在于理解 `ListView` 渲染过程。

```java
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
```





## 二、问题

### 2.1 点击和长按列表项没有反应

#### 问题描述

在 APP 中，点击列表项和长按列表项均没有反应 。



#### 原因分析

`res/layout/item_fruit.xml` 列表项的代码为：

```
<LinearLayout
    android:clickable="true"
    android:focusable="true">
    <!-- 子View -->
</LinearLayout>
```

在 Android 事件传递机制中，`clickable="true"` 的影响：

- 告诉系统："这个 View 自己要处理点击事件"
- 当用户点击时，事件会被这个 LinearLayout 消费掉
- 事件不会继续传递给父容器（ListView）
- ListView 的 `OnItemClickListener`就收不到事件了

`focusable="true"` 的影响：

- 这个 View 可以获取焦点
- 在事件传递中，可聚焦的 View 会优先处理事件
- 可能干扰 ListView 的正常事件处理流程

事件流程为：

```
用户点击列表项
    ↓
触摸事件到达 ListView
    ↓
ListView 准备将事件传递给对应的 Item
    ↓
Item 的根布局（clickable="true"）拦截事件
    ↓
❌ 事件被消费，不再向上传递
    ↓
ListView 的 OnItemClickListener 永远不会被调用
```





#### 解决方案

删除 `clickable="true"` 和 `focusable="true"` 即可。修复之后，事件流程为：

```
用户点击列表项
    ↓
触摸事件到达 ListView
    ↓
ListView 将事件传递给对应的 Item
    ↓
Item 的根布局（无 clickable 属性）不处理事件
    ↓
✅ 事件继续向上传递给 ListView
    ↓
ListView 的 OnItemClickListener 被正常调用
```

