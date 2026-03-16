## 一、Glide 简介

### **1.1 什么是 Glide**

**Glide** 是一款功能强大、灵活且高效的**Android图片加载和缓存库**。它被谷歌官方推荐，是目前 Android 开发中最流行的图片加载库之一。

其核心工作是用一个简单的链式调用，从任何地方（网络、本地、资源等）加载图片，处理好它（如下采样、缓存、转换等），然后显示到指定的 `ImageView` 或其他目标上。

```java
Glide.with(context)  	// 绑定生命周期
      .load(imageUrl)  // 加载源（URL、文件路径、资源ID等）
      .into(imageView); // 显示目标
```
> 本文主要研究 Glide v4 的使用方法。


### 1.2 核心特性与优势

- **简单易用**：流式 API 简洁直观，多数场景下一行链式调用即可完成加载。
- [**可替换网络栈**](https://bumptech.github.io/glide/int/about.html)：默认使用基于 `HttpUrlConnection` 的实现，也提供与 Volley、OkHttp 等集成的库，可按需接入自己的网络层。
- **支持 GIF 与视频帧**：可直接加载 GIF 动图，或从本地视频取某一帧作为缩略图。
- **列表表现好**：在 `ListView`、`RecyclerView` 中快速滑动时，会取消不可见项请求并优先加载当前可见项，滚动更顺畅。
- **图片处理丰富**：内置圆形裁剪、圆角、高斯模糊等，也支持自定义变换。

性能方面的优化在下一节单独说明。

### 1.3 性能优化

Glide 充分考虑了Android图片加载性能的两个关键方面：

- 图片解码速度
- 解码图片带来的资源压力

为了让用户拥有良好的App使用体验，图片不仅要快速加载，而且还不能因为过多的主线程I/O或频繁的垃圾回收导致页面的闪烁和抖动现象。

Glide使用了多个步骤来确保在Android上加载图片尽可能的快速和平滑：

- 图片处理：自动、智能地下采样(`downsampling`)和缓存(`caching`)，以最小化存储开销和解码次数；
- 自动内存和磁盘缓存：积极的资源重用，例如字节数组和Bitmap，以最小化昂贵的垃圾回收和堆碎片影响；
- 生命周期管理：深度的生命周期集成，以确保仅优先处理活跃的Fragment和Activity的请求，并有利于应用在必要时释放资源以避免在后台时被杀掉。







## 二、Glide 的运行机制

Glide的机制可以概括为：**以生命周期感知为前提，通过三级缓存加速访问，利用高度模块化的组件流水线处理从数据源到视图的加载、解码、转换、显示全过程，并在每个环节进行内存和性能的自动优化。**

### 2.1 请求链路

#### 2.1.1 请求链路中的数据类型

Glide 处理一个图片请求的核心流程可以被抽象为一条清晰的转换路径，每一步都由特定的核心组件驱动：

```
Model → Data → Resource → Transcoded Resource
```

其中的状态含义如下所示：

| 状态/数据类型                        | 核心处理组件             | 职责描述                                                     |
| ------------------------------------ | ------------------------ | ------------------------------------------------------------ |
| **Model (模型)**                     |                          | 请求的输入，即用户通过 `load()`方法传入的源标识（如 URL、URI、File）。 |
| **Data (数据)**                      | **`ModelLoader`**        | 将抽象的 `Model`转换为具体的原始数据流（如 `InputStream`, `FileDescriptor`）。 |
| **Resource (资源)**                  | **`ResourceDecoder`**    | 将原始 `Data`解码为可被 Android 系统操作的中间资源（如 `Bitmap`, `Drawable`, `GifDrawable`）。 |
| **Transcoded Resource (转码后资源)** | **`ResourceTranscoder`** | 对解码后的 `Resource`进行最终的封装类型转换，以确保其与 `Target`的要求完全匹配（如将 `Bitmap`包装为 `BitmapDrawable`）。 |

Glide 内部会为每个请求智能地尝试所有可能的组件组合来完成这条路径，直至成功或全部失败。



#### 2.1.2 核心组件

- **[`ModelLoaders`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/load/model/ModelLoader.html)**：负责从各种“模型”（如URL、URI、自定义对象）中加载“数据”（如`InputStream`、`FileDescriptor`）。

- **[`ResourceDecoders`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/load/ResourceDecoder.html)**：负责将“数据”解码成“资源”（如`Bitmap`、`Drawable`）。支持各种图片格式和数据流。

- **[`Encoder`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/load/Encoder.html) / [`ResourceEncoder`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/load/ResourceEncoder.html)**：负责将原始数据或处理后的 Resource 数据写入磁盘缓存。

- **Transformation**：负责对资源进行转换（如裁剪、圆角、滤镜）。

- **Target**：图片加载完成后的回调目标，通常是`ImageView`，也可以是自定义对象。





### 2.2 请求与生命周期管理

Glide通过 Glide.with(Activity/Fragment/Context)与Android组件的生命周期深度绑定。当绑定的 Activity 或 Fragment 销毁时，Glide 会自动取消该组件下所有进行中的图片加载请求，并清理相关资源。这是防止内存泄漏的最重要机制。



### 2.3 三级缓存机制

Glide通过三层缓存来最大化性能和减少网络请求。具体细节查看 [Android图片加载篇： Glide 缓存机制深度优化指南通过深度定制Glide缓存机制，可显著提升图片加载性能并降低 - 掘金](https://juejin.cn/post/7482769108524023847)

#### 2.3.1 缓存类型

**(1) 活动资源缓存 (Active Resources)**

- **机制**：使用弱引用 `HashMap<Key, WeakReference<EngineResource>>` 存储**当前正被引用或显示**的图片资源，确保正在使用的图片不会被移出内存。同时，使用封装在 `EngineResource`对象内部的引用计数器来判断图片在活动资源缓存和内存之间的迁移时机。
- **作用**：避免同一图片在同一时刻被多次解码，并保证 UI 的快速响应。

**(2) 内存缓存 (Memory Cache)**

- **机制**：存储最近被加载过、**当前未被任何 Target 使用**的图片资源。默认使用 `LruResourceCache`（LRU 算法），内存不足时自动移除最近最少使用的条目。大小可根据 `MemorySizeCalculator` 自动计算或手动配置。
- **作用**：在内存中快速复用图片，是列表快速滑动的关键。

**(3) 磁盘缓存 (Disk Cache)**

- **机制**：将原始数据和/或解码、转换后的资源持久化到磁盘。默认使用 `DiskLruCacheWrapper`（LRU 算法）。位置和大小可配置（如 `InternalCacheDiskCacheFactory`、`ExternalCacheDiskCacheFactory` 等）。
- **作用**：避免重复的网络请求和重复解码/转换，节省流量和电量。
- **磁盘缓存策略**：
  
  - `DiskCacheStrategy.NONE`：不使用磁盘缓存
  - `DiskCacheStrategy.DATA`：只缓存原始数据（解码前的数据）
  - `DiskCacheStrategy.RESOURCE`：只缓存解码并转换后的资源
  - `DiskCacheStrategy.ALL`：远程数据同时缓存 DATA 与 RESOURCE，本地数据仅缓存 RESOURCE
  - `DiskCacheStrategy.AUTOMATIC`：默认策略，根据数据源自动选择



#### 2.3.2 缓存策略

发起请求时，Glide会按 **活动资源 -> 内存缓存 -> 磁盘缓存 -> 原始源（网络/文件等）** 的顺序查找资源。找到后，资源会逆向填充各级缓存。



#### 2.3.3 缓存清理

Glide 提供了手动清除缓存的方法。注意：`clearMemory()` 必须在**主线程**调用，`clearDiskCache()` 必须在**子线程**调用（内部有同步 I/O）。

```java
// 清除内存缓存（须在主线程调用）
Glide.get(this).clearMemory();

// 清除磁盘缓存（须在子线程调用）
new Thread(() -> Glide.get(this).clearDiskCache()).start();
```





## 三、Glide 加载图片的全过程

### 3.1 绑定与创建请求

Glide 加载图片可以分为下面三个阶段：

- **`with(context)`**：根据传入的 Activity、Fragment 或 Context 获取或创建 `RequestManager`，并与其生命周期绑定。后续请求会在该组件销毁时自动取消并释放资源。
- **`load(model)`**：指定加载源（URL、URI、文件路径、资源 ID 等），构建 `RequestBuilder`。此时尚未发起网络或磁盘读取，只是把“要加载什么”记录下来。
- **`into(target)`**：指定展示目标（如 `ImageView`）或自定义 `Target`，并**真正发起一次加载请求**。

若在任意阶段因生命周期结束或 `into()` 被新请求覆盖，Glide 会取消当前请求并回收资源，因此不会产生多余加载或泄漏。

> 本节的源码解析推荐查看 [一文搞懂Glide，不懂来打我 - 知乎](https://zhuanlan.zhihu.com/p/450544419)



### 3.2 一次请求的完整流程

1. **检查内存缓存**：先查当前进程的活跃资源与 LruCache。命中则直接解码/转码后回调到 Target，不再走网络和磁盘。
2. **检查磁盘缓存**：若内存未命中，根据 `DiskCacheStrategy` 查磁盘（可能存原始数据或转换后的资源）。命中则解码/转码后写入内存缓存并回调。
3. **从数据源加载**：内存和磁盘都未命中时，通过对应的 `ModelLoader` 从网络、本地文件等获取数据（即 **Model → Data**）。
4. **解码与转换**：用 `ResourceDecoder` 将数据解码成资源（**Data → Resource**），再经 `Transformation` 做裁剪、圆角等处理，必要时经 `ResourceTranscoder` 转成目标类型（**Resource → Transcoded Resource**）。
5. **写入缓存与回调**：将结果按策略写入内存缓存和磁盘缓存，最后把最终资源交给 `Target`（如显示到 `ImageView`）。







## 四、Glide 的高级用法

[Glide v4 : 配置](https://muyangmin.github.io/glide-docs-cn/doc/configuration.html#applications)



### 4.1 选项

Glide 里真正的「配置」是两类**选项**：**RequestOptions**（请求选项）和 **TransitionOptions**（过渡选项）。它们不是单独存在的，而是通过链式调用或 `apply()` / `transition()` 挂在一次「请求」上；承载这次请求、并最终执行 `load().into()` 的对象，就是 **RequestBuilder**。所以 RequestBuilder 是请求的骨架，选项是贴在请求上的配置。更多细节见：[Glide v4 : 选项](https://muyangmin.github.io/glide-docs-cn/doc/options.html)。

**(1) 请求相关（RequestOptions / RequestBuilder）**

- **占位符与错误图**：占位符（placeholder）、错误图（error）、以及失败时的后备请求（error RequestBuilder，4.3.0+）。
- **变换（Transformations）**：如 `centerCrop()`、`circleCrop()` 等，控制最终显示形状与缩放。
- **缓存策略（Caching）**：控制内存/磁盘缓存行为。
- **组件相关设置**：如编码质量、Bitmap 解码配置等。

**(2) 过渡（TransitionOptions）**

- 控制加载完成时的表现：View 淡入、与占位符交叉淡入，或不使用过渡（直接替换）。
- 资源类型不同则选项不同，例如 `DrawableTransitionOptions.withCrossFade()`、`BitmapTransitionOptions` 等。

**（3）组件选项（Option）**

[`Option`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/load/Option.html) 类是给Glide的组件添加参数的通用办法，包括 [`ModelLoaders`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/load/model/ModelLoader.html) , [`ResourceDecoders`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/load/ResourceDecoder.html) , [`ResourceEncoders`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/load/ResourceEncoder.html) , [`Encoders`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/load/Encoder.html) 等等。一些Glide的内置组件提供了设置项，自定义的组件也可以添加设置项。



### 4.2 Generated API 

[Generated API](https://muyangmin.github.io/glide-docs-cn/doc/generatedapi.html) 是 Glide v4 引入的一项核心特性，旨在利用 注解处理器 (Annotation Processor)​ 在编译时自动生成代码来简化 API 调用。它允许开发者通过简单的注解配置，自动生成一个功能增强的 GlideApp类，从而替代传统的 Glide.with()调用。其目的主要有：

- 集成库扩展：允许第三方库（如 OkHttp 集成库）在生成的 API 中添加自定义选项。
- 选项打包：允许开发者将常用的配置选项（如占位图、缓存策略）打包成一个自定义方法，避免重复代码。

  ```java
  // 传统写法：创建RequestOptions对象来设置选项，加载时通过apply()方法使用
  RequestOptions options = new RequestOptions()
          .placeholder(R.drawable.placeholder_image) // 占位符
          .error(R.drawable.error_image)            // 错误图
          .centerCrop();                            // 缩放模式（居中裁剪）
  Glide.with(fragment)
       .load(myUrl)
       .apply(options) // 应用上面定义的所有选项
       .into(imageView);
  
  
  // Generated API 写法
  ```

  

注意，Generated API 目前仅限在 Application 模块中使用，以确保全局只有一份 API 定义，避免冲突。




### 4.3 扩展 Generated API

使用 @GlideExtension 注解来自定义扩展 Generated API。支持如下两种扩展：
- [`@GlideOption`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/annotation/GlideOption.html) 扩展选项：将一组经常一起使用的 RequestOptions配置（比如缩略图大小、占位符、错误图）打包成一个新的、简洁的方法。
-  [`@GlideType`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/annotation/GlideType.html) 扩展类型：被 [`@GlideType`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/annotation/GlideType.html) 注解的静态方法用于扩展 [`RequestManager`](https://muyangmin.github.io/glide-docs-cn/javadocs/400/com/bumptech/glide/RequestManager.html) 。被 `@GlideType` 注解的方法允许你添加对新的资源类型的支持，包括指定默认选项。

假设你的应用里到处都要加载一种尺寸为 200x200 的圆形小缩略图，每次都写一遍配置很麻烦。你可以用 `@GlideOption`创建一个 .circleMiniThumb()方法。
 ```java
// 1. 创建扩展类
@GlideExtension
public class MyAppExtension {
    // 私有构造函数，防止被实例化
    private MyAppExtension() {}

    // 2. 定义你的自定义配置方法
    @NonNull
    @GlideOption
    public static BaseRequestOptions<?> circleMiniThumb(BaseRequestOptions<?> options) {
        // 在这里“打包”你的常用配置
        return options
                .circleCrop()           // 圆形裁剪
                .override(200, 200)     // 固定为200x200像素
                .placeholder(R.drawable.ic_default_avatar) // 默认占位图
                .error(R.drawable.ic_error_avatar);       // 错误图
    }
}
 ```
编译项目后，GlideApp就会自动拥有 .circleMiniThumb()这个方法，无需进行额外配置。
 ```java
// 使用你自定义的方法，一行代码就包含了上面定义的所有配置
GlideApp.with(fragment)
        .load(user.getAvatarUrl())
        .circleMiniThumb() // 调用你的“快捷按钮”
        .into(imageViewAvatar);
 ```



## 参考资料

[一文搞懂Glide，不懂来打我 - 知乎](https://zhuanlan.zhihu.com/p/450544419)

[GitHub - bumptech/glide: An image loading and caching library for Android focused on smooth scrolling · GitHub](https://github.com/bumptech/glide?tab=readme-ov-file)

[Glide v4 : 快速高效的Android图片加载库](https://muyangmin.github.io/glide-docs-cn/)