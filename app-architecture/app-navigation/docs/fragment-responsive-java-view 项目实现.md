## 一、项目概述

本项目采用**单 Activity + 多 Fragment** 架构，通过**资源限定符**为不同屏幕宽度提供多套布局，实现一套代码适配手机、平板与大屏：窄屏使用底部导航 + 溢出菜单，中等宽度使用侧边抽屉，大屏使用常驻侧边栏。

### 1.1 核心实现思路

1. **按最小宽度提供多套布局**：使用 `layout/`（默认）、`layout-w600dp/`、`layout-w1240dp/`，系统根据设备最小宽度自动选用对应布局。
2. **关键 View 的 id 保持一致**：`nav_host_fragment_content_main`、`nav_view`、`drawer_layout`、`app_bar_main` 等在各配置下 id 统一，便于代码通过 View Binding / `findViewById` 获取；不存在的视图为 `null`，用**判空**区分当前是哪种布局。
3. **同一 NavController 绑定多种导航 UI**：无论底部导航、抽屉还是常驻侧栏，都绑定同一个 `NavController`，保证导航目标与返回栈一致；`AppBarConfiguration` 根据是否存在抽屉分别配置。
4. **溢出菜单按需显示**：仅当当前布局中不存在 `nav_view`（即无侧栏/抽屉）时，在 `onCreateOptionsMenu` 中 inflate 溢出菜单，避免大屏上重复入口。



### 1.2 关键组件

| 组件 | 作用 |
|------|------|
| **MainActivity** | 统一处理 Toolbar、FAB、NavController 与多种导航 UI 的绑定；根据 `nav_view` 是否存在决定是否显示溢出菜单并处理返回键。 |
| **NavHostFragment** | 承载导航图 `mobile_navigation`，所有 Fragment 切换由 Navigation 组件管理。 |
| **AppBarConfiguration** | 配置顶级目标与可选的 `DrawerLayout`；有抽屉时用 `setOpenableLayout(drawerLayout)`，无抽屉时不设置。 |
| **BottomNavigationView** | 仅出现在默认 `content_main.xml`，提供 Transform / Reflow / Slideshow 三个入口。 |
| **NavigationView** | 在 w600dp 中作为抽屉子视图、在 w1240dp 中作为常驻侧栏，使用 `navigation_drawer` 菜单（含设置）。 |
| **overflow 菜单** | 仅在无 `nav_view` 时 inflate，提供“设置”入口，点击后通过 `NavController.navigate(R.id.nav_settings)` 跳转。 |



### 1.3 项目结构（与实现相关部分）

```
app/src/main/
├── java/.../responsive/
│   ├── MainActivity.java              # 主 Activity，多布局下的导航与菜单逻辑
│   └── ui/
│       ├── transform/                 # Transform 页
│       ├── reflow/                    # Reflow 页
│       ├── slideshow/                 # Slideshow 页
│       └── settings/                  # Settings 页
└── res/
    ├── layout/                        # 默认（手机）：有底部导航，无侧栏
    │   ├── activity_main.xml         # DrawerLayout 内仅 app_bar，无 nav_view
    │   └── content_main.xml          # NavHost + BottomNavigationView
    ├── layout-w600dp/                 # 中等宽度：侧边抽屉
    │   ├── activity_main.xml         # DrawerLayout + NavigationView
    │   └── content_main.xml          # 仅 NavHost，无底部导航
    ├── layout-w1240dp/               # 大屏：常驻侧栏
    │   ├── activity_main.xml         # 无 DrawerLayout，仅 app_bar
    │   └── content_main.xml          # NavigationView(256dp) + NavHost + FAB
    ├── menu/
    │   ├── bottom_navigation.xml     # 底部导航：3 项（无设置）
    │   ├── navigation_drawer.xml     # 侧栏/抽屉：4 项（含设置）
    │   └── overflow.xml              # Toolbar 溢出：设置
    ├── navigation/
    │   └── mobile_navigation.xml     # 导航图，startDestination=nav_transform
    └── values/、values-w600dp/、values-w936dp/  # 不同宽度下的 dimens
```



### 1.4 各 XML 文件作用与设计原因

下面按类型说明项目中主要 XML 的**作用**以及**为什么要这样拆分**，便于理解响应式布局是如何通过“多套资源、同一套代码”实现的。


#### 布局类（layout）

**activity_main（整页）→ 里面是 app_bar_main（顶栏 + 内容区外壳）→ 里面是 content_main（NavHost 和导航控件）**

| 文件 | 作用 | 为什么要这样设计 |
|------|------|------------------|
| **activity_main.xml**（默认 / w600dp / w1240dp 各一份） | 定义 Activity 根结构：是否包含 `DrawerLayout`、是否包含侧栏 `NavigationView`、是否只保留 `app_bar`。 | 不同宽度下**根结构不同**（手机有抽屉容器但无侧栏、平板有抽屉+侧栏、大屏无抽屉且侧栏在 content 里），必须用多份 layout，系统按 `layout/`、`layout-w600dp/`、`layout-w1240dp/` 自动选一份。代码里通过同一 id（如 `nav_view`、`drawer_layout`）取 View，不存在的为 `null`，从而区分当前布局。 |
| **app_bar_main.xml**（默认 / w600dp / w1240dp 各一份） | 提供 Toolbar（AppBar）和 `content_main` 的 include；部分配置下包含 FAB。 | 默认布局中 FAB 需要避开底部导航（如 marginBottom 56dp），w600dp 中 FAB 在 app_bar 内、w1240dp 中不在此处放 FAB（FAB 在 content_main 内）。通过多份 app_bar 避免在代码里写宽度判断，只依赖“当前加载的是哪份布局”。 |
| **content_main.xml**（默认 / w600dp / w1240dp 各一份） | 定义主内容区：NavHost + 可选底部导航 / 可选常驻侧栏 + 可选 FAB。 | **导航 UI 的差异**集中在这里：手机 = NavHost + BottomNavigationView；中等宽度 = 仅 NavHost（导航在 activity 的抽屉里）；大屏 = 侧栏 + NavHost + FAB。id 统一（`nav_host_fragment_content_main`、`bottom_nav_view`、`nav_view`），代码判空即可。 |
| **fragment_*.xml**（如 fragment_transform、fragment_reflow 等） | 各 Fragment 的界面布局，由导航图引用。 | 每个页面一份布局，职责清晰；若同一 Fragment 在手机用列表、平板用网格，可为该 Fragment 在 `layout/` 与 `layout-w600dp/` 各提供一份同名布局，系统按宽度选用，Fragment 代码无需改。 |
| **item_transform.xml**（默认 / w600dp 可各一份） | Transform 列表/网格中单条的 item 布局。 | 小屏单列、大屏多列时，item 的宽高或排版可能不同（如用 `dimens` 或不同 layout），通过资源限定符区分，适配器只按 id 取 View。 |
| **nav_header_main.xml** | 侧边抽屉/常驻侧栏顶部的头部区域（头像、标题、副标题等）。 | 抽屉和常驻侧栏共用同一头部，只写一份，在 `NavigationView` 的 `app:headerLayout` 中引用，避免在多个 layout 里重复写同一块 UI。 |

#### 菜单类（menu）

| 文件 | 作用 | 为什么要这样设计 |
|------|------|------------------|
| **bottom_navigation.xml** | 底部导航栏的菜单项：Transform、Reflow、Slideshow（3 项，无设置）。 | 手机端主入口只有 3 个，设置放在 Toolbar 溢出菜单里更符合习惯；且底部导航 item 的 id 与导航图 destination id 一致，便于 `NavigationUI.setupWithNavController` 自动处理点击跳转。 |
| **navigation_drawer.xml** | 侧边抽屉/常驻侧栏的菜单项：上述 3 项 + 设置（4 项）。 | 平板/大屏有足够空间在侧栏展示全部入口，用户在一个地方即可到达所有页面；同样通过 id 与导航图一致，由 Navigation 统一处理。 |
| **overflow.xml** | Toolbar 右侧“三个点”溢出菜单，仅包含“设置”一项。 | 仅在**没有侧栏**（即 `nav_view == null`）时由 MainActivity inflate，避免手机端没有设置入口；有大屏侧栏时不再 inflate，避免“设置”出现两处。 |

#### 导航与资源配置

| 文件 | 作用 | 为什么要这样设计 |
|------|------|------------------|
| **mobile_navigation.xml** | 定义导航图：四个 destination（Transform、Reflow、Slideshow、Settings）及 startDestination。 | 所有导航方式（底部导航、抽屉、常驻侧栏、溢出菜单点击）都通过同一个 `NavController` 和同一张导航图跳转，保证返回栈与页面一致；只需维护一份导航图。 |
| **values/dimens.xml** | 默认边距、FAB 边距、列表项图标尺寸等。 | 布局中引用 `@dimen/xxx`，便于统一调整；为不同宽度提供覆盖值（见下），实现大屏更大间距与控件尺寸，无需在代码里写 dp 判断。 |
| **values-w600dp/dimens.xml**、**values-w936dp/dimens.xml** | 在对应最小宽度下覆盖部分 dimens（如 `fragment_horizontal_margin`、`fab_margin`、`item_transform_image_length`）。 | 系统按设备最小宽度自动选用，同一布局文件在不同宽度下会拿到不同的 dimen 值，从而“同一份 layout、不同尺寸效果”，实现响应式间距与控件大小。 |
| **values/colors.xml、themes.xml、strings.xml** | 颜色、主题、文案。 | 集中管理，便于换肤与多语言；本项目中未按宽度再拆分，若需“大屏不同主题”可增加 values-w600dp 等下的 themes。 |

**小结**：布局与菜单的“多份 XML”是为了让**系统根据屏幕宽度自动选资源**，代码只依赖**固定 id + 判空**，不手写宽度判断；导航图与 dimens 的“一份/多份”则保证**导航逻辑统一**、**尺寸随宽度变化**，从而用最少代码实现响应式界面。



## 二、功能模块详解

### 2.1 三种布局下的导航与内容区

| 配置 | 最小宽度 | activity_main | content_main | 导航方式 | 设置入口 |
|------|----------|---------------|--------------|----------|----------|
| 默认 | &lt; 600dp | DrawerLayout，内仅 app_bar | NavHost + BottomNav | 底部导航（3 项） | Toolbar 溢出菜单 |
| w600dp | ≥ 600dp | DrawerLayout + NavigationView | 仅 NavHost | 侧边抽屉（4 项） | 抽屉内 |
| w1240dp | ≥ 1240dp | 仅 app_bar | 侧栏 + NavHost + FAB | 常驻侧栏（4 项） | 侧栏内 |

- **默认布局**：`activity_main` 中虽有 `DrawerLayout`，但未包含 `NavigationView`，故 `binding.navView` 为 `null`；`content_main` 中有 `bottom_nav_view`，只展示 Transform / Reflow / Slideshow，设置通过溢出菜单进入。
- **w600dp**：`activity_main` 中增加 `NavigationView`（`nav_view`），作为抽屉从左侧滑出，使用 `navigation_drawer`（含设置）；`content_main` 无底部导航，仅 NavHost。
- **w1240dp**：无 `DrawerLayout`，`content_main` 内左侧固定 256dp 的 `NavigationView`，右侧为 NavHost，内容区与侧栏并排；FAB 出现在此布局中。

### 2.2 MainActivity 中的关键逻辑

**导航与 AppBar 配置（onCreate）：**

- 获取 `NavController`：通过 `findFragmentById(R.id.nav_host_fragment_content_main)` 得到 `NavHostFragment` 再取 `getNavController()`。
- 若 `binding.navView != null`：当前为抽屉或常驻侧栏布局，使用包含四个目标的 `AppBarConfiguration`，并 `setOpenableLayout(binding.drawerLayout)`（w1240dp 下 `drawerLayout` 为 null，效果为不显示汉堡菜单）；然后 `NavigationUI.setupWithNavController(navigationView, navController)`。
- 若 `binding.appBarMain.contentMain.bottomNavView != null`：当前为手机布局，使用仅包含三个目标的 `AppBarConfiguration`（无设置），并 `NavigationUI.setupWithNavController(bottomNavigationView, navController)`。

**溢出菜单（onCreateOptionsMenu）：**

- 使用 `findViewById(R.id.nav_view)` 判断侧栏是否存在；若为 `null`，则 `getMenuInflater().inflate(R.menu.overflow, menu)`，只在无侧栏时显示“设置”等溢出项。

**菜单项与返回（onOptionsItemSelected / onSupportNavigateUp）：**

- 溢出菜单中的“设置”项：`item.getItemId() == R.id.nav_settings` 时，通过 `NavController.navigate(R.id.nav_settings)` 跳转。
- `onSupportNavigateUp`：通过 `NavigationUI.navigateUp(navController, mAppBarConfiguration)` 处理返回或打开/关闭抽屉。

### 2.3 导航图与菜单 id 对应关系

- `mobile_navigation.xml` 中定义四个 destination：`nav_transform`、`nav_reflow`、`nav_slideshow`、`nav_settings`。
- `bottom_navigation.xml` 仅包含前三个 item（与导航图 id 一致），用于底部导航。
- `navigation_drawer.xml` 包含四个 item（含 `nav_settings`），用于抽屉与常驻侧栏。
- `overflow.xml` 仅包含 `nav_settings`，用于手机端 Toolbar 溢出菜单；点击后在 `onOptionsItemSelected` 中统一用 `NavController.navigate(R.id.nav_settings)` 跳转。

### 2.4 尺寸与布局资源

- **dimens**：`values/dimens.xml` 为默认边距与控件尺寸；`values-w600dp/dimens.xml`、`values-w936dp/dimens.xml` 中可覆盖 `fragment_horizontal_margin`、`fab_margin`、`item_transform_image_length` 等，实现大屏更大间距与图标。
- **Fragment 布局**：如 Transform 在 `layout/fragment_transform.xml` 与 `layout-w600dp/fragment_transform.xml` 中可分别使用列表与网格等不同布局，同一 Fragment 类通过系统按宽度选用的布局文件呈现不同 UI。




## 三、实现效果

- **手机（窄屏）**：底部三个 Tab（Transform、Reflow、Slideshow），Toolbar 右侧溢出菜单中有“设置”，点击进入 Settings 页；无侧栏。
- **中等宽度（如 7 寸平板）**：Toolbar 左侧汉堡菜单可打开侧边抽屉，抽屉内四个入口（含设置）；无底部导航。
- **大屏（如 10 寸平板或折叠展开）**：左侧常驻 256dp 侧栏，始终显示四个导航项，主内容区在右侧；无抽屉交互，FAB 显示在内容区右下角。

三种布局共用同一 `MainActivity`、同一导航图与同一套 Fragment，仅通过多套 layout 与判空逻辑实现响应式导航与菜单，无需在业务代码中手写屏幕宽度判断。更多理论与断点选择可参考同目录下的 [响应式布局.md](响应式布局.md)。
