

## 一、项目概述

### 1.1 核心实现思路

在 **Reflow** 页面中，通过 **ViewPager2** 实现水平滑动切换多页内容（水平分页），并用 **TabLayout** 显示标签，便于用户识别当前页并快速切换。实现要点如下：

- 使用 **FragmentStateAdapter** 为 ViewPager2 提供基于 Fragment 的页面，按需创建/回收，适合页数较多或内容较重的场景。
- 每一页由独立的 **ReflowObjectFragment** 表示，通过 `arguments` 传入页码等数据。
- 使用 **TabLayoutMediator** 将 TabLayout 与 ViewPager2 双向联动：滑动页面时标签选中状态同步，点击标签时页面切换。

### 1.2 关键组件

| 组件 | 说明 |
|------|------|
| **ViewPager2** | 提供水平滑动与分页的容器，来自 `androidx.viewpager2`。 |
| **FragmentStateAdapter** | 为 ViewPager2 提供 Fragment 页面，本项目中实现为 `ReflowCollectionAdapter`。 |
| **TabLayout** | Material 标签栏，展示各页标题并与 ViewPager2 联动。 |
| **TabLayoutMediator** | 将 TabLayout 与 ViewPager2 绑定，同步选中状态与点击切换。 |
| **ReflowFragment** | Reflow 入口 Fragment，负责组装 TabLayout、ViewPager2 与 Adapter。 |
| **ReflowObjectFragment** | 单页内容 Fragment，展示「页面 N」文案。 |

### 1.3 项目结构（与 ViewPager2 相关部分）

```
app/
├── build.gradle.kts                    # 依赖：implementation(libs.viewpager2)
├── src/main/
│   ├── java/.../ui/reflow/
│   │   ├── ReflowFragment.java         # Reflow 容器，挂接 TabLayout + ViewPager2
│   │   ├── ReflowCollectionAdapter.java # FragmentStateAdapter 实现
│   │   └── ReflowObjectFragment.java   # 单页 Fragment
│   └── res/
│       ├── layout/
│       │   ├── fragment_reflow.xml      # TabLayout + ViewPager2 布局
│       │   └── fragment_reflow_object.xml  # 单页布局
│       └── values/
│           └── strings.xml             # reflow_page_label："页面 %d"
gradle/
└── libs.versions.toml                  # viewpager2 版本与库声明
```

---

## 二、功能模块详解

### 2.1 依赖配置

- **版本目录**（`gradle/libs.versions.toml`）：声明 `viewpager2 = "1.1.0"` 及 `viewpager2 = { group = "androidx.viewpager2", name = "viewpager2", version.ref = "viewpager2" }`。
- **应用模块**（`app/build.gradle.kts`）：`implementation(libs.viewpager2)`。TabLayout 与 TabLayoutMediator 使用已有 Material 依赖，无需额外添加。

### 2.2 布局结构

**fragment_reflow.xml**（Reflow 主布局）：

- 根节点为垂直 `LinearLayout`。
- 上方：`TabLayout`（`id/tab_layout`），`tabMode="fixed"`，固定平分标签宽度。
- 下方：`ViewPager2`（`id/pager`），`layout_height="0dp"` + `layout_weight="1"` 占满剩余高度。

**fragment_reflow_object.xml**（单页布局）：

- 根节点为 `FrameLayout`，内嵌一个居中 `TextView`（`android:id="@android:id/text1"`），用于显示「页面 N」。

### 2.3 ReflowFragment（容器逻辑）

- **onCreateView**：使用 ViewBinding 加载 `fragment_reflow.xml`。
- **onViewCreated**：
  - 创建 `ReflowCollectionAdapter(this)` 并设置给 `binding.pager`。
  - 使用 `TabLayoutMediator(tabLayout, viewPager, ...)` 绑定 Tab 与 ViewPager2；回调中为每个 `tab` 设置文案：`getString(R.string.reflow_page_label, position + 1)`（即「页面 1」「页面 2」…）。
  - 调用 `tabLayoutMediator.attach()` 完成联动。
- **onDestroyView**：调用 `tabLayoutMediator.detach()` 并置空 `binding`，避免泄漏。

### 2.4 ReflowCollectionAdapter（FragmentStateAdapter）

- 继承 `FragmentStateAdapter`，构造传入宿主 `Fragment`（本项目中为 `ReflowFragment`）。
- **getItemCount()**：返回固定页数（当前为 5）。
- **createFragment(int position)**：每次返回新的 `ReflowObjectFragment` 实例，并通过 `Bundle` 传入 `ARG_OBJECT` = `position + 1`（页码从 1 开始）。

### 2.5 ReflowObjectFragment（单页内容）

- 定义常量 `ARG_OBJECT`，用于从 `arguments` 中读取页码。
- **onCreateView**：加载 `fragment_reflow_object.xml`。
- **onViewCreated**：从 `getArguments()` 读取 `ARG_OBJECT`，用 `getString(R.string.reflow_page_label, pageIndex)` 设置 `text1` 的文案。

---

## 三、实现效果

- 进入 **Reflow** 后，顶部为 5 个标签：「页面 1」～「页面 5」。
- 左右滑动 ViewPager2 可切换页面，当前页对应标签高亮。
- 点击某一标签可切换到对应页。
- 仅 Reflow 使用 ViewPager2；应用内其他导航项（如 Transform、Slideshow、Settings）保持原有实现，无滑动视图。

---

## 四、问题

- **页数修改**：若需改变页数，只需修改 `ReflowCollectionAdapter` 中的 `PAGE_COUNT` 常量；若页数很多，可将 `fragment_reflow.xml` 中 TabLayout 的 `android:tabMode` 改为 `scrollable`，避免标签挤在一起。
- **与 Navigation 的关系**：Reflow 作为 Navigation 的一个 destination，内部再使用 ViewPager2 做子页面滑动，两者互不替代：Navigation 负责主导航（如侧栏/底部导航），ViewPager2 仅负责 Reflow 内的水平分页。
- **生命周期**：FragmentStateAdapter 会按 ViewPager2 的预加载与回收策略创建/销毁子 Fragment，单页逻辑应放在各 Fragment 生命周期内处理；ReflowFragment 在 `onDestroyView` 中 detach TabLayoutMediator 并释放 binding，符合 Fragment 使用规范。

