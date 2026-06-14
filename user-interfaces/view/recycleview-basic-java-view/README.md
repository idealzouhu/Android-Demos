### 项目概述

本项目为基于 RecyclerView 的水果商城示例应用。运行应用后，你将看到如下功能：
- 使用 RecyclerView 显示美观的水果列表
- 点击列表项查看水果详情
- 长按列表项删除单个水果
- 使用按钮添加随机水果
- 清空整个列表的功能

### 项目结构

```
app/
├── src/main/java/com/example/recycleview/basic/
│   ├── MainActivity.java          # 主界面，设置 LayoutManager、Adapter、ItemDecoration、ItemAnimator
│   ├── Fruit.java                 # 数据模型
│   ├── FruitAdapter.java          # 列表适配器（ViewHolder + 数据绑定）
│   └── ListItemDecoration.java    # 自定义 ItemDecoration（列表项间分割线）
├── res/layout/
│   ├── activity_main.xml          # 主布局（含 RecyclerView、按钮、空视图）
│   └── item_fruit.xml             # 列表项布局
└── res/drawable/
    ├── ic_fruit_default.xml       # 水果占位图标
    └── divider_list_item.xml      # 分割线形状（供 ItemDecoration 使用）
```

### 学习目标

通过该项目，你将掌握：
- RecyclerView 与 Adapter、ViewHolder 的配合使用
- 设置 LayoutManager（如 LinearLayoutManager）控制列表排列方式
- **ItemDecoration**：自定义分割线（继承 `RecyclerView.ItemDecoration`，实现 `onDraw` 与 `getItemOffsets`）
- **ItemAnimator**：使用 `DefaultItemAnimator` 并设置增删动画时长，观察添加/删除项时的过渡效果
- 在 `onBindViewHolder` 中为 item 或子 View 注册点击、长按事件
- 使用 `notifyItemInserted`、`notifyItemRemoved` 等局部刷新替代 `notifyDataSetChanged`，提升性能与动画效果
