## 一、Fragment 概述

### 1.1 什么是 Fragment

[**`Fragment`**](https://developer.android.google.cn/reference/androidx/fragment/app/Fragment?hl=zh-cn) 是 Android 3.0 引入的概念，表示应用界面中<font color="red">**可重复使用**</font>的一部分。它是一个依赖于 Activity 的<font color="red">**模块化组件**</font>，不能独立存在。Fragment 可以看作是 Activity 界面的一部分，用于构建界面的局部模块。Fragment 是一种可嵌入在 Activity 中的 UI 片段，用于构建动态和灵活的用户界面，更加合理和充分利用大屏幕的空间。

假设有一个响应各种屏幕尺寸的应用。在大屏设备上，您可能希望应用以网格布局显示静态抽屉式导航栏和列表。在小屏设备上，您可能希望应用以线性布局显示底部导航栏和列表。在 activity 中管理这些变体非常麻烦。将**导航元素与内容分离可使此过程更易于管理。**然后，activity 负责显示正确的导航界面，而 fragment 采用适当的布局显示列表。

![同一屏幕的采用不同屏幕尺寸的两个版本。在左侧，大屏幕包含一个由 activity 控制的抽屉式导航栏和一个由 fragment 控制的网格列表。在右侧，小屏幕包含一个由 activity 控制的底部导航栏和一个由 fragment 控制的线性列表。](images/fragment-screen-sizes.png)



### 1.2 Fragment 的生命周期

Fragment 拥有自己的生命周期，且与宿主 Activity 的生命周期紧密关联。理解 Fragment 的生命周期有助于在正确的时机初始化资源、处理用户交互与释放资源。官方说明可参考 [fragment 生命周期 | App architecture | Android Developers](https://developer.android.google.cn/guide/fragments/lifecycle?hl=zh-cn)。

![Fragment 生命周期状态，以及它们与 Fragment 的生命周期回调和 Fragment 的视图生命周期之间的关系](images/fragment-view-lifecycle.png)

需要区分两个概念：

- **Fragment 生命周期**：描述 Fragment 实例从创建到销毁的整个过程。
- **Fragment 的视图生命周期**：描述 Fragment 所持有的 View 从创建(`onCreateView()`)到销毁(`onDestroyView()`)的过程。Fragment 的视图拥有**独立的** `Lifecycle`，由 Fragment 通过 `getViewLifecycleOwner()` 或 `getViewLifecycleOwnerLiveData()` 暴露。视图可能在 Fragment 仍存在时被销毁，因此若在 Fragment 中观察 LiveData 或执行与视图相关的操作，应使用 `getViewLifecycleOwner()` 而非 Fragment 本身，以避免内存泄漏或重复订阅。


另外，从图中可以看到，**Fragment 实例与它的 View 的生命周期并不同步**：View 可能已经销毁，而 Fragment 对象仍被 `FragmentManager` 持有。典型场景是 **返回栈**——当通过 `FragmentTransaction.replace()` 切换界面并 `addToBackStack(null)` 时，被替换的 Fragment 会先走 `onDestroyView()`，视图被回收以节省内存，但 Fragment 实例会保留在返回栈中，直到用户按返回键或该 Fragment 被移除。因此图中用两条线分别表示“Fragment 活到哪一步”和“视图活到哪一步”。

**如何选择 LifecycleOwner？**

| 场景 | 使用 |
|------|------|
| 逻辑与**整个 Fragment 的存亡**相关（如与 Activity 的通信、Fragment 级数据） | Fragment 的 `getLifecycle()` |
| 逻辑与**界面显示**相关（如观察 LiveData 更新 UI、动画、焦点） | `getViewLifecycleOwner()` |

这样在视图销毁（`onDestroyView`）后，基于 `getViewLifecycleOwner()` 的订阅会自动解除，避免在无 View 时更新 UI 或造成泄漏。详见 [fragment 生命周期](https://developer.android.google.cn/guide/fragments/lifecycle?hl=zh-cn)。


#### 1.2.1 状态

Fragment 在其生命周期中会经历以下状态（由 `FragmentManager` 管理）：

| 状态 | 说明 |
|------|------|
| **INITIALIZED** | Fragment 实例已创建，但尚未与 `FragmentManager` 或宿主 Activity 关联。 |
| **CREATED** | Fragment 已与 `FragmentManager` 关联，`onCreate()` 已执行。若已调用 `onCreateView()`，则视图已创建；否则视图尚未创建或已被销毁（例如在返回栈中）。 |
| **STARTED** | Fragment 可见性为“已启动”，`onStart()` 已执行。Fragment 可能尚未完全可见（例如被其他界面遮挡）。 |
| **RESUMED** | Fragment 处于前台且可与用户交互，`onResume()` 已执行。 |
| **DESTROYED** | Fragment 已被销毁，`onDestroy()` 和 `onDetach()` 已执行，不应再使用该实例。 |

状态沿 **INITIALIZED → CREATED → STARTED → RESUMED** 前进；在配置变更、返回键、`FragmentTransaction.replace()` 等情况下会反向回退，直至 **DESTROYED**。




#### 1.2.2 回调方法

以下按调用顺序列出 Fragment 的主要生命周期回调及其常见用途：

| 回调方法 | 调用时机 | 典型用途 |
|----------|----------|----------|
| **onAttach(Context)** | Fragment 与 Activity 首次关联时。 | 保存 Activity/Context 引用或与宿主建立通信接口。 |
| **onCreate(Bundle)** | Fragment 创建时（可在配置变更后恢复时收到 `savedInstanceState`）。 | 初始化非视图相关的数据、恢复状态、注册不需要 View 的监听。 |
| **onCreateView(...)** | 需要为 Fragment 创建或恢复视图时。 | 通过 `LayoutInflater` 填充布局并返回根 View；若使用构造函数传入布局，可不必重写。 |
| **onViewCreated(View, Bundle)** | 视图创建完成后立即调用。 | 通过 `findViewById` 初始化控件、设置点击等监听、订阅与视图相关的 LiveData（应使用 `getViewLifecycleOwner()`）。 |
| **onStart()** | Fragment 变为“已启动”状态，即将可见。 | 注册与界面可见性相关的监听（如广播、传感器）。 |
| **onResume()** | Fragment 进入前台，可与用户交互。 | 刷新界面数据、恢复动画或焦点。 |
| **onPause()** | Fragment 即将离开前台。 | 提交未保存的更改、暂停动画或占用资源的操作。 |
| **onStop()** | Fragment 不再可见。 | 注销在 `onStart()` 中注册的监听，释放可见性相关资源。 |
| **onDestroyView()** | Fragment 的视图正在被销毁。 | 清理与 View 绑定的引用、取消使用 `getViewLifecycleOwner()` 的订阅，避免持有已销毁的 View。 |
| **onDestroy()** | Fragment 正在被销毁。 | 释放 Fragment 级别的资源、注销在 `onCreate()` 中注册的监听。 |
| **onDetach()** | Fragment 与 Activity 解除关联。 | 清空对 Activity 的引用，防止内存泄漏。 |

**使用建议：**

- 在 **onCreateView / onViewCreated** 中做与界面相关的初始化；在 **onDestroyView** 中做对应的清理。
- 需要观察 LiveData 或执行与 View 相关的异步操作时，使用 **getViewLifecycleOwner()**，这样在视图销毁时会自动取消，避免在无视图时更新 UI。
- 保存与恢复状态使用 **onSaveInstanceState(Bundle)** 和 **onCreate(Bundle)** 中的 `savedInstanceState`，不要依赖视图在配置变更后仍然存在。

---

## 二、Fragment 使用方式

本节介绍如何创建 Fragment、在 Activity 中嵌入 Fragment（静态与动态两种方式），以及常用代码示例。

> 注意：某些 Android Jetpack 库（如 Navigation、BottomNavigationView 和 ViewPager2）经过精心设计，可与 fragment 配合使用。

### 2.1 创建 Fragment 类

#### 2.1.1 导入依赖

```kotlin
dependencies {
    val fragment_version = "1.8.9"

    // Java language implementation
    implementation("androidx.fragment:fragment:$fragment_version")
    // Kotlin
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
}
```



#### 2.1.2 创建 Fragment 并指定布局资源

继承 `androidx.fragment.app.Fragment`（推荐使用 AndroidX），并重写生命周期方法。其中 **onCreateView()** 负责将布局填充为 View 并返回，是创建界面最核心的一步。

```java
class ExampleFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 使用LayoutInflater将布局资源文件充气为View对象
        return inflater.inflate(R.layout.example_fragment, container, false);
    }
    
     @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 在这里初始化视图控件，例如设置按钮点击事件
    }
}
```

> Fragment 库还提供更专业的 Fragment 基类， 即[`DialogFragment`](https://developer.android.google.cn/reference/androidx/fragment/app/DialogFragment?hl=zh-cn) 和 [`PreferenceFragmentCompat`](https://developer.android.google.cn/reference/androidx/preference/PreferenceFragmentCompat?hl=zh-cn)。

从 AndroidX Fragment 1.3.0 起，可在构造函数中直接传入布局 ID。适用于**仅需静态布局**的简单 Fragment；若需根据参数动态选布局或在 `onCreateView` 中做复杂逻辑，仍应重写 **onCreateView()**。

```
class ExampleFragment extends Fragment {
    public ExampleFragment() {
        super(R.layout.example_fragment);
    }
}
```



### 2.2 向 Activity 添加 Fragment

Fragment 必须嵌入 [`FragmentActivity`](https://developer.android.google.cn/reference/androidx/fragment/app/FragmentActivity?hl=zh-cn)（[`AppCompatActivity`](https://developer.android.google.cn/reference/androidx/appcompat/app/AppCompatActivity?hl=zh-cn) 的基类）中才能显示。添加方式有两种：

| 方式 | 说明 |
|------|------|
| **静态添加** | 在 Activity 的布局 XML 中直接声明 Fragment，界面固定、不可在运行时切换。 |
| **动态添加** | 布局中只放一个容器，在代码里通过 `FragmentManager` / `FragmentTransaction` 在运行时添加或替换 Fragment，适合底部导航、Tab 等场景。 |

两种方式都需在布局中为 Fragment 预留位置，并**优先使用 [`FragmentContainerView`](https://developer.android.google.cn/reference/androidx/fragment/app/FragmentContainerView?hl=zh-cn)** 作为容器（相比 `FrameLayout` 有 Fragment 相关修复）。


#### 2.2.1 静态添加（XML 声明）

在 Activity 的布局 XML 中用 `FragmentContainerView` 声明要显示的 Fragment 类，界面结构固定，无法在运行时切换。

示例（`res/layout/example_activity.xml`）：

```xml
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragment_container_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:name="com.example.ExampleFragment" />
```

> 注意，不再建议使用`<fragment>` 标记通过 XML 添加 fragment，因为 `<fragment>` 标记允许 fragment 超出其 `FragmentManager` 的状态。而是应始终使用 [`FragmentContainerView`](https://developer.android.google.cn/reference/androidx/fragment/app/FragmentContainerView?hl=zh-cn) 通过 XML 添加 Fragment。



#### 2.2.2 动态添加（代码中添加/替换）

在 Activity 中通过 **FragmentManager** 和 **FragmentTransaction** 在运行时添加或替换 Fragment，适合底部导航、Tab 等需动态切换内容的场景。

**步骤一：在布局中预留容器**

在 Activity 的布局里放置一个容器（推荐 `FragmentContainerView`）：

```xml
<!-- res/layout/example_activity.xml -->
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragment_container_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```



**步骤二：在代码中提交事务**

在 Activity 的 `onCreate` 中获取 **FragmentManager**，通过 **FragmentTransaction** 添加或替换 Fragment。

```java
public class ExampleActivity extends AppCompatActivity {
    public ExampleActivity() {
        super(R.layout.example_activity);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("some_int", 0);

            getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, ExampleFragment.class, bundle)
                .commit();
        }
    }
}
```

> 通过 `savedInstanceState == null` 判断仅在首次创建 Activity 时执行添加，可避免配置变更（如旋转屏幕）后重复添加 Fragment；恢复时由系统从 `savedInstanceState` 恢复。

**常用写法示例（添加 / 替换 + 返回栈）：**


```java
FragmentManager fragmentManager = getSupportFragmentManager();
FragmentTransaction transaction = fragmentManager.beginTransaction();
transaction.replace(R.id.fragment_container, new MyFragment());
transaction.addToBackStack(null);  // 可选：加入返回栈，按返回键可回到上一 Fragment
transaction.commit();
```


## 三、FragmentManager 与进阶用法

Fragment 实例化后处于 **INITIALIZED** 状态，只有加入 **FragmentManager** 后才会经历 CREATED → STARTED → RESUMED 等状态转换。FragmentManager 负责管理 Fragment 的添加、移除、状态与返回栈。本节介绍返回栈用法以及 Fragment 与 Activity 的通信方式。

### 3.1 返回栈

在 `FragmentTransaction` 上调用 **addToBackStack(null)** 可将当前事务加入返回栈。用户按返回键时，会按栈顺序逆向执行事务（例如被 replace 掉的 Fragment 会重新显示），而不是直接退出 Activity。适用于多级界面、需“返回上一页”的场景。

### 3.2 Fragment 与 Activity / Fragment 之间的通信

为保持 Fragment **可复用、独立**，应避免让 Fragment 直接持有对 Activity 或其他 Fragment 的引用。官方推荐两种方式（参见 [与 fragment 通信](https://developer.android.google.cn/guide/fragments/communicate?hl=zh-cn)）：

| 方式 | 适用场景 |
|------|----------|
| **共享 ViewModel** | 需要在多个 Fragment 之间或 Fragment 与宿主 Activity 之间**持续共享**数据（如选中项、筛选条件）。 |
| **Fragment Result API** | 需要传递**一次性结果**（可放入 Bundle），例如列表页 → 详情页 → 返回并带回用户选择的数据。 |

---

#### 3.2.1 使用 ViewModel 共享数据

通过 **ViewModelProvider** 指定“作用域”，使同一作用域内的 Activity 与 Fragment（或多个 Fragment）拿到**同一个 ViewModel 实例**，从而共享数据。

**（1）Fragment 与宿主 Activity 共享**

Activity 与 Fragment 都使用 **Activity 作为作用域**，即可共享同一 ViewModel：

```java
// Activity 中
public class MainActivity extends AppCompatActivity {
    private ItemViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.getSelectedItem().observe(this, item -> { /* 响应选中项 */ });
    }
}

// Fragment 中
public class ListFragment extends Fragment {
    private ItemViewModel viewModel;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        // 与 Activity 拿到的是同一实例，可读写同一份数据
        viewModel.selectItem(someItem);
    }
}
```

**（2）同一 Activity 内多个 Fragment 之间共享**

两个 Fragment 都使用 `requireActivity()` 作为 ViewModelProvider 的作用域，会得到**同一个 ViewModel 实例**，无需互相引用即可通信（例如列表 Fragment 与筛选 Fragment 共享筛选条件）。

**（3）父 Fragment 与子 Fragment 之间共享**

子 Fragment 使用**父 Fragment** 作为作用域：`new ViewModelProvider(requireParentFragment()).get(SomeViewModel.class)`，则父子共享同一 ViewModel。

> **注意**：作用域必须一致。若 Fragment 用 `this` 作为作用域，得到的是仅该 Fragment 的 ViewModel，与 Activity 中的不是同一实例。

---

#### 3.2.2 使用 Fragment Result API 传递一次性结果

从 Fragment 1.3.0 起，**FragmentManager** 可作为“结果中转”：发送方调用 `setFragmentResult(requestKey, bundle)`，接收方通过 `setFragmentResultListener(requestKey, ...)` 收取，**双方无需直接引用**。数据需能放入 Bundle（如基本类型、String、Parcelable 等）。

**接收方先设置监听（例如 Fragment A 接收）：**

```java
@Override
public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
        String result = bundle.getString("bundleKey");
        // 使用 result
    });
}
```

**发送方在合适时机设置结果（例如 Fragment B 返回时）：**

```java
button.setOnClickListener(v -> {
    Bundle result = new Bundle();
    result.putString("bundleKey", "result");
    getParentFragmentManager().setFragmentResult("requestKey", result);
});
```

接收方进入 **STARTED** 状态后才会收到结果；同一 `requestKey` 只保留一份结果，被消费后会被清除。

**在宿主 Activity 中接收结果**：在 Activity 的 `onCreate` 中对 `getSupportFragmentManager()` 调用 `setFragmentResultListener("requestKey", this, ...)` 即可。

**父 Fragment 接收子 Fragment 的结果**：父 Fragment 在 `getChildFragmentManager()` 上设置 `setFragmentResultListener`；子 Fragment 仍在**自己的** `getParentFragmentManager()` 上调用 `setFragmentResult`（即把结果交给父 Fragment 的 FragmentManager）。









## 参考资料

[Fragment  | App architecture  | Android Developers](https://developer.android.google.cn/guide/fragments?hl=zh-cn)