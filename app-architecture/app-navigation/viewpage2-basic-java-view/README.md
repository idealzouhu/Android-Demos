### 项目概述

本案例演示了 Android 中 ViewPager2 的基础使用方法，在单个 Activity + Navigation 架构下，于 Reflow 页面内实现带标签的滑动视图（水平分页），配合 TabLayout 与 FragmentStateAdapter 完成多页切换与联动。


### 项目结构

```
viewpage2-basic-java-view/
├── app/src/main/java/com/viewpage2/basic/
│   ├── MainActivity.java                           # 主 Activity，含 Navigation 等
│   └── ui/reflow/
│       ├── ReflowFragment.java                     # Reflow 容器，挂接 TabLayout + ViewPager2
│       ├── ReflowCollectionAdapter.java             # FragmentStateAdapter，为 ViewPager2 提供页面
│       └── ReflowObjectFragment.java                # 单页 Fragment
├── app/src/main/res/
│   ├── layout/
│   │   ├── fragment_reflow.xml                     # Reflow 布局（TabLayout + ViewPager2）
│   │   └── fragment_reflow_object.xml              # ViewPager2 单页布局
│   ├── menu/
│   │   └── ...                                     # 菜单资源
│   └── values/
│       └── strings.xml                             # 字符串资源（含 reflow_page_label）
├── app/build.gradle.kts                             # 应用模块依赖（含 viewpager2）
└── gradle/libs.versions.toml                       # 版本与库声明（含 viewpager2）
```


### 学习目标

通过该项目，你将掌握：

- ViewPager2 与 FragmentStateAdapter 的使用，如为 ViewPager2 设置 Adapter、按 position 创建 Fragment 并传参
- TabLayout 与 ViewPager2 的联动，如使用 TabLayoutMediator 绑定标签与页面、同步选中与点击切换
- Reflow 内滑动视图的完整实现，如布局设计（上 TabLayout 下 ViewPager2）、单页 Fragment 与 Adapter 的职责划分、在 onDestroyView 中 detach Mediator 避免泄漏
