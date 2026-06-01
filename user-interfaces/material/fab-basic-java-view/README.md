### 项目概述

本案例演示如何通过 `CoordinatorLayout`实现悬浮动作按钮（FAB）与 `Snackbar` 的联动效果。当 `Snackbar` 弹出时，FAB 会自动上移避免被遮挡；Snackbar 消失时，FAB 会自动下移回原位。这是 `CoordinatorLayout`作为特殊规则制定者，协调其子视图行为的经典应用。




### 项目结构

```
fab-basic-java-view/
├── app/src/main/java/com/example/fabcoordinator/
│   └── MainActivity.java                    # 主Activity，包含业务逻辑
├── app/src/main/res/layout/
│   └── activity_main.xml                    # 主界面布局文件，使用 CoordinatorLayout
├── app/src/main/res/values/
│   ├── strings.xml                          # 字符串资源
│   └── colors.xml                           # 颜色资源
└── app/build.gradle.kts                     # 项目依赖配置
```



### 学习目标

通过该项目，你将掌握：

- **悬浮按钮 (FAB) 的实现方式**：掌握如何使用 Material Design 组件库中的 `FloatingActionButton`，理解其作为促进主要操作的高强调度组件的意义。
- **Snackbar 的交互能力**：理解 `Snackbar`作为 `Toast`增强替代品的角色，它不仅提供短暂的消息提示，更关键的是允许嵌入一个可交互的 `Action`按钮，使用户能够在不离开当前上下文的情况下执行撤销或其他辅助操作。
- **CoordinatorLayout的核心作用**：理解 CoordinatorLayout并非普通布局容器，而是作为协调者，通过内置的 Behavior机制管理子视图之间的交互菜单系统实现，

