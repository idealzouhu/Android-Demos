# modular-arouter-java-view

基于 **ARouter** 的组件化 Demo，按「应用层 → 业务组件层 → 业务公共层 → 基础层」四层划分，业务场景仿**小红书**（首页信息流、发现、发布、消息、我的、笔记详情）。

## 项目结构（四层架构）

| 层级 | 模块 | 说明 |
|------|------|------|
| **(1) 应用层** | `app` | 壳工程：Application 初始化 ARouter，MainActivity 底部 5 Tab，通过 ARouter 跳转各业务 |
| **(2) 业务组件层** | `feature-home` | 首页（信息流列表，点击卡片跳详情） |
| | `feature-discover` | 发现页（占位） |
| | `feature-publish` | 发布页（占位） |
| | `feature-message` | 消息页（占位） |
| | `feature-profile` | 我的页（占位） |
| | `feature-detail` | 笔记详情（接收 ARouter 参数 id/title/summary） |
| **(3) 业务公共层** | `common` | 公用模型（如 `NoteItem`）、公用资源（颜色等） |
| **(4) 基础层** | `base` | 路由路径常量 `RouterPath`、基类 `BaseActivity`、ARouter API 依赖 |

## 依赖关系

- **app** 依赖：base、common、所有 feature 模块
- 各 **feature** 只依赖 **common**（feature 之间不互相依赖）
- **common** 依赖 **base**
- **base** 不依赖本工程其他模块

组件间跳转统一通过 **ARouter** + **RouterPath** 常量，无直接类引用。

## 运行方式

1. 用 Android Studio 打开本目录（`modular-arouter-java-view`）。
2. 同步 Gradle，选择 `app` 运行到设备或模拟器。
3. 主界面底部 5 个 Tab：首页、发现、发布、消息、我的；点击「首页」进入列表，点击列表项通过 ARouter 进入详情页。

## 业务组件独立调试

各业务模块可单独作为 APP 安装运行，便于只调试该模块、加快编译。

1. **开关**：在根目录 `gradle.properties` 中，将对应模块的开关设为 `true`，其余保持 `false`：
   - `isFeatureHomeDebug=true` → 仅首页模块独立运行
   - `isFeatureDiscoverDebug`、`isFeaturePublishDebug`、`isFeatureMessageDebug`、`isFeatureProfileDebug`、`isFeatureDetailDebug` 同理。
2. **Sync**：同步 Gradle 后，被设为独立的模块会变为 application，主工程 `app` 会不再依赖该模块。
3. **运行**：在运行配置中选择该 feature 模块（如 `feature-home`），运行到设备即可单独安装并打开该模块的启动页（如首页列表）。
4. **还原**：调试结束后将该开关改回 `false`，Sync 后继续用 `app` 跑完整应用。

独立运行时使用各模块下 `src/debug/AndroidManifest.xml`（含 Application、Launcher Activity）及 `src/debug/java/.../DebugApp.java`（初始化 ARouter）。

## 技术栈

- **语言**：Java  
- **UI**：View（XML + Material）  
- **路由**：ARouter 1.5.2  
- **依赖注入**：Hilt 2.51.1（用于组件间通信与全局单例）  
- **最低 SDK**：24  

## 依赖注入（Hilt）与组件间通信

本项目使用 **Hilt** 做依赖注入，并演示**组件间通过接口 + 注入**进行通信（不增加 feature 模块之间的直接依赖）。

### 整体思路

- **common**：定义组件间通信的接口（如 `ILastOpenedNoteProvider`），不依赖 Hilt。
- **提供方 feature**：实现接口，用 `@Module` + `@Binds` 将实现注册到 Hilt（如 `feature-detail` 提供 `DetailHiltModule`）。
- **使用方 feature**：仅依赖 common，通过 `@Inject` 注入接口，由 Hilt 在运行时注入提供方的实现（如 `feature-home` 注入 `ILastOpenedNoteProvider`）。
- **app**：应用 Hilt 插件，Application 使用 `@HiltAndroidApp`，承载 Fragment 的 Activity 使用 `@AndroidEntryPoint`，并依赖各 feature，使 Hilt 能收集到所有 Module 的绑定。

### 本 Demo 中的示例

| 角色 | 模块 | 说明 |
|------|------|------|
| 接口定义 | `common` | `ILastOpenedNoteProvider`：获取/设置「最近打开的笔记标题」 |
| 实现与注册 | `feature-detail` | `LastOpenedNoteProviderImpl` + `DetailHiltModule`；详情页打开时调用 `setLastOpenedNoteTitle(title)` |
| 注入使用 | `feature-home` | `HomeFragment` 注入 `ILastOpenedNoteProvider`，在首页展示「最近浏览：xxx」 |

运行应用后：在首页点击某条笔记进入详情，返回首页即可在列表上方看到「最近浏览：该笔记标题」。  
这样 **feature-home** 与 **feature-detail** 无直接依赖，仅通过 common 的接口和 Hilt 完成数据共享。

### 涉及文件速览

- **app**：`ModularApp`（`@HiltAndroidApp`）、`MainActivity`（`@AndroidEntryPoint`），以及 `build.gradle.kts` 中的 Hilt 插件与依赖。
- **common**：`service/ILastOpenedNoteProvider.java`。
- **feature-detail**：`LastOpenedNoteProviderImpl`、`DetailHiltModule`、`DetailActivity`（`@AndroidEntryPoint` + `@Inject`）。
- **feature-home**：`HomeFragment`（`@AndroidEntryPoint` + `@Inject`）、布局中的「最近浏览」TextView。

## 学习要点

- 四层模块划分与依赖方向
- 使用 ARouter 做跨模块页面跳转与参数传递（`withString`、`@Autowired`）
- 路由路径集中在 base 层，避免业务模块相互依赖
- 使用 Hilt 做依赖注入，并通过「common 接口 + 某 feature 实现并注册」实现组件间通信，保持 feature 间无直接依赖
