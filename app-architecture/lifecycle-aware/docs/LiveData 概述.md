## 一、LiveData 简介

### 1.1 什么是 LiveData

在 Activity 中，我们手动获取 ViewModel 中的数据，但是 ViewModel 却无法将数据的变化主动通知给 Activity。如果将 Activity 的实例传给 ViewModel ，这样 ViewModel 却无法将数据的变化主动通知给 Activity。但是，ViewModel 的生命周期长于 Activity，这样会导致Activity 无法释放从而造成内存泄漏。

为了解决这一问题，Android 官方提供了 LiveData 这一解决方案。LiveData 是 Jetpack 提供的一种响应式编程组件,它可以包含任何类型的数据,并**在数据发生变化的时候通知给观察者**。其特点主要有:

- **数据持有与观察**:  LiveData 可以包含任何类型的数据，当数据发生变化时，它会自动通知所有处于活跃状态的观察者。观察者可以是 Activity、Fragment 或其他组件。
- **避免内存泄漏**：LiveData 会自动感知生命周期，当观察者（如 Activity）销毁时会自动移除观察，无需手动清理
- **生命周期安全**：只在观察者处于活跃状态（如 Activity 的 onStart 到 onStop 之间）才发送数据更新，避免不必要的 UI 刷新



### 1.2 LiveData 的基本用法

LiveData特别适合与ViewModel结合在一起使用，虽然它也可以单独用在别的地方，但是在绝大多数情况下，它是使用在ViewModel当中的。

```java
// 在 ViewModel 中定义 LiveData
public class MyViewModel extends ViewModel {
    private MutableLiveData<String> _data = new MutableLiveData<>();
    private LiveData<String> data = _data;
    
    public LiveData<String> getData() {
        return data;
    }
    
    public void updateData(String newData) {
        _data.setValue(newData);
    }
}


// 在 Activity 中观察
public class MyActivity extends AppCompatActivity {
    private MyViewModel viewModel;
    private TextView textView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textView = findViewById(R.id.textView);
        
        // 获取 ViewModel
        viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        
        // 观察 LiveData
        viewModel.getData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String data) {
                // 更新 UI
                textView.setText(data);
            }
        });
    }
}

```

其中，View 只能观察，不能直接修改数据。





在堆内存中，只有一个 `MutableLiveData`对象，`_data` 和 `data` 都是指向这个对象的引用，只是这两个引用的**声明类型**不同。这是Java/Kotlin中**多态**和**接口设计**的典型应用，通过类型系统在编译时提供访问控制。这个访问控制细节如下：

- _data：`MutableLiveData<String>`类型，是可变的（有 `setValue()` 和 `postValue()` 方法）
- data：`LiveData<String>`类型，是只读的（只有观察功能 `observe()` ，没有修改功能）

  ```
  内存中的对象：
        +----------------------+
        | MutableLiveData 实例 |
        | (实际类型)           |
        +----------------------+
           ↑                  ↑
           |                  |
          _data              data
      (MutableLiveData类型)  (LiveData类型)
  ```

  





## 二、LiveData 源码分析

LiveData 通过监听生命周期，确保只在合适时机响应由 `setValue` 发起、经 `mVersion` 标记的数据变化。整体观察流程如下：

```
[1] UI组件调用 observe() 注册观察
    ↓
[2] LiveData 保存观察者引用，并绑定到 Lifecycle
    ↓
[3] ViewModel 通过 setValue() 修改数据
    ↓
[4] LiveData 内部检测到值变化
    ↓
[5] LiveData 检查观察者的生命周期状态
    ↓
[6] 如果观察者处于活跃状态（STARTED/RESUMED），调用 onChanged() 回调
    ↓
[7] UI 在 onChanged() 中更新界面
```

关键重点如下：

- **生命周期感知 (`observe`+ `onStateChanged`)**：负责**建立、维护和销毁“观察关系”本身**，确保只有当UI组件可见时，才允许数据流通过。这是实现“只在活跃状态更新UI”和“自动解除绑定防泄漏”的关键。
- **数据变化响应 (`setValue`+ `considerNotify`)**：负责在**观察关系已建立且“阀门”打开的前提下**，判断数据是否为新，如果是，则**触发**最终的`onChanged`回调。

注意，这两部分都使用到了观察者模式。在生命周期感知中，LiveData 是作为被观察者，从而实现数据监听。在数据变化响应中，LiveData 里面的 `LifecycleBoundObserver`  是作为观察者，用于实现生命周期感知。



### 2.1 生命周期机制

LiveData本身**不具备直接监听生命周期的能力**，它通过**依赖注入**的方式，将观察者包装成`LifecycleBoundObserver`，并注册到`LifecycleOwner`（如Activity/Fragment）的生命周期组件中。当宿主生命周期变化时，LiveData 通过回调机制自动更新观察者的活跃状态或移除观察者。

#### 2.1.1 观察者注册（observe方法）

当调用`liveData.observe(owner, observer)`时，LiveData执行以下关键步骤：

- **安全检查**：检查`owner`是否处于`DESTROYED`状态，若是则直接忽略，防止无效操作。
- **包装观察者**：将用户传入的`observer`和`owner`封装成内部类`LifecycleBoundObserver`。这个包装类实现了`LifecycleEventObserver`接口，使其具备接收生命周期事件的能力。
- **注册监听**：调用`owner.getLifecycle().addObserver(wrapper)`，将包装后的观察者添加到`Lifecycle`的观察者列表中。**这一步是LiveData能够感知生命周期的根本原因**。

```java
@MainThread
public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
    assertMainThread("observe");
    if (owner.getLifecycle().getCurrentState() == DESTROYED) {
        // ignore
        return;
    }
    LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
    ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
    if (existing != null && !existing.isAttachedTo(owner)) {
        throw new IllegalArgumentException("Cannot add the same observer"
                + " with different lifecycles");
    }
    if (existing != null) {
        return;
    }
    owner.getLifecycle().addObserver(wrapper);
}
```



#### 2.1.2 生命周期响应（onStateChanged)

当 Activity/Fragment 的生命周期状态发生变化时，`LifecycleBoundObserver`的`onStateChanged`方法会被回调：

- **销毁处理**：如果当前状态为`DESTROYED`，则自动调用`removeObserver`，**自动解除订阅，防止内存泄漏**。
- **活跃状态判断**：通过`shouldBeActive()`方法判断当前状态是否至少为`STARTED`（即`STARTED`或`RESUMED`）。
- **状态同步**：调用`activeStateChanged()`，根据活跃状态决定是否分发数据。

```java
class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver {
    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        if (mOwner.getLifecycle().getCurrentState() == DESTROYED) {
            removeObserver(mObserver); // 自动清理
            return;
        }
        
        Lifecycle.State prevState = null;
        while (prevState != currentState) {
            prevState = currentState;
            activeStateChanged(shouldBeActive());  // 更新活跃状态
            currentState = mOwner.getLifecycle().getCurrentState();
        }
    }
    
    @Override
    boolean shouldBeActive() {
        return mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
    }
}
```





#### 2.1.3 数据分发控制（activeStateChanged）

- **活跃状态(STARTED /RESUMED)**：当组件变为活跃时，如果LiveData有数据，会立即调用`dispatchingValue`检查观察者是否需要更新（体现粘性特性）。新观察者或重新活跃的观察者，如果版本落后，立即收到最新值。

- **非活跃状态(非STARTED /RESUMED)**：当组件变为非活跃时，停止分发数据，避免在后台更新UI导致崩溃。

```java
private abstract class ObserverWrapper {

		void activeStateChanged(boolean newActive) {
            if (newActive == mActive) {
                return;
            }
            // immediately set active state, so we'd never dispatch anything to inactive
            // owner
            mActive = newActive;
            changeActiveCounter(mActive ? 1 : -1);
            if (mActive) {
                dispatchingValue(this);  // 检查当前观察者是否需要更新 
            }
        }

}

void dispatchingValue(@Nullable ObserverWrapper initiator) {
    if (mDispatchingValue) {
        mDispatchInvalidated = true;
        return;
    }
    mDispatchingValue = true;
    do {
        mDispatchInvalidated = false;
        if (initiator != null) {
            considerNotify(initiator);
            initiator = null;
        } else {
            for (Iterator<Map.Entry<Observer<? super T>, ObserverWrapper>> iterator =
                    mObservers.iteratorWithAdditions(); iterator.hasNext(); ) {
                considerNotify(iterator.next().getValue());
                if (mDispatchInvalidated) {
                    break;
                }
            }
        }
    } while (mDispatchInvalidated);
    mDispatchingValue = false;
}
```



### 2.2 数据变化检测

#### 2.2.1 数据被更新 (setValue/postValue)

ViewModel 内部修改 `MutableLiveData`的值（通过 `setValue()`或 `postValue()`）

```java
protected void setValue(T value) {
    mVersion++; // 关键：增加数据版本号
    mData = value; // 存储新数据
    dispatchingValue(null); // 广播给所有活跃观察者，检查数据是否需要更新
}
```

可以看到，这里使用了**版本号机制**，跟踪每个观察者最后接收的数据版本 `mVersion`。



#### 2.2.2 尝试分发数据 (`dispatchingValue`-> `considerNotify`)

该方法会遍历所有观察者，为每一个观察者调用`considerNotify`。`considerNotify`的逻辑是决定是否通知的最终裁决。当所有判断都通过后，调用观察者的 `onChanged()` 方法来发送通知。

```java
private void considerNotify(ObserverWrapper observer) {
    // 规则1: 观察者自身当前不活跃 -> 不通知
    if (!observer.mActive) {
        return;
    }
    
    // 规则2: 观察者关联的组件当前不应是活跃状态 -> 不通知，并更新其活跃状态
    if (!observer.shouldBeActive()) { // 这里就用到了生命周期感知！
        observer.activeStateChanged(false);
        return;
    }
    
    // 规则3: 这个观察者记录的版本号 >= 当前数据的版本号 -> 不通知
    // (说明上次已经通知过这个值了，数据没有新变化)
    if (observer.mLastVersion >= mVersion) {
        return;
    }
    
    // 通过所有检查，执行通知
    observer.mLastVersion = mVersion;
    observer.mObserver.onChanged((T) mData);
}
```





### 2.3 数据变化检测中的观察者模式

使用了观察者模式，并且是典型的"推"模式（Push Model）

#### 2.3.1 观察者模式要素

观察者模式的三大要素在这里都具备：

| 观察者模式要素               | LiveData 实现                                   |
| ---------------------------- | ----------------------------------------------- |
| **Subject（主题/被观察者）** | `LiveData`对象本身                              |
| **Observer（观察者）**       | 实现了 `Observer`接口的对象（通常是匿名内部类） |
| **订阅/取消订阅**            | `observe()`/ `removeObserver()`                 |
| **通知变化**                 | 内部调用观察者的 `onChanged()`方法              |

```java
// 经典观察者模式结构
public interface Subject {
    void registerObserver(Observer o);  // 对应 LiveData.observe()
    void notifyObservers();             // 对应 LiveData 内部的值变化检查
}

// LiveData 的实现
viewModel.getData().observe(this, new Observer<String>() {  // 被观察者LiveData注册观察者
    @Override
    public void onChanged(String data) {  // 被通知时的回调
        // 处理更新
    }
});
```



#### 2.3.2 使用推模式

- **推模式（Push）**：被观察者**主动推送**新数据给观察者

- **拉模式（Pull）**：观察者**主动拉取**数据，被观察者只通知"有变化"

**LiveData 是明确的推模式**：

```java
// ViewModel 内部
public void updateData(String newData) {
    _data.setValue(newData);  // 设置新值
    // LiveData 内部会自动：
    // 1. 比较新旧值
    // 2. 如果不同，遍历所有观察者
    // 3. 对每个观察者调用：observer.onChanged(newData)
    // ↑↑↑ 这是关键：直接把新数据推送给观察者
}

// 观察者接收的是完整数据
public void onChanged(String newData) {  // 接收推送的完整数据
    textView.setText(newData);  // 直接使用，无需再查询
}
```





## 参考资料

[Android开源框架面试题：谈谈LiveData的生命周期是怎么监听的?](https://mp.weixin.qq.com/s?__biz=Mzg3ODY2MzU2MQ==&mid=2247490521&idx=5&sn=6a1fc75afcb22482fdd8a33a69128a9a&chksm=cf11193ff86690299a0d4b9e2982341f589e902b4dd81ed3847c0bb795508d4ddb345f7f451f&token=1436311520&lang=zh_CN#rd)
