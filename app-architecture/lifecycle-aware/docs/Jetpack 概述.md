### 什么是 Jetpack

[**Android Jetpack**](https://developer.android.google.cn/jetpack) 是 Google 官方推出的一套组件库、工具和指南的集合，旨在帮助开发者更轻松地编写高质量、健壮的 Android 应用。它遵循现代 Android 开发的最佳实践，提供向后兼容性，并减少样板代码。

主要特点有：

- Jetpack 采用 **MVVM（Model-View-ViewModel）** 架构模式，通过生命周期感知组件避免内存泄漏，并支持 Kotlin 协程和 Flow 进行异步编程。

- 大部分不依赖于任何 Android 系统版本，都定义在 AndroidX 库中，拥有非常好的向下兼容性。



### Jetpack 的组成部分

Jetpack 组件按功能场景分为：

- **架构组件(Architecture)**:  帮助构建健壮的应用架构，包括ViewModel、LiveData、Room等。
- **基础组件(Foundation)**：提供向后兼容性、测试和 Kotlin 语言支持，包括 AppCompat、Android KTX、Test 等。

- **行为组件(Behavior)**: 集成标准 Android 服务，包括 Notifications、Permissions、CameraX、DownloadManager、Media & playback等。
- **界面组件(UI)**:  提供小部件和辅助类，帮助构建美观的界面，包括 Material Design 、Animation & Transitions、Fragment、Layout 等。

这些类别共同构成了现代Android开发的完整生态体系，每个类别都包含针对特定开发需求的组件库。所有组件都打包在AndroidX命名空间下，确保与Android系统的解耦和向后兼容性。