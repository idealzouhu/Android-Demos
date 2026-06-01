### 项目概述

本案例演示了 Android 中单 Activity 多 Fragment 的响应式布局用法，重点展示如何根据屏幕宽度（手机、平板、大屏）切换不同导航方式：底部导航栏、侧边抽屉与常驻侧边栏，以及溢出菜单的按需显示。

### 项目结构

```
fragment-responsive-java-view/
├── app/src/main/java/com/example/fragment/responsive/
│   ├── MainActivity.java                    # 主 Activity，统一处理多种布局下的导航与菜单
│   └── ui/
│       ├── transform/                       # Transform 页面
│       │   ├── TransformFragment.java
│       │   └── TransformViewModel.java
│       ├── reflow/                          # Reflow 页面
│       │   ├── ReflowFragment.java
│       │   └── ReflowViewModel.java
│       ├── slideshow/                       # Slideshow 页面
│       │   ├── SlideshowFragment.java
│       │   └── SlideshowViewModel.java
│       └── settings/                        # Settings 页面
│           ├── SettingsFragment.java
│           └── SettingsViewModel.java
├── app/src/main/res/
│   ├── layout/                             # 默认（手机）布局
│   │   ├── activity_main.xml
│   │   ├── app_bar_main.xml
│   │   ├── content_main.xml                # 含底部导航与 NavHost
│   │   ├── fragment_*.xml
│   │   ├── item_transform.xml
│   │   └── nav_header_main.xml
│   ├── layout-w600dp/                      # 中等宽度（如 7 寸平板）：侧边抽屉
│   │   ├── activity_main.xml
│   │   ├── app_bar_main.xml
│   │   └── content_main.xml
│   ├── layout-w1240dp/                     # 大屏：常驻侧边栏
│   │   ├── activity_main.xml
│   │   ├── app_bar_main.xml
│   │   └── content_main.xml
│   ├── menu/
│   │   ├── bottom_navigation.xml           # 底部导航菜单
│   │   ├── navigation_drawer.xml           # 侧边抽屉/侧栏菜单
│   │   └── overflow.xml                    # Toolbar 溢出菜单（如设置）
│   ├── navigation/
│   │   └── mobile_navigation.xml           # 导航图
│   └── values/
│       ├── strings.xml
│       ├── colors.xml
│       ├── dimens.xml
│       ├── themes.xml
│       └── values-w600dp/                  # 中等宽度尺寸
│       └── values-w936dp/                  # 大屏尺寸
├── build.gradle.kts
└── settings.gradle.kts
```

### 学习目标

通过该项目，你将掌握：

- 响应式布局设计：使用 `layout-w600dp`、`layout-w1240dp` 等限定符，为不同屏幕宽度提供不同布局与导航方式
- 单 Activity 多 Fragment 架构：通过 Navigation 组件与 NavHostFragment 统一管理页面切换，同一套代码适配多种布局
- 导航方式切换：在窄屏使用底部导航 + 溢出菜单、在中等宽度使用侧边抽屉、在大屏使用常驻侧边栏，并在 Activity 中通过视图是否存在（如 `nav_view`）决定是否显示溢出菜单
- Fragment 与 ViewModel 基础：各子页面以 Fragment + ViewModel 组织，理解在响应式场景下的生命周期与状态处理
