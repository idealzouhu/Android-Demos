## 一、RecyclerView 简介

### 1.1 什么是 RecyclerView

（简要定义：Android 官方推荐的列表/网格控件，替代 ListView，支持复杂布局与动画。）

### 1.2 RecyclerView 的核心组成

| 组件           | 作用         | 说明                               |
| :------------- | :----------- | :--------------------------------- |
| RecyclerView   | 列表容器     | 负责滚动与复用                     |
| LayoutManager  | 布局管理器   | 决定排列方式（线性/网格/瀑布流等） |
| Adapter        | 数据适配器   | 数据与 ViewHolder 的桥梁           |
| ViewHolder     | 项视图持有者 | 缓存与绑定单条列表项               |
| ItemDecoration | 项装饰       | 分割线、间距等                     |
| ItemAnimator   | 项动画       | 增删改时的动画（可自定义）         |



### 1.3 与 ListView 的对比

| 特性           | **ListView/GridView**                     | **RecyclerView**                                                                 |
| -------------- | ----------------------------------------- | -------------------------------------------------------------------------------- |
| **布局/滚动** | 仅垂直列表或固定网格，无法灵活换向        | 通过更换 LayoutManager 可做垂直/横向列表、网格、瀑布流等，布局与滚动方向可配置   |
| **点击事件**   | 提供 `setOnItemClickListener()` 等，整项点击 | 无内置，需在 `onBindViewHolder` 中为 item（`holder.itemView`）或子 View 注册监听；更灵活，可针对项内某控件单独设点击 |
| **ViewHolder** | 最佳实践（非强制）                        | 强制使用，性能基础有保障                                                         |
| **动画支持**   | 无内置，需手动实现                        | 内置增删改动画支持                                                               |
| **模块化**     | 耦合度高，定制困难                        | 高度解耦，易于扩展定制                                                           |
| **局部刷新**   | 仅支持 `notifyDataSetChanged`             | 支持 `notifyItemInserted` 等精确局部刷新                                         |



## 二、RecyclerView 的工作原理

### 2.1 视图复用（Recycle）机制

（说明：只创建屏幕可见数量 + 缓冲的 ViewHolder，滚动时回收移出屏幕的 View、绑定新数据再显示。）



### 2.2 数据绑定流程

```
数据源 → Adapter（getItemCount / onCreateViewHolder / onBindViewHolder）→ ViewHolder → 显示
```



### 2.3 LayoutManager 的作用

（说明：负责测量、布局、回收与复用策略，不同 LayoutManager 实现不同排列方式。）




## 三、RecyclerView 的使用方法

### 3.1 基本使用步骤

1. 添加依赖 / 引入 RecyclerView
2. 在布局中声明 RecyclerView
3. 定义列表项布局（item layout）
4. 创建 ViewHolder 与 Adapter
5. 设置 LayoutManager、Adapter，可选设置 ItemDecoration、ItemAnimator



### 3.2 Adapter 核心方法

```
1. onCreateViewHolder() - 创建 ViewHolder（对应“创建视图”）
2. onBindViewHolder() - 绑定数据到 ViewHolder
3. getItemCount() - 返回数据项数量
4. getItemViewType() - 多类型列表时返回项类型（可选）
```



### 3.3 常用 LayoutManager

| 类型   | 类名                       | 用途                |
| :----- | :------------------------- | :------------------ |
| 线性   | LinearLayoutManager        | 垂直/水平列表       |
| 网格   | GridLayoutManager          | 网格列表            |
| 瀑布流 | StaggeredGridLayoutManager | 不等高/不等宽瀑布流 |



### 3.4 分割线与间距

（ItemDecoration 用法，或通过 item 布局 margin/padding 实现。）



### 3.5 点击事件

（在 onBindViewHolder 中为 itemView 或子 View 设置 OnClickListener。）



## 四、最佳实践与注意事项

- ViewHolder 继承 RecyclerView.ViewHolder，在 Adapter 内完成创建与绑定
- 列表数据变更用 DiffUtil / 局部刷新，避免整表 notifyDataSetChanged
- 固定高度或 setHasFixedSize(true) 可优化性能
- 多类型列表时正确实现 getItemViewType 与 onCreateViewHolder 分支





## 五、扩展（可选）

- 下拉刷新、上拉加载更多
- 与 DataBinding / ViewBinding 结合
- 列表动画与 ItemAnimator 自定义