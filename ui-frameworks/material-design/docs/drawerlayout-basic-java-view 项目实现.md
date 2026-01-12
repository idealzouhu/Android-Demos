## 一、项目概述

本案例演示了在 Android 应用中如何使用 `DrawerLayout` 配合 `NavigationView` 实现一个标准的 Material Design 风格侧滑菜单。侧滑菜单是一种常见的导航模式，用户可以通过从屏幕边缘滑出或点击工具栏图标来访问主导航选项。

### 1.1 核心实现思路

核心思路是利用 `DrawerLayout`作为根布局，管理主内容区和侧滑菜单的协调工作。`DrawerLayout`自身特性是侧边菜单可以根据手势展开与隐藏。主内容区的内容可以随着菜单的点击而变化，这需要开发者自己去实现具体的逻辑。



### 1.2 关键组件

| 组件名称                    | 角色定位           | 主要功能                               |
| --------------------------- | ------------------ | -------------------------------------- |
| **DrawerLayout**            | 根布局容器         | 管理主内容视图和抽屉菜单视图           |
| **NavigationView**          | 侧滑菜单界面构建器 | 快速构建标准化的侧滑菜单界面           |
| **Toolbar**                 | 应用顶栏           | 集成导航按钮控制菜单开关               |
| **ActionBarDrawerToggle**   | 可选但经典的辅助类 | 集成菜单状态与 ActionBar，提供视觉反馈 |
| **OnBackPressedDispatcher** | 返回导航处理器     | 处理 Android 13+ 的预测性返回手势      |





### 1.3 项目结构

```
drawerlayout-basic-java-view/
├── app/src/main/java/com/example/drawerlayout/basic/
│   └── MainActivity.java                    # 主 Activity，处理菜单逻辑与交互
├── app/src/main/res/
│   ├── layout/
│   │   ├── activity_main.xml                # 主界面布局，包含 DrawerLayout 结构
│   │   └── navigation_header.xml            # 侧滑菜单的头部布局
│   ├── menu/
│   │   └── drawer_menu.xml                  # 侧滑菜单的菜单项资源
│   ├── drawable/
│   │   └── ic_menu.xml                      # 工具栏导航图标
│   └── values/
│       ├── strings.xml                      # 字符串资源
│       └── colors.xml                       # 颜色资源
└── app/build.gradle                         # 项目依赖配置
```



## 二、功能模块详解

### 2.1 DrawerLayout 布局实现

`DrawerLayout` 中一般会放置两个直接子控件。但对这两个子控件的类型没有什么要求。但是，需要遵循以下规范：

- **主内容优先**：显示界面主要内容的 `View`必须为 `DrawerLayout`的第一个子 `View`。这是因为 XML 布局文件中的  `View `顺序为 Android 系统中的 z-ordering 顺序，抽屉必须出现在内容之上。该视图的宽度和高度通常设置为 `match_parent`。
- **明确侧滑视图 `layout_gravity` 属性**： 抽屉菜单必须使用 `android:layout_gravity` 属性
- **菜单尺寸建议**：抽屉菜单的宽度建议不超过 `320dp`，这样用户可以在菜单打开的时候看到部分内容界面

基本结构如下所示：

```xml
<androidx.drawerlayout.widget.DrawerLayout>
    
    <!-- 主内容区域：必须是第一个子视图 -->
    <ConstraintLayout>
        <Toolbar/>
        ... 其他主要内容 ...
    </ConstraintLayout>

    
    <!-- 侧滑菜单区域：必须设置layout_gravity -->
    <com.google.android.material.navigation.NavigationView
        android:layout_gravity="start"
        ... />
    
</androidx.drawerlayout.widget.DrawerLayout>
```

另外，通过 `openDrawer()`和 `closeDrawer()`方法，并传入 `GravityCompat.START`等参数，可以控制侧滑菜单的打开与关闭。







### 2.2 导航按钮实现

为了提升用户体验，防止用户不知道可以从屏幕左侧边缘拖动打开菜单，Material Design 建议在 `Toolbar`的最左边加入一个导航按钮。点击此按钮也可以展开滑动菜单，这相当于提供了两种打开方式。

#### 2.2.1 Toolbar 集成配置

设置导航按钮以及按钮对应图标。

```java
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
private void setupToolbar() {
    // 将布局中定义的 Toolbar 实例设置为当前 Activity 的 ActionBar
    setSupportActionBar(toolbar);
    
    if (getSupportActionBar() != null) {
        // 启用首页按钮显示
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 设置导航图标（汉堡菜单）
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }
}
```



#### 2.2.2 导航按钮点击处理

在 Activity 的  `onOptionsItemSelected ` 方法中，监听对该按钮的点击事件。在事件处理中，判断抽屉当前状态，并调用 `openDrawer`或 `closeDrawer`方法进行切换。

> 注意，**导航按钮在 `Toolbar`上的 ID 固定为 `android.R.id.home`**。

```java
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    // 处理工具栏首页按钮点击（android.R.id.home）
    if (item.getItemId() == android.R.id.home) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true; // 事件已消费
    }
    
    return super.onOptionsItemSelected(item);
}
```





### 2.3 NavigationView 实现

NavigationView是一个封装好的视图，用于快速创建符合 Material Design 规范的导航抽屉内容。NavigationView 配置要点主要有：

- **菜单资源**：使用 `app:menu` 属性绑定菜单资源

- **头部布局**：通过`app:headerLayout`可添加自定义头部布局，（如 `navigation_header.xml`），通常用于展示用户头像、名称和邮箱等信息，提升个性化体验。
- **事件处理**：通过实现 `NavigationView.OnNavigationItemSelectedListener`接口，并在 `onNavigationItemSelected`回调方法中处理菜单项的点击事件。在该方法中，通常需要执行如更新主界面内容、记录选中状态、关闭抽屉等操作。

侧滑菜单中的菜单项事件处理：

```java
// 实现 NavigationView.OnNavigationItemSelectedListener 接口
@Override
public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();
    
    // 根据选中的菜单项执行不同操作
    if (itemId == R.id.nav_home) {
        showHomeFragment();
    } else if (itemId == R.id.nav_profile) {
        showProfileFragment();
    }
    
    // 更新工具栏标题
    if (getSupportActionBar() != null) {
        getSupportActionBar().setTitle(item.getTitle());
    }
    
    // 关闭侧滑菜单
    drawerLayout.closeDrawer(GravityCompat.START);
    return true; // 标记事件已处理
}
```





#### 2.4 现代化返回手势处理（Android 13+）

在 Android 13 及以上版本，推荐使用 `OnBackPressedDispatcher`替代已弃用的 `onBackPressed()`方法

```java
private void setupBackPressedHandler() {
    // 创建返回按键回调
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            // 如果菜单打开，先关闭菜单
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                // 只有回调启用时才执行默认返回操作
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
```

同时，在 `AndroidManifest.xml`中启用预测性返回手势支持：

```xml
<application
    android:enableOnBackInvokedCallback="true"
    ... >
</application>
```






## 三、问题

在 Android 13 (API 33) 及以上版本，应使用 `OnBackPressedDispatcher`和 `OnBackPressedCallback`来替代已弃用的 `onBackPressed()`方法，以更好地处理预测性返回手势。逻辑是当菜单打开时，按返回键先关闭菜单，而不是直接退出 Activity。



