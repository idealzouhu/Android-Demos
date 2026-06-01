### 项目概述

本案例演示了 Android 架构组件 ViewModel 和 LiveData 的基础使用方法。通过一个计数器应用，展示了如何：
1. 使用 ViewModel 管理界面相关的数据
2. 使用 LiveData 观察数据变化
3. 处理配置变化（如屏幕旋转）时的数据保持


### 项目结构

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

### 学习目标

通过该项目，你将掌握：

- ViewModel 的基本创建和使用 
- LiveData 的观察和数据更新 
- ViewModel 在配置变化时的存活机制 
- 使用工厂模式创建 ViewModel
