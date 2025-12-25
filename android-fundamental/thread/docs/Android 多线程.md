## 一、Android 多线程概述

### 1.1 什么是 Android 多线程

Android 多线程是指在 Android 应用程序中同时执行多个线程的技术。

- **主线程(UI 线程)**: 每个 Android 应用启动时都会创建一个主线程，负责处理用户界面（UI）的更新和事件响应。如果主线程被阻塞超过5秒，系统会抛出 ANR（Application Not Responding）错误。
- **工作线程**： 用于执行耗时操作（网络请求、数据库操作、文件读写等），不能在非主线程中直接更新 UI。





### 1.2 Android 多线程和 Java 多线程的区别

Java 多线程是**通用性的基础**，关注于并发计算和资源利用；而 Android 多线程是在此基础上，为满足**移动端UI响应的苛刻要求**而特化的一个分支，它引入了严格的线程规则和专用的通信机制。理解 **UI 线程模型**和 **Handler 机制**是掌握 Android 多线程编程的关键。

主要区别如下：

| 特性               | Java 多线程                                                  | Android 多线程                                               |
| :----------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **核心目标**       | 最大化利用 CPU 资源，提升计算效率                            | 保证 UI 线程（主线程）的流畅响应，避免应用无响应（ANR）      |
| **线程类型与职责** | 线程角色平等，通常无严格的主从之分                           | 严格区分 **UI 线程（主线程）** 和**工作线程（子线程）**      |
| **UI 操作规则**    | 无特殊限制，任何线程通常都可更新 UI（如 Swing/JavaFX 有特定规则，但不同于 Android） | **黄金法则：只能在主线程中更新 UI**。工作线程更新 UI 会抛出 `CalledFromWrongThreadException` |
| **核心编程工具**   | `Thread`, `Runnable`, `synchronized`, `Lock`, `ExecutorService` 等 Java 并发包（`java.util.concurrent`） | `Handler`, `Looper`, `MessageQueue`, **`AsyncTask`** (已废弃，但体现了设计思想), `IntentService`, **`ViewModel`** 配合 **协程 (Coroutines)** 或 **`LiveData`** (现代推荐) |
| **线程间通信机制** | 主要通过共享内存、锁机制、`wait()`/`notify()` 等实现线程同步 | 基于**消息循环 (Message Loop)** 机制。工作线程通过向主线程的 `MessageQueue` 发送 `Message`，由主线程的 `Handler` 处理并更新 UI |
| **性能与资源管理** | 关注点在线程生命周期开销、锁竞争、CPU 资源利用率             | 除 Java 层面的考量外，更强调**严格控制主线程负载**，并深度整合**线程池**进行资源复用 |







### 1.3 注意事项

- **ANR 约束**： Android 系统要求应用必须在 5秒内响应输入事件，否则会触发 Application Not Responding (ANR) 错误。这是 Android 多线程设计最根本的驱动力，迫使开发者必须将任何可能耗时的操作（网络请求、复杂计算、数据库查询）移至工作线程。
- **UI 线程不安全**：Android 的 UI 控件不是线程安全的。为了简化编程模型并避免复杂的同步逻辑，Android 强制要求所有对 UI 的修改都必须在创建它的主线程中进行。这催生了 `Handler`等通信机制完美地解决了在子线程中进行 UI 更新的问题。





## 二、异步消息处理机制

### 2.1 核心组件

#### 2.1.1 Message

Message 是消息的载体，包含 what、arg1、arg2、obj 等字段。

> 建议使用 `Message.obtain()`从消息池获取，避免频繁创建对象

```java
public final class Message {
    Handler target;     // 目标 Handler
    Runnable callback;  // 回调
    int what;           // 消息标识
    Object obj;         // 数据
    // ...
}
```



#### 2.1.2 Handler

Handler 是消息的发送者和处理者，与特定线程的 MessageQueue 和 Looper 关联。

Handler 可以发送消息 (`sendMessage()`) 或者可执行任务（`post(Runnable)`），并处理消息（`handleMessage()`）。

```java
// Handler 基本用法
Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
        // 在主线程处理消息
        switch (msg.what) {
            case 1:
                textView.setText((String) msg.obj);
                break;
        }
    }
};
```





#### 2.1.3 MessageQueue

MessageQueue 是消息的存储队列（单链表实现），按时间顺序排列消息。

每个线程只有一个 MessageQueue。





#### 2.1.4 Looper

Looper 是消息循环的核心，不断从 MessageQueue 中取出消息，分发给对应的 Handler 进行处理。 

主线程默认有 Looper，子线程需要手动创建。每个线程中只会有一个 Looper 对象。

```java
// 在子线程创建 Looper
class WorkerThread extends Thread {
    public Handler handler;
    
    @Override
    public void run() {
        Looper.prepare();  // 初始化 Looper
        
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 处理消息
            }
        };
        
        Looper.loop();  // 开始消息循环
    }
}
```





### 2.2 工作流程

引用 《第一行代码 Android》中的图片。

![image-20251224161338308](images/image-20251224161338308.png)



Looper 是怎么将消息发送给指定的 Handler 的？Looper 并不是将消息"发送"给指定的 Handler，而是 Handler 从消息队列中"取出"属于自己的消息来处理。每个 **Message 对象内部都保存了 target 字段**，这个 target 就是**发送该消息的 Handler 的引用**。

> 注意，一般发送消息的Handler和处理消息的Handler是同一个对象

```java
// Message.java 源码片段
public final class Message {
    Handler target;      // 关键！消息知道该由哪个Handler处理
    Runnable callback;   // 或者是一个可执行的Runnable
    Message next;        // 链表下一个消息
    // ... 其他字段
}
```



一个线程可以有多个 Handler，但只有一个 Looper。

```java
// 线程只有一个 Looper
Looper.prepare();  // 每个线程只能调用一次

// 创建多个 Handler，都绑定到当前线程的 Looper
Handler handler1 = new Handler(Looper.myLooper()) {
    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 1) {
            Log.d("Handler1", "Handler1 收到消息: " + msg.obj);
        }
    }
};

Handler handler2 = new Handler(Looper.myLooper()) {
    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 2) {
            Log.d("Handler2", "Handler2 收到消息: " + msg.obj);
        }
    }
};

Handler handler3 = new Handler(Looper.myLooper());

Looper.loop();  // 开始消息循环
```





Handler 并不直接与线程绑定，而是与线程的 Looper 对象绑定。每个线程只能有一个 Looper 实例。绑定方式为：

```java
workerHandler = new Handler(workerThread.getLooper());
```









发送的 runnable 会在 Handler 类里面的 handleMessage 函数里面执行吗？不会，handleMessage 专门处理 Message 对象。Runnable 通过 Handler.post() 直接执行，不经过 handleMessage





如果一个线程里面有多个 Handler，哪 Looper 里面的消息会被哪个 Handler 所获取 ？ 在 Android 中，**一个线程中的多个 Handler 共享同一个 Looper，但每个 Handler 只处理发送给自己的消息**。

```
// 当你调用 handler.sendMessage(msg) 时：
public boolean sendMessage(Message msg) {
    // 关键：设置消息的 target 为当前 Handler
    msg.target = this;
    // 将消息放入共享的消息队列
    return queue.enqueueMessage(msg, uptimeMillis);
}

// Looper 从队列取出消息后：
Message msg = queue.next();  // 获取下一条消息
if (msg != null) {
    // 调用消息中 target 指向的 Handler
    msg.target.dispatchMessage(msg);
}
```





### 2.3 消息发送方式

Message、Runnable、延时消息



## 三、最佳实践

Handler 容易引起内存泄漏。



虽然 Handler 机制仍在使用，但现代 Android 开发更推荐：

- Kotlin 协程
- **LiveData + ViewModel**

















