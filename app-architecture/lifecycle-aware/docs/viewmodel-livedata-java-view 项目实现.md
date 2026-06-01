## 一、项目概述

本案例演示了 Android 架构组件 ViewModel 和 LiveData 的基础使用方法。通过一个计数器应用，展示了如何：
1. 使用 ViewModel 管理界面相关的数据
2. 使用 LiveData 观察数据变化
3. 处理配置变化（如屏幕旋转）时的数据保持
4. 实现 MVVM 架构的基本模式



### 1.1 核心实现思路

本案例采用 **MVVM（Model-View-ViewModel）** 架构模式，将界面逻辑与业务逻辑分离：

- **View层**：`MainActivity`负责界面展示和用户交互，通过观察 ViewModel 中的 LiveData 数据来更新 UI
- **ViewModel层**：`CounterViewModel`管理界面相关的数据，提供业务逻辑方法，不持有 View 的引用
- **Model层**：数据模型和业务逻辑，本案例中为简单的计数器数据

ViewModel 在配置变化（如屏幕旋转）时存活，避免数据丢失；LiveData 自动管理观察者的生命周期，防止内存泄漏。**数据流向**：

1. 用户点击按钮触发 View 层事件
2. View 调用 ViewModel 的业务方法
3. ViewModel 更新 LiveData 数据
4. LiveData 通知所有观察者（View 层）
5. View 根据新数据更新 UI



### 1.2 关键组件

| 组件                          | 作用                       | 核心特性                     |
| ----------------------------- | -------------------------- | ---------------------------- |
| **ViewModel**                 | 管理界面数据，处理业务逻辑 | 生命周期感知，配置变化时存活 |
| **LiveData**                  | 可观察的数据持有者         | 生命周期感知，自动更新 UI    |
| **MutableLiveData**           | 可变的 LiveData            | 允许外部修改数据             |
| **ViewModelProvider**         | 创建和管理 ViewModel       | 确保 ViewModel 实例唯一      |
| **ViewModelProvider.Factory** | 自定义 ViewModel 创建      | 支持带参数的 ViewModel       |
| **Observer**                  | 观察 LiveData 变化         | 响应式更新 UI                |



### 1.3 项目结构

```
viewmodel-livedata-java-view/
├── 📱 app/
│   ├── src/main/java/com/example/viewmodel/
│   │   ├── MainActivity.java
│   │   ├── CounterViewModel.java
│   │   └── CounterViewModelFactory.java
│   ├── src/main/res/
│   │   ├── layout/
│   │   │   └── activity_main.xml
│   │   └── values/
│   │       ├── strings.xml
│   │       ├── colors.xml
│   │       └── styles.xml
│   └── build.gradle.kts
├── 📄 README.md
├── 📄 build.gradle.kts
├── 📄 settings.gradle.kts
└── 📁 gradle/
```



## 二、功能模块详解

具体细节查看代码。



## 三、应用效果

1. 屏幕旋转时，计数不会重置
2. 重置按钮状态随计数值动态变化
3. 背景颜色随计数值变化






## 四、问题

暂无。