## 一、项目概述

本案例演示了如何使用 `CollapsingToolbarLayout` 实现可折叠式标题栏，并实现标题栏背景图与系统状态栏的融合效果。



### 1.1 核心实现思路

可折叠标题栏的核心实现基于 `CoordinatorLayout` 协调布局机制，通过 `AppBarLayout` 与 `CollapsingToolbarLayout` 的协同工作，实现视图滚动时的动态折叠效果。当用户滚动内容时，系统通过预设的行为模式自动处理标题栏的折叠动画，无需手动计算位置和偏移量。





### 1.2 关键组件

实现可折叠标题栏需要以下关键Material Design组件：

- **CoordinatorLayout**：作为根布局，协调子视图之间的交互行为
- **AppBarLayout**：专门用于包裹可折叠标题栏的垂直线性布局
- **CollapsingToolbarLayout**：实现具体折叠效果的核心容器
- **NestedScrollView**：支持嵌套滚动的可滚动内容区域





### 1.3 项目结构

```
collapsing-toolbar-basic-java-view/
├── app/src/main/java/com/example/collapsingtoolbar/
│   └── basic/
│       ├── MainActivity.java                 # 主 Activity，包含业务逻辑
│       └── DetailActivity.java               # 详情页面，展示折叠效果
├── app/src/main/res/
│   ├── layout/
│   │   ├── activity_main.xml                # 主界面布局文件
│   │   └── activity_detail.xml              # 详情页布局文件
│   ├── values/
│   │   ├── strings.xml                      # 字符串资源
│   │   ├── colors.xml                       # 颜色资源
│   │   └── styles.xml                       # 样式主题配置（实现状态栏透明）
│   └── drawable/
│       ├── header_bg.jpg                    # 标题栏背景图片
│       └── placeholder.xml                  # 内容占位符
└── app/build.gradle                         # 项目依赖配置
```



## 二、功能模块详解

### 2.1 可折叠式标题栏实现

#### 2.1.1 布局结构设计

可折叠标题栏的布局采用层次化结构设计，从外到内依次为：

1. **CoordinatorLayout**：根容器，负责协调所有子组件的滚动行为
2. **AppBarLayout**：定义标题栏的滚动行为模式
3. **CollapsingToolbarLayout**：实现折叠动画效果的核心容器
4. **ImageView和Toolbar**：具体的视觉元素



#### 2.1.2 关键属性配置

可折叠标题栏的属性配置：

- `app:contentScrim` 属性:  指定`CollapsingToolbarLayout` 在折叠状态时的背景色。当折叠完成后，`CollapsingToolbarLayout` 实质上变成一个标准的Toolbar，因此背景色应设置为主题的主色  `?attr/colorPrimary`。   

- `app:layout_scrollFlags` 属性：
  - `scroll` 表示 CollapsingToolbarLayout 会随着内容区域的滚动一起滚动。
  - `exitUntilCollapsed` 表示折叠完成后保留在界面上，不再移出屏幕。
  - `snap`：提供平滑的折叠动画效果，确保折叠状态要么完全展开要么完全折叠。

- `app:layout_collapseMode`属性:   指定控件在 CollapsingToolbarLayout  折叠过程中的折叠模式。
  - `pin`：在折叠过程中位置始终保持不变，适用于Toolbar等需要持续可见的元素。
  - `parallax` ： 在折叠过程中产生错位偏移，创造视觉深度效果，适用于背景图片。

内容区域，由于CoordinatorLayout本身已经可以响应滚动事件了，因此我们在它的内部就需要使用NestedScrollView或RecyclerView这样的布局。并指定布局行为 `app:layout_behavior`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"> <!-- 关键属性，用于系统栏适配 -->

    <!-- 可折叠标题栏 -->
    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/app_bar"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        android:fitsSystemWindows="true"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:id="@+id/collapsing_toolbar"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:contentScrim="?attr/colorPrimary"
            app:expandedTitleMarginStart="48dp"
            app:expandedTitleMarginBottom="48dp"
            app:layout_scrollFlags="scroll|exitUntilCollapsed|snap"
            android:fitsSystemWindows="true">

            <!-- 背景图片，设置视差折叠效果 -->
            <ImageView
                android:id="@+id/header_image"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop"
                android:src="@drawable/header_bg"
                app:layout_collapseMode="parallax"
                android:fitsSystemWindows="true"/>

            <!-- 工具栏，折叠时固定在顶部 -->
            <androidx.appcompat.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:layout_collapseMode="pin"/>

        </com.google.android.material.appbar.CollapsingToolbarLayout>
    </com.google.android.material.appbar.AppBarLayout>

    <!-- 可滚动的内容区域，其滚动会驱动标题栏折叠 -->
    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">
		
		...
		
    </androidx.core.widget.NestedScrollView>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```







### 2.2 充分利用系统状态栏空间

在 Android 5.0 后，系统支持对系统状态栏的背景和颜色进行操作。通过设置透明状态栏实现标题栏背景图延伸到状态栏后面的效果，这种技术利用系统窗口的布局机制，将内容绘制到系统状态栏区域。

具体操作如下:

1. 设置系统状态栏颜色为透明色：在主题中添加 `android:statusBarColor=“@android:color/transparent”`  这一属性

   ```xml
   <style name="DetailActivityTheme" parent="Theme.AppCompat.Light.NoActionBar">
       <item name="android:windowDrawsSystemBarBackgrounds">true</item>
       <item name="android:statusBarColor">@android:color/transparent</item>
   </style>
   
   
    <activity
               android:name=".DetailActivity"
               android:theme="@style/DetailActivityTheme"
               android:exported="false" />
   ```

2. 为了让可折叠式标题栏中的背景图和系统状态栏融合，可以使用 `android:fitsSystemWindows` 属性来实现。 将 ImageView及其所有父布局都设置成 `android:fitsSystemWindows=“true”`，就表示该控件会出现在系统状态栏里。

   







## 三、实现效果

完成以上步骤后，您的应用将具备以下特性：

1. **主页**：显示一张精美的 Material Design 卡片，点击卡片或按钮即可跳转。
2. **详情页**：
   - 打开时显示完整的背景图和标题。
   - **向上滚动**内容时，背景图会以视差效果逐渐折叠，最终变成一个标准的工具栏。
   - 标题文字会从大标题平滑过渡到工具栏上的小标题。
   - 背景图片会延伸到状态栏下方，实现沉浸式效果。
3. **导航**：点击详情页工具栏上的返回箭头，可以回到主页面









## 四、问题

### 可折叠标题栏效果没有实现好

#### 问题描述

- 在一开始打开这个页面的时候，标题栏里面只看得到图片，标题只显示了文字最上面的一点内容，没有看到全部标题。

- 向上滚动后，标题栏理论上最终会变成完全折叠状态，背景图片消失，只显示一个普通的 Toolbar。但是，标题栏没有变成完全折叠状态，背景图片消失，Toolbar 只显示了一半左右。



#### 原因分析

通常是由于 **可折叠标题栏的滚动行为配置** 与 **内容区域的尺寸** 不匹配导致的。

第一个问题

- `CollapsingToolbarLayout` 设置属性 `app:expandedTitleGravity="bottom|start"` 即可。



第二个问题：

- 把 `AppBarLayout` 的 `android:fitsSystemWindows="true"` 属性删除就好了。但是，这样的话，标题栏的背景图片就没有和系统栏融合了。

核心在于 `CoordinatorLayout`嵌套结构中 `fitsSystemWindows`属性的处理逻辑。同版本的 Android 系统对它们的处理顺序和方式可能产生冲突，干扰了 `CollapsingToolbarLayout`折叠高度的正确计算。



#### 解决方案

暂时解决不了

