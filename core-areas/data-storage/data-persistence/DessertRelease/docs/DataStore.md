### DataStore 组件

在不需要存储关系型数据的情况下， [DataStore](https://developer.android.google.cn/topic/libraries/architecture/datastore?hl=zh-cn) 可以提供一种简单的解决方案。Jetpack 中的 DataStore 组件非常适合存储简单的小型数据集，且开销较低。DataStore 有两种不同的实现：`Preferences DataStore` 和 `Proto DataStore`。

- `Preferences DataStore` 存储键值对。这些值可以是 Kotlin 的基本数据类型，例如 `String`、`Boolean` 和 `Integer`。它不存储复杂的数据集，也不需要预定义的架构。`Preferences Datastore` 的主要应用场景是**在用户的设备上存储其偏好设置**。
- `Proto DataStore` 存储自定义数据的类型。它需要一个预定义的架构，用于将 proto 定义映射到对象结构。

相关依赖项为：

```
implementation("androidx.datastore:datastore-preferences:1.0.0")
```

