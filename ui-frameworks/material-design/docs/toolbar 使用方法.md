### 什么是 Toolbar

Toolbar 是 Android 5.0 之后引入的控件，用于替代传统的 ActionBar，它提供了更高的灵活性和可定制性，可以轻松地集成 Material Design 风格





### 主题配置

为了使用 `Toolbar`，必须首先隐藏系统默认的 `ActionBar`。这是通过在 `styles.xml`中应用 `Theme.AppCompat.Light.NoActionBar`或其变体来实现的

```
<style name="Theme.ToolbarBasic" parent="Theme.AppCompat.Light.NoActionBar">
    <!-- 定制你的主题颜色等 -->
    <item name="colorPrimary">@color/purple_500</item>
    <item name="colorPrimaryDark">@color/purple_700</item>
    <item name="colorAccent">@color/teal_200</item>
</style>
```

然后，在 `AndroidManifest.xml` 中指定样式即可。

```
<!-- AndroidManifest.xml -->
<application
    android:theme="@style/Theme.ToolbarBasic.NoActionBar"
    ... >
    
    <activity
        android:name=".MainActivity"
        ... />
</application>
```





单个组件也可以设置样式







### Toolbar 的组成结构

在 Android 应用中，**首页按钮**（Home Button）通常指的是应用栏（ActionBar 或 Toolbar）左上角那个可点击的图标区域。它的核心作用是返回上一个 Activity、跳转到应用主页（根Activity）、或打开导航抽屉（DrawerLayout）。在代码中，它的固定资源 ID 是 **`android.R.id.home`**。你需要在 `onOptionsItemSelected`方法中监听这个 ID 来处理点击事件。

为什么这个首页按钮， 一会是打开侧滑菜单，一会是返回上一级菜单？简单来说，**这个按钮的行为不是固定的，而是由开发者根据当前界面在应用导航结构中的位置来动态定义的**。其背后的逻辑基于一个核心概念：**导航层级**。

- **当你的应用处于“首页”或“根层级”时**：在这个层级之上，应用内部已经没有更上一级的界面了。此时，这个按钮的默认“向上”功能失去了目标。因此，开发者会将它重新定义为打开侧滑菜单的功能，让用户能快速跳转到其他主要模块（如从“首页”切换到“我的”）。这遵循了 Material Design 的设计原则，即充分利用屏幕左上角的空间提供核心导航功能。
- **当你进入次级页面（如“设置”）时**：此时应用有了明确的上一级界面（比如“我的”页面）。系统会自动（或由开发者显式设置）将这个按钮的功能恢复为“向上”，用于在应用的层级结构中回溯。

```
<activity
    android:name=".SettingsActivity"
    android:parentActivityName=".MyProfileActivity" />
```



启用步骤如下：

1. **显示按钮**：调用 `setDisplayHomeAsUpEnabled(true)`确保按钮可见 。
2. **使其可点击**：调用 `setHomeButtonEnabled(true)`激活按钮的点击功能（在 Android 4.0 及以上版本中，此设置默认为 `false`，所以需要显式启用）。
3. **处理点击**：在 `onOptionsItemSelected`方法中编写逻辑来响应 `android.R.id.home`







## 问题

如果主题设置了 NoActionBar 样式，但 toolbar 却设置了 ActionBar 样式

