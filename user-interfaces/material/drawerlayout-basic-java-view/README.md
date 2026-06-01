### 项目概述

本案例演示了在 Android 应用中如何使用 DrawerLayout配合 NavigationView实现一个标准的 Material Design 风格侧滑菜单。侧滑菜单是一种常见的导航模式，用户可以通过从屏幕边缘滑出或点击工具栏图标来访问主导航选项。


### 项目结构

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

### 学习目标

通过该项目，你将掌握：

- 侧滑菜单布局架构：理解 DrawerLayout作为根布局，如何协调主内容区与侧滑菜单视图。
- NavigationView 使用：掌握如何使用 NavigationView快速构建菜单界面，包括设置头部布局 (headerLayout) 和菜单资源 (menu)。
- 菜单交互处理：实现菜单项的点击事件监听 (OnNavigationItemSelectedListener)，并完成相应的界面更新或操作。
- 手势与图标联动：实现通过手势从边缘滑动打开菜单，以及通过点击工具栏上的导航图标 (HomeAsUp) 来控制菜单的展开与收起。
- 返回键智能处理：掌握如何使用 OnBackPressedCallback现代方式，在菜单打开时优先关闭菜单而非直接退出 Activity。
