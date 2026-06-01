[TOC]

## 一、导航组件概述

导航组件是 Android Jetpack 的一部分，用于实现应用内的导航功能。它基于 Fragment 的轻量级特性，相比传统的 Activity 导航，能显著减少资源消耗，提升应用性能和用户体验。

### 1.1 什么是导航组件

导航是指允许用户跨越、进入和退出应用中不同内容片段的交互。

Android Jetpack 的 Navigation 组件包含 [Navigation 库](https://developer.android.google.cn/jetpack/androidx/releases/navigation?hl=zh-cn)、[Safe Args Gradle 插件](https://developer.android.google.cn/guide/navigation/navigation-pass-data?hl=zh-cn#Safe-args)，以及可帮助您实现应用导航的工具。Navigation 组件可以处理各种导航用例，从普通的按钮点击、屏幕顶部应用栏、屏幕底部导航栏、抽屉导航栏到更负责的模式。

导航组件的具体优势如下：

- **动画与过渡**：提供标准化的动画与过渡资源。
- **深层链接**：实现并处理可让用户直接跳转到目标页面的深层链接。
- **UI 模式**：以最少额外工作量支持侧边导航抽屉、底部导航等界面模式。
- **类型安全**：支持在目的地之间以类型安全的方式传递数据。
- **ViewModel 支持**：可将 ViewModel 作用域限定在导航图，以在图中各目的地间共享与 UI 相关的数据。
- **Fragment 事务**：完全支持并处理 Fragment 事务。
- **返回与向上导航**：默认正确处理返回（Back）和向上（Up）操作。

> **注意**：Android 13 引入了预测性返回导航功能，该功能与 Android 设备的导航组件配合使用。请尽快在您的应用中实现预测性返回导航，否则用户在未来 Android 版本中可能会遇到意外行为。



### 1.2 关键概念

| 概念                      | 目的                                                         | 对应类型（Type）                                             |
| :------------------------ | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **宿主（Host）**          | 一个**容纳当前导航目的地**的 UI 容器。当用户在应用中导航时，应用实际上是在宿主中切换不同的目的地。 | Compose：[`NavHost`](https://developer.android.google.cn/reference/kotlin/androidx/navigation/compose/package-summary#NavHost(androidx.navigation.NavHostController,androidx.navigation.NavGraph,androidx.compose.ui.Modifier,androidx.compose.ui.Alignment,kotlin.Function1,kotlin.Function1,kotlin.Function1,kotlin.Function1,kotlin.Function1))<br>Fragments：[`NavHostFragment`](https://developer.android.google.cn/reference/androidx/navigation/fragment/NavHostFragment) |
| **导航图（Graph）**       | 一个数据结构，定义应用内所有导航目的地以及它们之间的连接关系。 | [`NavGraph`](https://developer.android.google.cn/reference/androidx/navigation/NavGraph) |
| **控制器（Controller）**  | 管理目的地之间导航的核心协调器。控制器提供在目的地之间导航、处理深层链接、管理返回堆栈等方法。 | [`NavController`](https://developer.android.google.cn/reference/androidx/navigation/NavController) |
| **目的地（Destination）** | 导航图中的一个节点。当用户导航到此节点时，宿主会显示其内容。通常是在构建导航图时创建。 | [`NavDestination`](https://developer.android.google.cn/reference/androidx/navigation/NavDestination) |
| **路由（Route）**         | 唯一标识一个目的地及其所需的任何数据。可以通过路由进行导航。路由将带你到达目的地。 | 任何可序列化的数据类型                                       |

**关系简述**：导航图与导航宿主需要**显式绑定**（如 View 里 `app:navGraph`、Compose 里 `NavHost { }` 内定义）；导航控制器与宿主则无需单独绑定——控制器由宿主内部创建并持有（View），或在构造宿主时传入（Compose）。

### 1.3 导航的原则

导航组件默认实现 [导航原则](https://developer.android.google.cn/guide/navigation/principles?hl=zh-cn) 来确保一致且可预测的用户体验，从而确保用户在各应用之间切换时能够使用相同的启发法和模式进行导航。

- **固定的起始目的地**：除了用户在特定情况下才看到的一次性设置或者一系列登录屏幕，用户从启动器启动您的应用时看到的第一个屏幕应该是固定的。
- **导航状态表示为目的地堆栈**: 导航组件将用户的浏览路径记录为一个目的地堆栈，堆栈底部是固定的起始页，顶部是当前屏幕。用户每次前往新页面，就相当于在堆栈顶部放入新页面；点击返回，则移除顶部页面，从而回到之前的位置。这套机制由系统自动管理，确保了导航的一致性与可预测性。
- **在应用的任务中，向上按钮和返回按钮的行为相同**：返回按钮显示在屏幕底部的系统导航栏中，用于按照时间倒序浏览用户最近访问过的屏幕的历史记录。向上按钮显示在屏幕顶部应用栏中，用于根据应用内部层级关系向上导航当行当前界面的 “父级” 界面。在绝大多数应用内部，浏览顺序（时间顺序）和界面层级（父子关系）是**一致的**。

- **向上按钮绝不会使用户退出您的应用**: 在用户通过其他应用点击[深层链接](https://developer.android.google.cn/training/app-links/deep-linking?hl=zh-cn)启动您的应用时， 向上按钮会通过[模拟的返回堆栈](https://developer.android.google.cn/guide/navigation/principles?hl=zh-cn#deep-link)使用户返回到您应用的任务，而不是返回到触发深层链接的应用。但返回按钮遵循时间顺序，使用户直接回到刚才点击链接的那个外部应用。
- **深层链接可模拟手动导航**:  当深层链接到应用任务中的某个目的地时，系统会移除应用任务的任何现有返回堆栈(应用原有的浏览历史)，并将其替换为深层链接的合成返回堆栈(从应用首页自然进入目标页面的虚拟路径)。这使得用户点击向上按钮时，能像正常操作一样层层返回首页，而不会回到之前浏览的其他页面。

> 向上按钮依据的是应用内部层级，而返回按钮依据的是系统历史记录。





## 二、基本使用方法

### 2.1 组件依赖

添加 Navigation 组件依赖到 `build.gradle` 文件：

```kotlin
plugins {
  // Kotlin serialization plugin for type safe routes and navigation arguments
  kotlin("plugin.serialization") version "2.0.21"
}

dependencies {
  val nav_version = "2.9.7"

  // Jetpack Compose integration
  implementation("androidx.navigation:navigation-compose:$nav_version")

  // Views/Fragments integration
  implementation("androidx.navigation:navigation-fragment:$nav_version")
  implementation("androidx.navigation:navigation-ui:$nav_version")

  // Feature module support for Fragments
  implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

  // Testing Navigation
  androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

  // JSON serialization library, works with the Kotlin serialization plugin
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}
```





### 2.2 Navigation Controller 使用方法

#### 2.2.1 创建 Navigation Controller 

在 View 系统中，`NavController`通常不是由开发者直接 `new`出来的，而是通过 [**查找**](https://developer.android.google.cn/guide/navigation/navcontroller) 的方式获取。这是因为 Navigation 组件在 View 系统中深度集成了 Fragment 管理。

- `findNavController()`： 系统会沿着 View 的层级结构向上查找，直到找到关联的 `NavHostFragment`，然后返回其内部的 `NavController`。注意，在 `Activity.onCreate()`中调用 `findNavController()`时，系统可能**找不到**有效的导航宿主，从而导致崩溃。
- `getNavController()`: 从**已成功附加**的 `NavHostFragment`对象直接获取其内部的 `NavController`实例。这样确保了 `NavController`已准备好，避免了 fragment 未完成事务时查找失败。

```java
NavHostFragment navHostFragment =
    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
NavController navController = navHostFragment.getNavController();
```

在 Compose 中，由于没有 XML 布局和 Fragment 的约束，`NavController`的获取方式变得更加直接和声明式。通常在可组合项树的**最顶层**（如 `MainActivity`的 `setContent`中）创建 `NavController`，然后通过参数向下传递，遵循 Compose 的**状态提升 (State Hoisting)** 原则。

```kotlin
val navController = rememberNavController()
```



#### 2.2.2 使用导航图

- **谁持有导航图**：View 中由 `NavHostFragment` 通过布局里的 `app:navGraph` 绑定一份导航图；Compose 中在 `NavHost { composable(...) }` 里用代码定义。拿到 `NavController` 后，它已经和这份图关联好了。
- **怎么用**：调用 `navController.navigate(目的地 ID)` 或 `navController.navigate(动作 ID)` 时，会在当前关联的导航图中查找目标并执行跳转；返回用 `navController.popBackStack()`。
- **可选**：通过 `navController.currentBackStackEntry`、`navController.graph` 等可获取当前目的地、整张图结构，用于根据当前页做 UI 或逻辑分支。



### 2.3 设计导航图 Navigation Graph

#### 2.3.1 导航图的组成元素

导航图可以由以下项的任意组合构成：

- 单个目的地，例如 `<fragment>` 目的地。
- 封装了一组相关目的地的[嵌套图](https://developer.android.google.cn/guide/navigation/navigation-nested-graphs?hl=zh-cn)。
- [``](https://developer.android.google.cn/guide/navigation/navigation-nested-graphs?hl=zh-cn#include) 元素，可让您嵌入另一个导航图文件，就像事先完成了嵌套一样。

借助这种灵活的组合方式，您可以将一些较小的导航图组合在一起，形成应用的完整导航图。即便这些较小的导航图是由不同的[模块](https://developer.android.google.cn/topic/modularization?hl=zh-cn)提供，也没有关系。



#### 2.3.1 目的地类型

| 类型 (Type)               | 标签 (XML)   | 描述 (Description)                                           | 典型用例 (Use Cases)                                         |
| :------------------------ | :----------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **托管目的地 (Hosted)**   | `<fragment>` | 填满整个导航宿主。也就是说，托管目的地的大小与导航宿主相同，并且先前的目的地不可见。 | 主界面和详情界面。        |
| **对话框目的地 (Dialog)** | `<dialog>`   | 呈现覆盖式的 UI 组件。此 UI 不依赖于导航宿主的位置或其大小。先前的目的地在当前目的地之下可见。 | 提示框、选择器、表单。     |
| **活动目的地 (Activity)** | `<activity>` | 代表应用内独立的屏幕或功能。作为导航图的出口点，会启动一个不受 Navigation 组件管理的新 Android Activity。 | 在与第三方 Activity 交互时，或作为迁移过程的一部分使用。|




#### 2.3.2 创建 Navigation Graph

Navigation Graph 的具体实现方式根据所使用的框架（View 或 Compose）而有所不同，具体细节可查看 [Design your navigation graph | Android Developers](https://developer.android.google.cn/guide/navigation/design#xml)。

**(1) Compose 框架**

在 **Compose** 框架中，使用 `NavHost` 作为导航宿主，通过  [Kotlin DSL](https://developer.android.google.cn/guide/navigation/navigation-kotlin-dsl) 向其中添加导航图。创建图的方式有两种：

- **编程方式**：先调用 [`NavController.createGraph()`](https://developer.android.google.cn/reference/androidx/navigation/NavController#(androidx.navigation.NavController).createGraph(kotlin.String,kotlin.String,kotlin.Function1)) 生成 `NavGraph`，再将该图传给 `NavHost`。

  ```kotlin
  val navGraph by remember(navController) {
    navController.createGraph(startDestination = Profile)) {
      composable<Profile> { ProfileScreen( /* ... */ ) }
      composable<FriendsList> { FriendsListScreen( /* ... */ ) }
    }
  }
  NavHost(navController, navGraph)
  ```

- **内联在 NavHost 中**：在添加 `NavHost` 时直接在其 lambda 里用 `composable(...) { }` 等构建导航图。每个路由都被作为一个类型参数传递给 [`NavGraphBuilder.composable()`](https://developer.android.google.cn/reference/kotlin/androidx/navigation/NavGraphBuilder#(androidx.navigation.NavGraphBuilder).composable(kotlin.collections.Map,kotlin.collections.List,kotlin.Function1,kotlin.Function1,kotlin.Function1,kotlin.Function1,kotlin.Function1,kotlin.Function2)) 方法，这个方法将目的地添加到最终生成的导航图中。

  ```kotlin
  @Serializable
  object Profile
  @Serializable
  object FriendsList
  
  val navController = rememberNavController()
  
  NavHost(navController = navController, startDestination = Profile) {
      composable<Profile> { ProfileScreen( /* ... */ ) }
      composable<FriendsList> { FriendsListScreen( /* ... */ ) }
      // Add more destinations similarly.
  }
  ```




**(2) View 框架**

在 View 框架中使用 Fragment 时候，使用 `NavHostFragment` 作为导航宿主。创建导航图的方式包括：

- **编程方式（Kotlin DSL）**：用 Kotlin DSL 动态创建 `NavGraph` 并应用到 `NavHostFragment`。与 Compose 共用同一套 `createGraph()` API。
- **XML**：在 XML 中静态编写导航宿主与导航图。
- **Android Studio 编辑器**：在 Android Studio 中用 [图形编辑器](https://developer.android.google.cn/guide/navigation/design/editor) 创建、调整导航图，保存为 XML 资源。

> 不同框架下通过 `NavController` 与导航图交互的方式类似，详见 [Navigate to a destination](https://developer.android.google.cn/guide/navigation/navigate)。





### 2.4 简单案例（View 框架）

以下以 **View 框架 + Java 语言 + XML 导航图** 为例，说明从零搭建导航的完整流程。**流程概览：**

1. 添加依赖
2. 创建导航图 XML（含配置目的地）
3. 在布局中添加 NavHostFragment, 并指定导航图 
4. 在代码中通过 NavController 实现跳转


#### 2.4.1 添加依赖

在模块的 `build.gradle` 中添加 Navigation 的 Fragment 与 UI 依赖（Java 项目可用 Groovy 或 KTS，此处以版本变量示意）：

```groovy
dependencies {
    def nav_version = "2.9.7"

    // View/Fragment 导航
    implementation "androidx.navigation:navigation-fragment:$nav_version"
    implementation "androidx.navigation:navigation-ui:$nav_version"
}
```





#### 2.4.2 创建导航图 XML

在 res/navigation/目录下新建一个导航图资源文件（如 nav_graph.xml），以定义应用中的目的地（屏幕）以及它们之间的导航路径。

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/nav_home">

    <!-- 定义第一个目的地：HomeFragment -->
    <fragment
        android:id="@+id/nav_home"
        android:name="com.example.app.HomeFragment"
        android:label="首页">
        <!-- 定义一个从 nav_home 跳转到 nav_detail 的动作 -->
        <action
            android:id="@+id/action_nav_home_to_nav_detail"
            app:destination="@id/nav_detail" />
    </fragment>

    <!-- 定义第二个目的地：DetailFragment -->
    <fragment
        android:id="@+id/nav_detail"
        android:name="com.example.app.DetailFragment"
        android:label="详情">
        <!-- 定义一个返回动作，并清空返回堆栈 -->
        <action
            android:id="@+id/action_nav_detail_back_to_nav_home"
            app:destination="@id/nav_home"
            app:popUpTo="@id/nav_home"
            app:popUpToInclusive="true" />
    </fragment>
</navigation>
```

其中：

1. **`<navigation>`**：导航图的根元素。
   - `app:startDestination`：指定应用启动时或导航到该图时的默认起始页。

2. **`<fragment>`**：定义一个目的地，代表应用中的一个 Fragment 屏幕。
   - `android:id`：该目的地的唯一标识符，在代码中用于导航（如 `R.id.nav_home`）。与该 Fragment 自身布局 XML 里的控件 id 无关。
   - `android:name`：该目的地所对应的 Fragment 类的全限定名。
   - `android:label`：（可选）用于在界面（如应用栏）中显示的标题。

3. **`<action>`**：动作，定义从一个目的地到另一个目的地的可复用导航路径。
   - `android:id`：该动作的唯一标识符。
   - `app:destination`：指定该动作将用户带往的目标目的地 ID。
   - `app:popUpTo`：用于管理返回堆栈；导航时，系统会将堆栈中直到指定目的地（ID）的所有其他目的地都弹出。
   - `app:popUpToInclusive="true"`：与 `app:popUpTo` 联用；设为 `true` 时，会将 `popUpTo` 指定的目的地本身也从堆栈中移除，常用于实现「返回首页并重置导航历史」。


> **提示**：使用 XML 定义导航图时，可用 Android Studio 的 [导航编辑器](https://developer.android.com/guide/navigation/design/editor) 可视化查看和编辑。



#### 2.4.3 在布局中添加 NavHostFragment

在 Activity 的布局 XML 中放置一个 `FragmentContainerView` 作为 NavHost，并指定导航图。该布局仍是普通 Activity 布局，可同时加入 Toolbar、底部导航栏、FAB 等其它组件，与 NavHost 并列或上下排布。

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:defaultNavHost="true"
        app:navGraph="@navigation/nav_graph" />
    
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav_view"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:layout_gravity="bottom"
        app:menu="@menu/bottom_navigation" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- `android:name="...NavHostFragment"` 指定导航宿主。
- `app:navGraph` 指向 2.4.2 中创建的导航图。
- `app:defaultNavHost="true"` 使该宿主处理系统返回键。



#### 2.4.4 实现导航逻辑

在 Activity 或 Fragment 中获取 `NavController`，然后调用 `navigate()` 进行跳转：

```java
// 在 Activity 中
NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
        .findFragmentById(R.id.nav_host_fragment);
NavController navController = navHostFragment.getNavController();

// 点击按钮跳转到详情
findViewById(R.id.btn_go_detail).setOnClickListener(v ->
        navController.navigate(R.id.nav_detail));
```



### 2.5 简单案例（Compose 框架）

以下以 **Compose 框架 + Kotlin 语言** 为例，用代码定义路由和导航图，实现相同流程。**流程概览：**

1. 添加 Compose 与 Navigation 依赖 
2. 定义路由与 NavHost 
3. 使用 `rememberNavController()` 与 `navigate()` 控制跳转

---

#### 2.5.1 添加依赖

在模块的 `build.gradle.kts` 中：

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    val nav_version = "2.9.7"

    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
}
```

若使用类型安全路由（Kotlin Serialization），可额外添加 `kotlin("plugin.serialization")` 和 `kotlinx-serialization-json`（参见 2.1 组件依赖）。

#### 2.5.2 定义路由与 NavHost

在 Kotlin 中用密封类或字符串定义路由，并在根 Composable 中创建 `NavHost`：

```kotlin
// 路由定义，集中管理所有导航目的地
object Routes {
    const val HOME = "home"
    const val DETAIL = "detail"
}

@Composable
fun AppNavHost() {
    // 1. 创建/记住导航控制器
    val navController = rememberNavController()

    // 2. 创建导航宿主并定义导航图
    NavHost(
        navController = navController,  // 注入控制器
        startDestination = Routes.HOME  // 设置起始目的地
    ) {

        // 3. 定义名为 “home” 的目的地
        composable(Routes.HOME) {
            HomeScreen(onNavigateToDetail = {
                // 4. 导航到 “detail” 目的地
                navController.navigate(Routes.DETAIL)
            })
        }

        // 5. 定义名为 “detail” 的目的地
        composable(Routes.DETAIL) {
            // 6. 返回上一个目的地（即 “home”）
            DetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
```

整体流程与设计模式细节如下：
1. 启动流程：应用启动时，AppNavHost被调用。导航控制器创建，NavHost将 Routes.HOME设为起始目的地，因此首先显示 HomeScreen。
2. 正向导航：在 HomeScreen中，用户触发 onNavigateToDetail，控制器执行 navigate(Routes.DETAIL)。堆栈变为 [HOME, DETAIL]，界面切换为 DetailScreen。
3. 返回导航：在 DetailScreen中，用户触发 onBack，控制器执行 popBackStack()。堆栈变回 [HOME]，界面切换回 HomeScreen。
4. 状态提升：这是一个优秀的状态提升（State Hoisting）实践。NavController被创建并管理在顶层的 AppNavHost中，然后通过 回调函数（onNavigateToDetail, onBack）将导航能力传递给子屏幕。这使得子屏幕（HomeScreen, DetailScreen）无需知道 navController的具体存在，它们只是发出“想要导航”或“想要返回”的意图，从而实现了关注点分离和更好的可测试性。

`HomeScreen` / `DetailScreen` 为自定义 `@Composable` 函数，内部可调用传入的 `onNavigateToDetail`、`onBack` 等完成跳转或返回。

#### 2.5.3 在 Activity 中启用 Compose

在 `MainActivity` 中设置 Compose 内容为上述 NavHost：

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Material3Theme {
                AppNavHost()
            }
        }
    }
}
```



#### 2.5.4 实现导航逻辑

在定义 `NavHost` 时把跳转/返回封装成回调传给子界面，子界面在合适时机调用即可。示例：

```kotlin
// 子界面只接收回调，在点击时调用
@Composable
fun HomeScreen(onNavigateToDetail: () -> Unit) {
    Button(onClick = onNavigateToDetail) {
        Text("去详情")
    }
}

@Composable
fun DetailScreen(onBack: () -> Unit) {
    Button(onClick = onBack) {
        Text("返回")
    }
}
```

`AppNavHost` 里用 `navController.navigate(Routes.DETAIL)` 和 `navController.popBackStack()` 实现这两个回调即可（见 2.5.2）。





## 三、进阶用法

### 3.1 导航图设计的进阶用法

#### 3.1.1 嵌套图（Nested Graphs）

嵌套图把一段**子流程**（如登录、向导）收进一张子图，便于维护、复用和封装；根图只 `navigate()` 到嵌套图本身，首屏由子图的 `startDestination` 决定。使用方式：在根 `<navigation>` 内再写一层 `<navigation>` 或在编辑器中「Move to Nested Graph > New Graph」；根图通过 action 的 `app:destination="@id/嵌套图id"` 跳转，或用 `<include app:graph="..." />` 引入独立图文件。

**示例：根图内嵌子图**

```xml
<!-- nav_graph.xml 根图：首页 + 登录子图 -->
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/nav_home">

    <fragment android:id="@+id/nav_home" ...>
        <action android:id="@+id/action_home_to_login_graph"
                app:destination="@id/login_graph" />
    </fragment>

    <!-- 嵌套图：登录流程 -->
    <navigation android:id="@+id/login_graph"
                app:startDestination="@id/nav_login">
        <fragment android:id="@+id/nav_login" ... />
        <fragment android:id="@+id/nav_register" ... />
    </navigation>
</navigation>
```
跳转到登录子图：`navController.navigate(R.id.action_home_to_login_graph)`，将进入 `login_graph` 的起始目的地 `nav_login`。



#### 3.1.2 深层链接（Deep Links）

**深层链接**指将用户直接带到应用内某一目的地的链接或 Intent。
- **显式深层链接**由应用自己用 `PendingIntent` 构建并持有，用于通知、App Widget、快捷方式等「由本应用主动发起的跳转」；
- **隐式深层链接**通过 URI、Intent action 或 MIME 类型与系统匹配，当用户点击外部链接或系统分发 Intent 时，由系统打开应用并进入对应目的地。

**（1）创建显式深层链接**

使用 `NavDeepLinkBuilder` 构建一个指向某目的地的 `PendingIntent`，再将该 PendingIntent 用于通知、Widget 等。若 NavHost 不在默认启动 Activity 中，需调用 `.setComponentName(Activity::class.java)` 指定宿主 Activity。


```java
PendingIntent pendingIntent = new NavDeepLinkBuilder(context)
    .setGraph(R.navigation.nav_graph)
    .setDestination(R.id.nav_detail)
    .setArguments(args)
    .setComponentName(DestinationActivity.class)  // 在不指定时，默认启动到应用清单中声明的默认启动 Activity
    .createPendingIntent();
```

**（2）创建隐式深层链接**

在导航图中为目的地添加 `<deepLink>`，声明可匹配的 URI（或 `app:action`、`app:mimeType`）；在 `AndroidManifest.xml` 的宿主 Activity 下添加 `<nav-graph android:value="@navigation/nav_graph" />`，构建时会自动生成匹配图中所有 deepLink 的 intent-filter。URI 中可用 `{id}` 等占位符，与目的地的参数名对应。

首先，通过 URI、intent 操作和 MIME 类型匹配深层链接。您可以为单个深层链接指定多个匹配类型，但请注意，匹配的优先顺序依次是 URI 参数、操作和 MIME 类型。
```xml
<!-- 导航图中：为 nav_detail 声明 URI 深层链接 -->
<fragment android:id="@+id/nav_detail"  
          android:name="com.example.myapplication.FragmentA"
          tools:layout="@layout/a">
        <deepLink app:uri="https://example.com/detail/{id}"
                app:action="android.intent.action.MY_ACTION"
                app:mimeType="type/subtype"/>
</fragment>
```
然后，为了启动隐式深层链接，在 **已使用该导航图的 Activity**（即布局里有 NavHostFragment 且 `app:navGraph` 指向该图的 Activity）的声明内，添加 `<nav-graph>`，这样系统才能根据图中的 `<deepLink>` 生成 intent-filter。在 `AndroidManifest.xml` 中写法如下：
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myapplication">

    <application ... >

        <activity name=".MainActivity" ...>
            ...

            <nav-graph android:value="@navigation/nav_graph" />

            ...

        </activity>
    </application>
</manifest>
```

**（3）处理深层链接**

使用默认 `standard` launchMode 时，Navigation 会在 Activity 启动时自动处理 Intent 中的深层链接。若使用 `singleTop`（或其它会复用已有 Activity 的 launchMode），需在 `onNewIntent()` 中手动把新 Intent 交给 NavController 处理，否则从外部再次点击深层链接可能不会跳转。

```kotlin
// Kotlin
override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    navController.handleDeepLink(intent)
}
```

```java
// Java
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    navController.handleDeepLink(intent);
}
```




### 3.2 导航图使用的进阶用法

#### 3.2.1 在目的地之间添加动画过渡效果

在目的地之间添加动画过渡效果的实现方式主要有：
- 指定 Action 的动画（enterAnim / exitAnim / popEnterAnim / popExitAnim），整屏的进入/退出效果（例如当前页向左滑出、目标页从右滑入），不指定具体哪个 View。
- 共享元素过渡（Shared Element Transition）, 让某个具体 View（如图片、标题）从 A 页「飞」到 B 页的对应位置，两页之间有一个共同的视觉元素在连续运动。

> 注意，这两个方式不能混用。详见 [在目的地之间添加动画过渡效果](https://developer.android.google.cn/guide/navigation/use-graph/animate-transitions?hl=zh-cn)。

**(1) 指定 Action 的动画**

在 `<action>` 上配置四类动画：`app:enterAnim`、`app:exitAnim`、`app:popEnterAnim`、`app:popExitAnim`，指向 `res/anim` 下的动画资源。

```xml
<fragment android:id="@+id/nav_home" ...>
    <action
        android:id="@+id/action_nav_home_to_nav_detail"
        app:destination="@id/nav_detail"
        app:enterAnim="@anim/slide_in_right"
        app:exitAnim="@anim/slide_out_left"
        app:popEnterAnim="@anim/slide_in_left"
        app:popExitAnim="@anim/slide_out_right" />
</fragment>
```

**（2）共享元素过渡**

在代码中通过 `FragmentNavigator.Extras` 将「共享的 View」与 `transitionName` 传给 `navigate()`；目标 Fragment 中对应 View 需设置相同的 `android:transitionName`。另外，若目的地是 Activity，返回时需在目标 Activity 的 `finish()` 中调用 `ActivityNavigator.applyPopAnimationsToPendingTransition(this)`。



```java
FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
    .addSharedElement(view1, "hero_image")
    .build();

Navigation.findNavController(view).navigate(
    R.id.details,
    null, // Bundle of args
    null, // NavOptions
    extras);
```

目标页布局中对应 View 需设置 `android:transitionName="hero_image"`，与代码中的名称一致。

#### 3.2.2 NavigationUI：将界面组件连接到 NavController

Navigation 组件提供[`NavigationUI`](https://developer.android.google.cn/reference/androidx/navigation/ui/NavigationUI?hl=zh-cn)，通过静态方法把**顶部应用栏（Toolbar / ActionBar）**、**抽屉式导航栏（DrawerLayout + NavigationView）**、**底部导航栏（BottomNavigationView）** 等与 `NavController` 绑定。绑定后可自动根据当前目的地更新标题（来自导航图目的地的 `android:label`）、在「向上」按钮与抽屉图标之间切换、高亮对应菜单项，并处理点击后的导航，无需在每个界面手写逻辑。详见 [使用 NavigationUI 将界面组件连接到 NavController](https://developer.android.google.cn/guide/navigation/integrations/ui?hl=zh-cn)。

**AppBarConfiguration** 用来告诉 NavigationUI「哪些是顶层页面」以及「有没有抽屉」。  

- **顶层目的地**：处于导航层级最顶层的页面（例如首页、底部导航里的几个主 tab）。在这些页面上不显示「向上」按钮（因为上面没有父页面）；如果传入了 `DrawerLayout`，则显示抽屉图标，否则左侧导航按钮隐藏。  
- **怎么配**：只有一个主入口时，用 `AppBarConfiguration(navController.graph)`，会把起始目的地当作唯一顶层。有多个平级主屏（如底部导航）时，用 `AppBarConfiguration(setOf(R.id.a, R.id.b))` 传入顶层目的地 id 集合；**若界面里还有抽屉**（DrawerLayout），再把这个 drawer 对象作为第二个参数传进去，这样在顶层页面才会显示抽屉图标，否则只传 id 集合即可。

**示例：Toolbar + NavController**
首先，在布局中放置 `Toolbar`

```xml
<!-- Activity 布局：Toolbar + NavHost -->
<LinearLayout ...>
    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar" ... />
    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        app:navGraph="@navigation/nav_graph" ... />
</LinearLayout>
```
然后，在Activity 的 `onCreate()` 中调用 `toolbar.setupWithNavController(navController, appBarConfiguration)`（Kotlin 扩展）或 `NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)`（Java），即可自动更新标题并响应「向上」/抽屉图标点击。

```java
// Java
NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
NavController navController = navHostFragment.getNavController();
AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
NavigationUI.setupWithNavController(findViewById(R.id.toolbar), navController, appBarConfiguration);
```
导航图中目的地的 `android:label` 会作为 Toolbar 标题显示；使用 `{argName}` 可在 label 中引用目的地参数。

### 3.3 返回栈 BackStack

#### 3.3.1 状态管理策略
在 Android Navigation 组件中如何使用 NavController.getPreviousBackStackEntry()​ 和 SavedStateHandle​ 来实现跨目的地的状态持久化，特别是在用户登录场景中的应用。

- 使用 getPreviousBackStackEntry()​ 获取前一个目的地的状态容器
- 通过 SavedStateHandle​ 在目的地之间传递持久化状态，可以确保状态在进程终止后继续存在。
- 特别适用于登录流程：确保登录状态在导航、返回和进程重启后都能正确保持

相关案例可以查看 [条件导航  | App architecture  | Android Developers](https://developer.android.google.cn/guide/navigation/use-graph/conditional?hl=zh-cn)   [以程序化方式与 Navigation 组件交互  | App architecture  | Android Developers](https://developer.android.google.cn/guide/navigation/use-graph/programmatic?hl=zh-cn#navbackstackentry)





## 最佳实践

- 使用单一 Activity 架构，多 Fragment 实现界面

- 将导航逻辑与业务逻辑分离

- 为每个导航流创建独立的导航图
- 合理使用动画和过渡效果
- 处理返回栈，提供良好的返回体验





## 参考资料

[导航原则  | App architecture  | Android Developers](https://developer.android.google.cn/guide/navigation/principles?hl=zh-cn)