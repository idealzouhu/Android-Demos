## 一、标题栏定制

标题栏实现点有：

- **自定义标题栏布局**：使用 RelativeLayout 实现类似 ActionBar 的效果
- **动态标题修改**：支持运行时修改标题文字
- **菜单按钮集成**：右侧集成菜单触发按钮
- **Material Design 风格**：采用现代设计语言和色彩方案

### 1.1 自定义标题栏布局设计

使用 RelativeLayout 实现类似 ActionBar 的效果。`activity_main.xml` 中的标题栏部分：

```xml
<!-- 自定义标题栏 -->
<RelativeLayout
    android:id="@+id/toolbar"
    android:layout_width="0dp"
    android:layout_height="56dp"
    android:background="#2196F3"
    android:paddingStart="16dp"
    android:paddingEnd="16dp"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent">

    <!-- 标题 -->
    <TextView
        android:id="@+id/toolbar_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:text="主页面"
        android:textColor="@android:color/white"
        android:textSize="20sp"
        android:textStyle="bold" />

    <!-- 右侧菜单按钮 -->
    <ImageButton
        android:id="@+id/btn_menu"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_alignParentEnd="true"
        android:layout_centerVertical="true"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:src="@android:drawable/ic_menu_more"
        android:contentDescription="菜单" />

</RelativeLayout>
```



### 1.2 标题动态更新机制

`MainActivity.java` 中的实现：

```java
public class MainActivity extends AppCompatActivity {
    private TextView toolbarTitle;
    private int clickCount = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        // 初始化标题视图
        toolbarTitle = findViewById(R.id.toolbar_title);
    }
    
    private void setupClickListeners() {
        // 修改标题按钮点击事件
        findViewById<Button>(R.id.btn_change_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTitle();
            }
        });
    }
    
    /**
     * 动态修改标题文本
     */
    private void changeTitle() {
        // 标题数组，循环切换
        String[] titles = {
            "主页面", 
            "Activity 演示", 
            "Java + View", 
            "Android 开发"
        };
        
        // 计算新标题索引
        String newTitle = titles[clickCount % titles.length];
        
        // 更新标题文本
        toolbarTitle.setText(newTitle);
        
        // 更新状态显示
        updateStatus("标题已修改为：" + newTitle);
        
        clickCount++;
    }
}
```



### 1.3 菜单按钮集成与事件处理

```java
private void setupClickListeners() {
    // 菜单按钮点击事件
    btnMenu.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    });
}

/**
 * 显示弹出菜单
 */
private void showPopupMenu(View view) {
    // 创建 PopupMenu 实例
    PopupMenu popupMenu = new PopupMenu(this, view);
    
    // 加载菜单资源
    popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());
    
    // 设置菜单项点击监听
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return handleMenuItemClick(item);
        }
    });
    
    // 显示菜单
    popupMenu.show();
}
```



## 二、菜单系统实现

本案例使用自定义的 PopMenu, 而不使用默认菜单系统 OptionMenu。



### 2.1 菜单资源文件创建与配置

创建 **res/menu/main_menu.xml** 文件。

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <!-- 搜索菜单项（显示在ActionBar） -->
    <item
        android:id="@+id/action_search"
        android:icon="@android:drawable/ic_menu_search"
        android:title="@string/action_search"
        app:showAsAction="ifRoom" />

    <!-- 分享菜单项（显示在ActionBar） -->
    <item
        android:id="@+id/action_share"
        android:icon="@android:drawable/ic_menu_share"
        android:title="@string/action_share"
        app:showAsAction="ifRoom" />

    <!-- 设置菜单项（显示在溢出菜单） -->
    <item
        android:id="@+id/action_settings"
        android:title="@string/action_settings" />

    <!-- 关于菜单项（显示在溢出菜单） -->
    <item
        android:id="@+id/action_about"
        android:title="@string/action_about" />

</menu>
```

**菜单配置说明：**

- `showAsAction="ifRoom"`：如果有空间就显示在ActionBar。相应地，如果没有足够的空间，则会显示在溢出菜单里面。
- `android:icon`：设置菜单项图标
- `android:title`：设置菜单项文字



### 2.2 主题设置为 NoActionBar

主题设置为 

```xml
<style name="Base.Theme.Activitybasicjavaview" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your dark theme here. -->
        <!-- <item name="colorPrimary">@color/my_dark_primary</item> -->
    </style>
```



### 2.3 PopupMenu 的创建与显示

```java
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
```



### 2.4 菜单项点击事件处理

```java
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
```





## 三、基础交互功能

### 3.1 Button 点击事件监听

```java
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
```



### 3.2 Toast 消息提示使用

```java
	/**
     * 显示Toast消息
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
```



### 3.3 界面状态实时更新

```java
    private void updateStatus(String status) {
        tvStatus.setText("当前状态：" + status);
    }
```

