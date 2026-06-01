## 一、基础概念

在学习自定义 View 之前，先了解一下 View 绘制流程和事件分发流程。

### 1.1 自定义 View 的基本步骤

自定义 View 的核心步骤为：

1. **扩展现有类**：继承 `View`或某个子类。
2. **重写 `on` 方法**：例如 `onDraw()`, `onMeasure()`等，以注入自定义行为。
3. **使用新类**：在布局或代码中使用你创建的新类。



### 1.2 自定义 View 的类型

| 方法                                                         | 核心描述                                                     |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| [完全自定义组件](https://developer.android.google.cn/develop/ui/views/layout/custom-views/custom-components#fully-customized) | 继承 `View` 类，**必须重写** `onDraw` 和 `onMeasure` 方法，以完全控制绘制逻辑与尺寸测量，实现全新的图形组件。 |
| [复合控件](https://developer.android.google.cn/develop/ui/views/layout/custom-views/custom-components#compound) | 继承布局类（如 `LinearLayout`），组合多个现有控件，封装成一个逻辑统一的组件，通常无需重写绘制方法。 |
| [修改现有视图](https://developer.android.google.cn/develop/ui/views/layout/custom-views/custom-components#modifying) | 继承一个功能相近的现有控件（如 `EditText`），仅重写需要修改的少数方法（如 `onDraw`），在保留父类核心功能的基础上进行小幅调整。 |



### 1.3 自定义 View 的标准方法

| Category         | Methods                                                      | Description                                                  |
| :--------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| Creation         | Constructors                                                 | There is a form of the constructor that is called when the view is created from code and a form that is called when the view is inflated from a layout file. The second form parses and applies attributes defined in the layout file. |
|                  | [`onFinishInflate()`](https://developer.android.com/reference/android/view/View#onFinishInflate()) | Called after a view and all of its children are inflated from XML. |
| Layout           | [`onMeasure(int, int)`](https://developer.android.com/reference/android/view/View#onMeasure(int,%20int)) | Called to determine the size requirements for this view and all of its children. |
|                  | [`onLayout(boolean, int, int, int, int)`](https://developer.android.com/reference/android/view/View#onLayout(boolean,%20int,%20int,%20int,%20int)) | Called when this view must assign a size and position to all of its children. |
|                  | [`onSizeChanged(int, int, int, int)`](https://developer.android.com/reference/android/view/View#onSizeChanged(int,%20int,%20int,%20int)) | Called when the size of this view is changed.                |
| Drawing          | [`onDraw(Canvas)`](https://developer.android.com/reference/android/view/View#onDraw(android.graphics.Canvas)) | Called when the view must render its content.                |
| Event processing | [`onKeyDown(int, KeyEvent)`](https://developer.android.com/reference/android/view/View#onKeyDown(int,%20android.view.KeyEvent)) | Called when a key down event occurs.                         |
|                  | [`onKeyUp(int, KeyEvent)`](https://developer.android.com/reference/android/view/View#onKeyUp(int,%20android.view.KeyEvent)) | Called when a key up event occurs.                           |
|                  | [`onTrackballEvent(MotionEvent)`](https://developer.android.com/reference/android/view/View#onTrackballEvent(android.view.MotionEvent)) | Called when a trackball motion event occurs.                 |
|                  | [`onTouchEvent(MotionEvent)`](https://developer.android.com/reference/android/view/View#onTouchEvent(android.view.MotionEvent)) | Called when a touchscreen motion event occurs.               |
| Focus            | [`onFocusChanged(boolean, int, Rect)`](https://developer.android.com/reference/android/view/View#onFocusChanged(boolean,%20int,%20android.graphics.Rect)) | Called when the view gains or loses focus.                   |
|                  | [`onWindowFocusChanged(boolean)`](https://developer.android.com/reference/android/view/View#onWindowFocusChanged(boolean)) | Called when the window containing the view gains or loses focus. |
| Attaching        | [`onAttachedToWindow()`](https://developer.android.com/reference/android/view/View#onAttachedToWindow()) | Called when the view is attached to a window.                |
|                  | [`onDetachedFromWindow()`](https://developer.android.com/reference/android/view/View#onDetachedFromWindow()) | Called when the view is detached from its window.            |
|                  | [`onWindowVisibilityChanged(int)`](https://developer.android.com/reference/android/view/View#onWindowVisibilityChanged(int)) | Called when the visibility of the window containing the view is changed. |



## 二、自定义 View 的基本理论

### 2.1 定义视图行为和外观

attribute 是控制视图行为和外观的一种强大方式，但它们**只能在视图初始化时读取**。为了能够**在视图运行时提供动态的行为**，定义一个与 attribute 关联的动态访问接口 property ，提供公开的 getter 和 setter 方法。

> 具体细节查看 [Create a view class  | Views  | Android Developers](https://developer.android.google.cn/develop/ui/views/layout/custom-views/create-view#applyattr)



#### 2.1.1 自定义并应用 attribute

**(1) 自定义 attribute**

为了让自定义视图支持 XML 属性配置，需要在 `res/values/attrs.xml`中定义属性。使用 `<declare-styleable>`标签，指定名称和格式（如 `boolean`、`enum`）。

```xml
<resources>
    <declare-styleable name="PieChart">
        <attr name="showText" format="boolean" />
        <attr name="labelPosition" format="enum">
            <enum name="left" value="0"/>
            <enum name="right" value="1"/>
        </attr>
    </declare-styleable>
</resources>
```

然后，我们就可以在 XML 布局文件中为自定义视图直接设置属性，只需引入 需引入自定义命名空间（如 `xmlns:custom="http://schemas.android.com/apk/res-auto"`）即可。
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
   xmlns:custom="http://schemas.android.com/apk/res-auto">
 <com.example.customviews.charting.PieChart
     custom:showText="true"
     custom:labelPosition="left" />
</LinearLayout>
```

 **(2) 应用自定义 attribute**

当系统实例化视图时，会将包含所有属性值的 `AttributeSet` 对象传递给视图的构造函数（就是你必须实现的那个 `View(Context, AttributeSet)`构造器），我们可以在构造函数中解析属性值。
- **必须使用 [`obtainStyledAttributes()`](https://developer.android.google.cn/reference/android/content/res/Resources.Theme#obtainStyledAttributes(android.util.AttributeSet, int[], int, int))** 方法，因为它能正确处理资源引用和样式应用。
- 结束时必须调用 `recycle()` 来回收资源。

```java
public PieChart(Context context, AttributeSet attrs) {
   super(context, attrs);
   TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.PieChart,
        0, 0);

   try {
       mShowText = a.getBoolean(R.styleable.PieChart_showText, false);
       textPos = a.getInteger(R.styleable.PieChart_labelPosition, 0);
   } finally {
       a.recycle();
   }
}
```



#### 2.1.2 暴露 property

布局里的 `attribute` 只在 **inflate** 时写入；要在**创建完成后**从代码里改同一套状态，应对应暴露 **`property`**：**getter** 读当前值，**setter** 写内部字段并通知系统刷新。

- **Setter（如 `setShowText`）**：除赋值外，若会影响绘制，应调用 [`invalidate()`](https://developer.android.google.cn/reference/android/view/View#invalidate()) 触发重绘；若还可能改变测量结果或布局（宽高、边距等），再调用 [`requestLayout()`](https://developer.android.google.cn/reference/android/view/View#requestLayout()) 走一遍 **measure / layout**。
- **Getter（如 `isShowText`）**：对外返回与 XML `attribute` 对应的当前状态。

```java
public boolean isShowText() {
   return mShowText;
}

public void setShowText(boolean showText) {
   mShowText = showText;
   invalidate();
   requestLayout();
}
```

这里的 `mShowText` 与构造函数里 `a.getBoolean(R.styleable.PieChart_showText, …)` 读到的是**同一字段**，XML 默认值与代码里后续修改共用这份状态。



### 2.2 视图的通信能力

一个设计良好的自定义视图应提供完整的接口，允许外部与其通信。
- **输入接口（由外到内）**：通过 **属性 (Properties)** 的 getter/setter 方法，允许外部代码设置和修改视图的状态。
- **输出接口（由内到外）**：通过 **事件监听器 (Event Listeners)**，允许视图在内部状态（如用户选择）变化时通知外部。





### 2.2 视图的绘图机制

> 具体细节查看 [Create a custom drawing  | Views  | Android Developers](https://developer.android.google.cn/develop/ui/views/layout/custom-views/custom-drawing)

 [`android.graphics`](https://developer.android.google.cn/reference/android/graphics/package-summary) 将绘制过程分成两部分：

- **Canvas** (画布)：决定画什么，提供绘制**图形元素**的方法，如 `drawText()`（文字）、`drawRect()`（矩形）、`drawBitmap()`（图片）。它决定了“形状”和“位置”。
- **Paint** (画笔)：决定怎么画，提供绘制**样式**的配置，如 `setColor()`（颜色）、`setStyle()`（填充或描边）、`setTypeface()`（字体）。它决定了“颜色”和“风格”。

另外，在绘制之前，通常需要知道视图的准确大小，确保绘图内容不会超出可用区域。我们可以通过 [onSizeChanged()](https://developer.android.google.cn/reference/android/view/View#onSizeChanged(int, int, int, int)) 或者 [onMeasure()](https://developer.android.google.cn/reference/android/view/View#onMeasure(int, int)) 来计算绘图区域。

```java
private Boolean showText;    // Obtained from styled attributes.
private int textWidth;       // Obtained from styled attributes.

@Override
protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    // Account for padding.
    float xpad = (float)(getPaddingLeft() + getPaddingRight());
    float ypad = (float)(getPaddingTop() + getPaddingBottom());

    // Account for the label.
    if (showText) xpad += textWidth;

    float ww = (float)w - xpad;
    float hh = (float)h - ypad;

    // Figure out how big you can make the pie.
    float diameter = Math.min(ww, hh);
}
```



### 2.3 视图的交互能力

在已经创建了一个可以绘制自身内容（静态）的自定义视图的基础上，增加**响应用户输入**（如触摸、手势）和**动态、流畅地反馈**（如动画、惯性滚动）的能力。简单来说，就是将视图从一个“静态图片”转变为一个用户可以与之进行实时、自然互动的动态组件。

> 具体细节查看 [Make a custom view interactive  | Views  | Android Developers](https://developer.android.google.cn/develop/ui/views/layout/custom-views/making-interactive)

关键点有：

- **处理输入手势**：通过 `GestureDetector`将原始触摸事件 (`MotionEvent`) 转换为高级语义手势（如 `onScroll`, `onFling`），这是交互的基础。
- **创建物理上合理的动作**：通过 `Scroller`类模拟真实的物理惯性（如快速滑动后视图不会立即停止，而是有减速滑行的效果），这是提升交互质感的关键。
- **让转场更顺畅**：通过属性动画框架（`ViewPropertyAnimator`或 `ValueAnimator`）驱动视图状态（如位置、角度）的变化，使其平滑过渡，而非瞬间切换，这是保证交互流畅的核心。

注意，Scroller 只负责**计算**符合物理规律的坐标，**不负责**自动移动视图。我们需要手动获取 `currX/Y`， 并使用属性动画框架应用到视图属性上。







## 三、最佳实践

- 根据应用场景选择自定义的 View 类型。

- 

- **避免使用 `Handler`**：View 已提供 `post()`和 `postDelayed()`方法，能确保任务在 UI 线程安全执行，无需额外创建 Handler。



为了确保交互（手势、状态转换）不卡顿，**动画必须[稳定在每秒 60 帧](https://developer.android.google.cn/develop/ui/views/layout/custom-views/optimizing-view)**。这意味着每一帧的绘制时间必须控制在 **16ms** 以内。

> 如果一个动画正在播放，为了产生连续的视觉效果，每一帧都必须调用 `onDraw()`

**(1) 绘制性能优化**

- **禁止在 `onDraw`中创建对象**：`onDraw`每秒可能调用 60 次，频繁创建 `Paint`、`Path`等对象会导致内存抖动（频繁 GC），引发卡顿。应将对象声明为**成员变量**并在构造函数中初始化。
- **减少 `onDraw` 的调用频率**：onDraw的调用是由 invalidate() 触发的。避免不必要的 `invalidate()`调用。只有在视图内容真正发生变化时才调用它，不要盲目地在每一帧都触发重绘。

**(2) 布局性能优化**

- **保持视图层级扁平化**：避免创建深度嵌套的 `ViewGroup`结构。系统在调用 `requestLayout()`时，可能会多次遍历整棵视图树来计算尺寸，深层次结构会显著拖慢速度。
- **使用自定义 ViewGroup 进行针对性优化**：通用的内置布局（如 `LinearLayout`）不同，自定义 `ViewGroup`可以基于你对子视图尺寸的特定假设，**跳过对某些子视图的测量**。





## 参考资料

[Create a view class  | Views  | Android Developers](https://developer.android.google.cn/develop/ui/views/layout/custom-views/create-view)