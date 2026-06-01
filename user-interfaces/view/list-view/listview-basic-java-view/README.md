### 项目概述

本项目为水果商城应用。运行应用后，你将看到如下功能：
- 显示美观的水果列表
- 点击列表项查看水果详情
- 长按列表项删除单个水果
- 使用按钮添加随机水果
- 清空整个列表的功能





### 项目结构

```
app/
├── java/com/example/fruitmarket/
│   ├── MainActivity.java          # 主界面
│   ├── Fruit.java                 # 数据模型
│   └── FruitAdapter.java    	   # 详情页面
├── res/layout/
│   ├── activity_main.xml          # 主布局
│   └── item_fruit.xml        	   # 详情布局
```



### 学习目标
通过该项目，你将掌握：
- 自定义列表项布局
- ViewHolder 模式优化性能，替代使用 findViewById