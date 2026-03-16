### 项目概述

本案例演示了 Android 中使用 **Glide** 加载网络图片的基础用法，重点展示如何通过 **OkHttp** 作为底层网络库、如何启用 **Generated API（GlideApp）**，以及如何使用 **@GlideOption** 和 **@GlideType** 扩展自定义加载选项。界面以单个列表页展示多张网络图片。

### 项目结构

```
glide-basic-java-view/
├── app/src/main/java/com/example/glide/basic/
│   ├── MainActivity.java              # 主 Activity，组装列表数据并绑定 RecyclerView
│   ├── MyAppGlideModule.java          # AppGlideModule，启用 Generated API
│   ├── MyAppExtension.java            # @GlideExtension，定义 listThumb / asBitmapWithDefaults
│   ├── ImageItem.java                # 列表项数据模型（URL + 标题）
│   └── ImageListAdapter.java         # RecyclerView 适配器，使用 GlideApp 加载图片
├── app/src/main/res/
│   ├── layout/
│   │   ├── activity_main.xml         # 主界面布局（Toolbar + RecyclerView）
│   │   └── item_image.xml            # 列表项布局（图片 + 标题）
│   ├── drawable/
│   │   ├── placeholder_image.xml     # 加载中占位图
│   │   └── placeholder_error.xml     # 加载失败占位图
│   └── values/
│       ├── strings.xml               # 字符串资源
│       ├── colors.xml                # 颜色资源
│       └── themes.xml                # 主题样式
├── app/build.gradle.kts              # 应用模块依赖（Glide、OkHttp 集成、compiler）
├── app/proguard-rules.pro            # ProGuard 规则（Glide 相关 keep）
└── gradle/libs.versions.toml         # 版本与依赖声明
```

### 学习目标

通过该项目，你将掌握：

- **Glide 基础用法**：使用 `GlideApp.with().load().into()` 加载网络图片，占位图与错误图配置
- **OkHttp 集成**：依赖 `okhttp3-integration`，使 Glide 底层网络请求由 OkHttp 执行，便于与现有网络层统一
- **Generated API**：通过 `AppGlideModule` + 注解处理器生成 `GlideApp`，链式调用中直接使用选项方法
- **@GlideOption 扩展**：在 `MyAppExtension` 中定义 `listThumb()` / `listThumb(size)`，将常用选项（centerCrop、override、占位/错误图）打包成可复用方法
- **@GlideType 扩展**：定义 `asBitmapWithDefaults()`，为「按 Bitmap 加载」统一默认占位与过渡动画
- **列表场景**：在 RecyclerView 的 Adapter 中绑定生命周期加载图片，避免内存泄漏并优化列表滑动体验
