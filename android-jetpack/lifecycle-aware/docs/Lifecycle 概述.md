## 一、Lifecycle 概述

### 1.1 为什么引入 Lifecycle 

在有些场景下，我们需要感知某些组件（如 activity 和 fragment）的生命周期状态的变化。一种常见的模式是在 activity 和 fragment 的生命周期方法中手动调用依赖组件的操作。假设我们有一个在屏幕上显示设备位置信息的 activity，常见的实现可能如下所示：

```java
class MyLocationListener {
    public MyLocationListener(Context context, Callback callback) {
        // ...
    }

    void start() {
        // connect to system location service
    }

    void stop() {
        // disconnect from system location service
    }
}

class MyActivity extends AppCompatActivity {
    private MyLocationListener myLocationListener;

    @Override
    public void onCreate(...) {
        myLocationListener = new MyLocationListener(this, (location) -> {
            // update UI
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        myLocationListener.start();
        // manage other components that need to respond
        // to the activity lifecycle
    }

    @Override
    public void onStop() {
        super.onStop();
        myLocationListener.stop();
        // manage other components that need to respond
        // to the activity lifecycle
    }
}
```

这种实现方式存在如下问题：

- **代码维护困难**：在真实的应用中，为了响应生命周期的当前状态，将会进行过多的调用来管理界面和其他组件。管理多个组件会在生命周期方法（如 `onStart()` 和 `onStop()`）中包含大量代码，这使得它们难以维护。
- **生命周期管理不可靠**：无法保证组件会在 activity 或 fragment 停止之前启动。在我们需要执行长时间运行的操作（如 [`onStart()`](https://developer.android.google.cn/reference/android/app/Activity?hl=zh-cn#onStart()) 中的某种配置检查）时尤其如此。这可能会导致出现一种竞态条件，在这种条件下，`onStop()` 方法会在 `onStart()` 之前结束，这使得组件留存的时间比所需的时间要长。

为了解决这一问题，Android Jetpack 推出了生命周期感知型组件，将依赖组件的代码从生命周期方法移入组件本身内。



### 1.2 什么是 Lifecycle 

[`Lifecycle`](https://developer.android.google.cn/reference/androidx/lifecycle/Lifecycle) 是 Android Jetpack 生命周期感知型组件的核心，用于<font color="blue">**存储有关组件（如 activity 或 fragment）的生命周期状态的信息，并允许其他对象观测此状态**</font>。通过观察者模式，Lifecycle 将生命周期事件从宿主组件（Activity/Fragment）中解耦出来，让业务组件能够自动响应生命周期变化，避免手动管理生命周期带来的代码臃肿和维护困难。

以上一节案例，使用 Lifecycle 优化代码：

```java
// 1. 实现 DefaultLifecycleObserver
class MyLocationListener implements DefaultLifecycleObserver {
    public MyLocationListener(Context context, Callback callback) {
        // ...
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        // connect to system location service
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        // disconnect from system location service
    }
}

// 2. Activity 实现（AppCompatActivity 实现了 LifecycleOwner 接口 ）
class MyActivity extends AppCompatActivity {
    private MyLocationListener myLocationListener;

    @Override
    public void onCreate(...) {
        super.onCreate(...);
        myLocationListener = new MyLocationListener(this, (location) -> {
            // update UI
        });
        
        // 注册 Lifecycle 观察者
        getLifecycle().addObserver(myLocationListener);
    }
    
    // 不再需要手动调用 start()/stop() 方法
    // 系统会自动调用观察者的 onStart()/onStop() 方法
}
```







## 二、Lifecycle 基本工作原理

### 2.1 核心概念

#### 2.1.1 核心类与接口

| 类/接口                                                      | 作用             | 说明                                                     |
| ------------------------------------------------------------ | ---------------- | -------------------------------------------------------- |
| **[`LifecycleOwner`](https://developer.android.google.cn/reference/androidx/lifecycle/LifecycleOwner?hl=zh-cn)** | 生命周期持有者   | Activity/Fragment 实现的接口，提供 `getLifecycle()` 方法 |
| **Lifecycle**                                                | 生命周期对象     | 存储组件生命周期状态，管理观察者                         |
| [`DefaultLifecycleObserver`](https://developer.android.google.cn/reference/androidx/lifecycle/DefaultLifecycleObserver?hl=zh-cn) | 生命周期观察者   | 观察生命周期变化的组件                                   |
| **LifecycleRegistry**                                        | Lifecycle 实现类 | 管理生命周期状态和事件分发                               |



#### 2.1.2 生命周期状态和事件

Lifecycle 使用两个枚举类 **Event** 和 **State** 来跟踪其关联组件的生命周期状态。

[State](https://developer.android.google.cn/reference/androidx/lifecycle/Lifecycle.State) 是指  [`Lifecycle`](https://developer.android.google.cn/reference/androidx/lifecycle/Lifecycle?hl=zh-cn) 对象所跟踪的组件的当前状态。

- **INITIALIZED**：组件已构造但未调用 onCreate
- **CREATED**：已调用 onCreate，但不可见
- **STARTED**：已调用 onStart，可见但不可交互
- **RESUMED**：已调用 onResume，可交互
- **DESTROYED**：已销毁



[Event](https://developer.android.google.cn/reference/androidx/lifecycle/Lifecycle.Event) 是从框架和 [`Lifecycle`](https://developer.android.google.cn/reference/androidx/lifecycle/Lifecycle?hl=zh-cn) 类分派的生命周期事件。这些事件映射到 activity 和 fragment 中的回调事件。

- **ON_CREATE**：对应 onCreate 方法
- **ON_START**：对应 onStart 方法
- **ON_RESUME**：对应 onResume 方法
- **ON_PAUSE**：对应 onPause 方法
- **ON_STOP**：对应 onStop 方法
- **ON_DESTROY**：对应 onDestroy 方法
- **ON_ANY**：匹配任何事件



状态可视作节点，事件可视作节点之间的边。官方 [*Android activity 生命周期的状态和事件*](https://developer.android.google.cn/topic/libraries/architecture/lifecycle?hl=zh-cn#lc)  图展示了这种关系：

<img src="images/lifecycle-states.svg" alt="*Android activity 生命周期的状态和事件*" style="zoom: 25%;" />







### 2.2 Lifecycle的提供和观测

`DefaultLifecycleObserver` 和 `LifecycleOwner` 共同构成了 Android Jetpack 生命周期感知架构的核心：

- `LifecycleOwner`：生命周期事件(Lifecycle)的提供者，Activity/Fragment 默认实现
- `DefaultLifecycleObserver`：生命周期事件(Lifecycle)的消费者，业务组件实现

实现 [`DefaultLifecycleObserver`](https://developer.android.google.cn/reference/androidx/lifecycle/DefaultLifecycleObserver?hl=zh-cn) 的组件可与实现 [`LifecycleOwner`](https://developer.android.google.cn/reference/androidx/lifecycle/LifecycleOwner?hl=zh-cn) 的组件完美配合，因为所有者可以提供生命周期，而观测者可以注册以观测生命周期。



#### 2.2.1 LifecycleOwner 接口

[`LifecycleOwner`](https://developer.android.google.cn/reference/androidx/lifecycle/LifecycleOwner?hl=zh-cn) 是只包含一个方法的接口，指明类具有 [`Lifecycle`](https://developer.android.google.cn/reference/androidx/lifecycle/Lifecycle?hl=zh-cn)。它包含一个方法（即 [`getLifecycle()`](https://developer.android.google.cn/reference/androidx/lifecycle/LifecycleOwner?hl=zh-cn#getLifecycle())），该方法必须由类实现。

> 如果您要尝试管理整个应用进程的生命周期，请参阅 [`ProcessLifecycleOwner`](https://developer.android.google.cn/reference/androidx/lifecycle/ProcessLifecycleOwner?hl=zh-cn)。

```java
public interface LifecycleOwner {
    
    @NonNull
    Lifecycle getLifecycle();
}
```

此接口从各个类（如 `Fragment` 和 `AppCompatActivity`）抽象化 [`Lifecycle`](https://developer.android.google.cn/reference/androidx/lifecycle/Lifecycle?hl=zh-cn) 的所有权，并允许编写与这些类搭配使用的组件。任何自定义应用类均可实现 [`LifecycleOwner`](https://developer.android.google.cn/reference/androidx/lifecycle/LifecycleOwner?hl=zh-cn) 接口。Android 框架提供了多个内置的 `LifecycleOwner`实现：

```java
// 1. ComponentActivity（AppCompatActivity 的基类）
public class ComponentActivity extends androidx.core.app.ComponentActivity 
    implements LifecycleOwner, ViewModelStoreOwner, ... {
    // 自动实现 LifecycleOwner
}

// 2. Fragment
public class Fragment implements LifecycleOwner, ... {
    // 自动实现 LifecycleOwner
}

// 3. ProcessLifecycleOwner（应用进程生命周期）
// 用于监听整个应用进程的生命周期
```





#### 2.2.1 DefaultLifecycleObserver

[`DefaultLifecycleObserver`](https://developer.android.google.cn/reference/androidx/lifecycle/DefaultLifecycleObserver?hl=zh-cn) 是生命周期事件的接收者和处理者。

类可以通过实现 [`DefaultLifecycleObserver`](https://developer.android.google.cn/reference/androidx/lifecycle/DefaultLifecycleObserver?hl=zh-cn) 并替换相应的方法（如 `onCreate` 和 `onStart` 等）来监控组件的生命周期状态。然后，您可以通过调用 [`Lifecycle`](https://developer.android.google.cn/reference/androidx/lifecycle/Lifecycle?hl=zh-cn) 类的 [`addObserver()`](https://developer.android.google.cn/reference/androidx/lifecycle/Lifecycle?hl=zh-cn#addObserver(androidx.lifecycle.LifecycleObserver)) 方法并传递观测者的实例来添加观测者，如下例所示：

```
public class MyObserver implements DefaultLifecycleObserver {
    @Override
    public void onResume(LifecycleOwner owner) {
        connect()
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        disconnect()
    }
}

myLifecycleOwner.getLifecycle().addObserver(new MyObserver());
```

在上面的示例中，`myLifecycleOwner` 对象实现了 [`LifecycleOwner`](https://developer.android.google.cn/reference/androidx/lifecycle/LifecycleOwner?hl=zh-cn) 接口.





### 2.3 观察者模式

#### 2.3.1 类图

| 观察者模式概念                 | Lifecycle 对应组件                              |
| ------------------------------ | ----------------------------------------------- |
| Subject（主题）                | `LifecycleRegistry`                             |
| ConcreteSubject（具体主题）    | Activity/Fragment 中的 `LifecycleRegistry` 实例 |
| Observer（观察者）             | `LifecycleObserver`                             |
| ConcreteObserver（具体观察者） | `DefaultLifecycleObserver` 实现类               |

```
┌─────────────────────────────────────────────────────┐
│             观察者模式在 Lifecycle 中的实现           │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌─────────────────┐   注册/注销    ┌─────────────┐ │
│  │ LifecycleOwner  │◄───────────────►│  Observer   │ │
│  │  (被观察者)      │   事件通知      │  (观察者)    │ │
│  └─────────────────┘                └─────────────┘ │
│           │                               │          │
│           │ 持有                           │ 实现      │
│           ▼                               ▼          │
│  ┌─────────────────┐          ┌─────────────────────┐│
│  │   Lifecycle     │          │DefaultLifecycleObserver│
│  └─────────────────┘          └─────────────────────┘│
│           │                                         │
│           │ 包含                                     │
│           ▼                                         │
│  ┌─────────────────┐                                │
│  │ LifecycleRegistry│                                │
│  └─────────────────┘                                │
│                                                     │
└─────────────────────────────────────────────────────┘
```









#### 2.3.2 LifecycleRegistry 内部机制

LifecycleRegistry是 Lifecycle接口的核心实现，管理着整个观察者模式：

```java
// LifecycleRegistry 的简化实现逻辑
public class LifecycleRegistry extends Lifecycle {
    // 存储所有观察者
    private FastSafeIterableMap<LifecycleObserver, ObserverWithState> mObserverMap =
        new FastSafeIterableMap<>();
    
    // 当前状态
    private State mState = INITIALIZED;
    
    // 添加观察者
    @Override
    public void addObserver(@NonNull LifecycleObserver observer) {
        // 创建包装类
        ObserverWithState statefulObserver = new ObserverWithState(observer, mState);
        
        // 放入观察者映射
        ObserverWithState previous = mObserverMap.putIfAbsent(observer, statefulObserver);
        
        if (previous != null) {
            return; // 已存在
        }
        
        // 同步到当前状态
        statefulObserver.sync();
    }
    
    // 处理生命周期事件
    public void handleLifecycleEvent(@NonNull Lifecycle.Event event) {
        // 1. 计算新状态
        State next = getStateAfter(event);
        
        // 2. 更新当前状态
        mState = next;
        
        // 3. 【关键】通知所有观察者
        sync();
    }
    
    // 同步状态到所有观察者
    private void sync() {
        while (!isSynced()) {
            // 向前或向后同步状态
            moveToState(mState);
        }
    }
    
    // 状态变更时的回调
    private void moveToState(State next) {
        for (Map.Entry<LifecycleObserver, ObserverWithState> entry : mObserverMap) {
            entry.getValue().dispatchEvent(this, next);
        }
    }
    
    // 移除观察者
    @Override
    public void removeObserver(@NonNull LifecycleObserver observer) {
        mObserverMap.remove(observer);
    }
}
```



#### 2.3.3 状态转换的完整流程

**注册阶段**：观察者通过 `addObserver()`注册到 LifecycleOwner

**事件触发**：LifecycleOwner 的生命周期方法被调用，触发对应 Event

**状态计算**：LifecycleRegistry 根据 Event 计算新的 State

**通知分发**：LifecycleRegistry 遍历所有观察者，调用对应方法

**状态同步**：新注册的观察者会被立即同步到当前状态

**自动清理**：在 DESTROYED 状态时，系统会自动处理资源





## 三、最佳实践

相关信息查看 [使用生命周期感知型组件处理生命周期  | App architecture  | Android Developers](https://developer.android.google.cn/topic/libraries/architecture/lifecycle?hl=zh-cn#lc-bp)





## 四、其他

生命周期变化时，是通知观察者，还是先执行自己的生命周期函数？ 在 `super.onXXX()`中先通知观察者，然后才执行 Activity 自己的生命周期函数逻辑。



Activity 和 Lifecycle 的生命周期状态有区别吗？**有区别，** Lifecycle 的状态是对 Activity 状态的**抽象、简化、标准化**，提供了更清晰、更一致的状态管理。





## 参考资料

[使用生命周期感知型组件处理生命周期  | App architecture  | Android Developers](https://developer.android.google.cn/topic/libraries/architecture/lifecycle?hl=zh-cn)

[Lifecycle  | API reference  | Android Developers](https://developer.android.google.cn/reference/androidx/lifecycle/Lifecycle)



