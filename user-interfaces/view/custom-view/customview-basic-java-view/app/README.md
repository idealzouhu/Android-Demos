### 项目概述

本工程用**三个独立 Android Library 模块**演示自定义 View 的常见做法，宿主应用 `app` 在同一界面中依次展示。说明与官方指南一致，可参考：[创建自定义视图组件](https://developer.android.google.cn/develop/ui/views/layout/custom-views/custom-components)。

---

**1. 完全自定义组件（Fully customized）**

| 项 | 内容 |
| --- | --- |
| 模块 / 类 | `signalMeterView` → `SignalMeterView` |
| 效果 | 半圆弧形「信号 / 仪表」：三色分区弧段 + 指针 + 数值 |
| 要点 | 直接继承 `View`；重写 `onMeasure` 给出默认尺寸；在 `onDraw` 中用 `Canvas.drawArc`、`drawLine`、`drawCircle` 等绘制；通过自定义属性控制量程、当前值、安全 / 警告 / 危险区颜色与分界比例。当前实现用**三段实色弧**区分区间；若要做连续过渡，可改为 `SweepGradient` 等 `Shader`。 |

---

**2. 复合控件（Compound control）**

| 项 | 内容 |
| --- | --- |
| 模块 / 类 | `labeledEditText` → `LabeledEditText` |
| 效果 | 左侧标签 + 中间输入框 + 右侧清除按钮 |
| 要点 | 继承 `LinearLayout`（横向）；用 `labeled_edit_text.xml` 的 `<merge>` 组合 `TextView`、`EditText`、**`ImageButton`**（清除，带 ripple）；`TextWatcher` 控制清除按钮显隐，点击清空并 `requestFocus`。一般**不必**重写 `onDraw`，外观由子 View 与主题负责。 |

---

**3. 扩展现有控件（Modify existing view）**

| 项 | 内容 |
| --- | --- |
| 模块 / 类 | `linedEditText` → `LinedEditText` |
| 效果 | 多行输入 + 横格纸式横线（示例为**虚线**，可改为实线或换色） |
| 要点 | 继承 `AppCompatEditText`；先 `super.onDraw(canvas)` 再画线，避免挡住文字与光标；按 `getLineBounds` 与行高延伸空白行；虚线通过 `DashPathEffect` + `Path` 绘制。输入、光标、滚动等行为与系统 `EditText` 一致。 |

---

### 项目结构（节选）

```
customview-basic-java-view/
├── app/                                    # 宿主：单 Activity 演示
│   └── src/main/
│       ├── java/com/example/customview/basic/
│       │   └── MainActivity.java           # 主界面（含 SignalMeter 动画演示）
│       └── res/
│           ├── layout/activity_main.xml   # 三个示例 + MaterialCardView 分区
│           └── values/strings.xml、themes.xml 等
├── signalMeterView/                        # 库：SignalMeterView + attrs / 样式
├── labeledEditText/                        # 库：LabeledEditText + merge 布局 + attrs
├── linedEditText/                          # 库：LinedEditText + attrs
├── settings.gradle.kts                     # include 上述模块
├── build.gradle.kts
└── gradle/
```

---

### 学习目标

通过本案例，你可以对照代码理清：

- **完全自定义**：何时从 `View` 起手、`onMeasure` 与 `setMeasuredDimension`、`onDraw` 与 `invalidate` / 自定义属性的配合。
- **复合控件**：用布局文件或代码组装子 View、在容器内转发测量与事件、何时不需要自定义绘制。
- **扩展控件**：在保留原有行为的前提下，用 `onDraw` 做最小视觉增强；`super.onDraw` 与后绘制的顺序对叠层的影响。
- **工程形态**：多模块 library + app 依赖、在 XML 中引用自定义 View 与 `app:` 前缀属性。
