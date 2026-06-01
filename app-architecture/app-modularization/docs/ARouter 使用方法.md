具体细节查看 [GitHub - alibaba/ARouter: 💪 A framework for assisting in the renovation of Android componentization (帮助 Android App 进行组件化改造的路由框架) · GitHub](https://github.com/alibaba/ARouter)，注意这个项目已经没有人维护了，并且不与 androidx 兼容。



#### 为什么跨模块不能使用 Navigation？

跨模块不适用 Navigation 的核心原因在于 **编译时依赖和工程结构** 的限制。

- **硬编码的类引用依赖（致命问题）**: Navigation 的 Graph 文件必须直接引用 Fragment/Activity 的完整类名。在模块化项目中，`商品模块`的 Fragment 对 `首页模块`是不可见的（没有依赖关系），编译时会直接报错：`Cannot resolve class`。
- **无法独立编译和测试**

| 维度           | **ARouter（跨模块）**    | **Navigation（模块内）**        |
| -------------- | ------------------------ | ------------------------------- |
| **设计目标**   | 解耦，模块间通信         | 单模块内导航管理                |
| **依赖关系**   | 编译时无依赖，运行时发现 | 编译时必须引用目标类            |
| **类型安全**   | 弱（字符串路径）         | 强（Safe Args + 类引用）        |
| **独立编译**   | 完全支持                 | 不支持（如果跨模块引用）        |
| **可视化编辑** | 无                       | 有（Android Studio Nav Editor） |
| **适用场景**   | 架构级，连接业务模块     | 实现级，管理界面流程            |