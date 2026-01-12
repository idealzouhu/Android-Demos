## 一、ListView 基础知识

### 1.1 什么是 ListView

ListView 是 Android 中用于显示垂直滚动列表的视图组件，是 Android 早期最重要的列表展示控件。



### 1.2 ListView 的核心组成

| 组件        | 作用       | 说明                     |
| :---------- | :--------- | :----------------------- |
| Adapter     | 数据适配器 | 连接数据和列表视图的桥梁 |
| ListView    | 列表容器   | 显示可滚动的列表项       |
| Item Layout | 列表项布局 | 定义每个列表项的显示样式 |



#### 1.2.1 Adapter  的核心方法

```
// Adapter 的核心方法：
1. getCount() - 返回数据项数量
2. getItem() - 返回指定位置的数据
3. getItemId() - 返回项目ID
4. getView() - 创建和绑定列表项视图
```





### 1.3 ListView 的工作原理

```
数据源 → Adapter → ListView → 显示
```



## 1.4 优缺点









## 二、最佳实践

使用 ViewHolder 模式





