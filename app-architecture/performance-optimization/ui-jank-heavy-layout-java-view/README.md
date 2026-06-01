### 项目概述

本案例通过多个独立页面**故意制造 UI 卡顿（jank）**：过度绘制、深层布局测量、列表绑定重计算、过重 `onDraw`、滑动触发的布局抖动等。适合配合开发者选项中的 **GPU 过度绘制**、Android Studio **Profiler**、**Perfetto / 系统跟踪** 对照观察帧时间与 `measure`/`layout`/`draw` 开销。

窗口采用 **`WindowCompat.setDecorFitsSystemWindows(getWindow(), true)`**，即**不启用 Edge-to-Edge**：内容由系统在状态栏、导航栏内侧布局，无需再手写 `WindowInsets` padding。

### 场景说明

| 入口 | 复现思路 |
|------|-----------|
| 多层全屏叠加 | `FrameLayout` 内多层 `match_parent` 不透明子 View，同一像素多次绘制 |
| 极深 LinearLayout 嵌套 | `ScrollView` 内数十层纵向 `LinearLayout`，放大 measure/layout 链成本 |
| RecyclerView 重 onBind | `onBindViewHolder` 内大量字符串拼接（展示文本仅为短标签） |
| 过重 onDraw | 自定义 `HeavyOnDrawJankView` 在 `onDraw` 中 `new Paint`/`Path` 并密集绘制；置于 `ScrollView` 内滑动触发重绘 |
| 滑动 requestLayout | `onScrollChange` 中反复修改顶部 `FrameLayout` 高度并 `requestLayout` |

### 项目结构

```
ui-jank-heavy-layout-java-view/
├── app/src/main/java/com/example/ui/jank/
│   ├── MainActivity.java
│   ├── OverdrawJankActivity.java
│   ├── DeepNestedJankActivity.java
│   ├── RecyclerJankActivity.java
│   ├── JankRecyclerAdapter.java
│   ├── HeavyOnDrawJankActivity.java
│   ├── HeavyOnDrawJankView.java
│   ├── LayoutThrashJankActivity.java
│   └── JankToolbarHelper.java
├── app/src/main/res/layout/
│   ├── activity_main.xml
│   ├── activity_overdraw_jank.xml
│   ├── activity_deep_nested_jank.xml
│   ├── activity_recycler_jank.xml
│   ├── activity_heavy_ondraw_jank.xml
│   ├── activity_layout_thrash_jank.xml
│   └── item_jank_row.xml
└── app/build.gradle.kts
```

### 学习目标

- 区分「GPU 过度绘制」「主线程 layout 过重」「列表绑定 CPU」「绘制阶段分配」等常见掉帧原因
- 在真机/模拟器上打开 GPU 渲染分析，建立现象与工具视图之间的对应关系
