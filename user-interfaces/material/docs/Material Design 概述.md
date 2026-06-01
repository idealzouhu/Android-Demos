









### 主题







矢量图的大小和主题也可以调整

```
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="1024"
    android:viewportHeight="1024">
  <path
      android:pathData="M128,469.3h768v85.3H128zM128,213.3h768v85.3H128zM128,725.3h768v85.3H128z"
      android:fillColor="?attr/colorOnPrimary"/>
</vector>

```







### UI 导航模式

| 导航模式 | 组件                  | 应用场景                 |
| -------- | --------------------- | ------------------------ |
| 底部导航 | BottomNavigationView  | 3-5个主要功能模块        |
| 顶部 Tab | TabLayout + ViewPager | 同一模块下的子分类       |
| 侧滑菜单 | DrawerLayout          | 功能菜单、设置、用户中心 |
| 悬浮按钮 | FloatingActionButton  | 主要操作                 |
| 导航抽屉 | NavigationRail        | 大屏适配                 |



[使用 ViewPager2 在 Fragment 之间滑动  | Views  | Android Developers](https://developer.android.google.cn/develop/ui/views/animations/screen-slide-2?hl=zh-cn)



[导航原则  | App architecture  | Android Developers](https://developer.android.google.cn/guide/navigation/principles?hl=zh-cn)





## 布局

### FrameLayout- 基础堆叠容器

FrameLayout 是最基础的堆叠容器。

```
<!-- 最简单的容器，子控件堆叠 -->
<FrameLayout>
    <ImageView />  <!-- 背景 -->
    <TextView />   <!-- 盖在图片上 -->
    <Button />     <!-- 盖在最上面 -->
</FrameLayout>
```



#### **CoordinatorLayout** - 协调联动容器

**CoordinatorLayout**  是加强版的 FrameLayout ，能协调子控件之间的联动，自动处理复杂动画(下拉刷新、视差滚动)。

```
<androidx.coordinatorlayout.widget.CoordinatorLayout>
    
    <!-- 可滚动的列表 -->
    <androidx.core.widget.NestedScrollView
        app:layout_behavior="@string/appbar_scrolling_view_behavior">
    </androidx.core.widget.NestedScrollView>
    
    <!-- 工具栏（上滑隐藏） -->
    <com.google.android.material.appbar.AppBarLayout>
        <androidx.appcompat.widget.Toolbar />
    </com.google.android.material.appbar.AppBarLayout>
    
    <!-- 悬浮按钮（滑动时自动隐藏） -->
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        app:layout_anchor="@id/appbar" />
        
</androidx.coordinatorlayout.widget.CoordinatorLayout>
```



在  [fab-basic-java-view](..\fab-basic-java-view) 案例中， `CoordinatorLayout`实现悬浮动作按钮（FAB）与 Snackbar 的联动效果。当 Snackbar 弹出时，FAB 会自动上移避免被遮挡；Snackbar 消失时，FAB 会自动下移回原位。





## LinearLayout



### **AppBarLayout** - 工具栏专用容器

AppBarLayout 实际上是一个 垂直方向的 LinearLayout，内部做了很多滚动事件的封装。

AppBarLayout  用于专门管理工具栏行为，配合滚动控件实现折叠效果



注意： **AppBarLayout** 必须是 CoordinatorLayout 的子布局。



```
<com.google.android.material.appbar.AppBarLayout>
    
    <!-- 普通工具栏 -->
    <androidx.appcompat.widget.Toolbar
        app:layout_scrollFlags="scroll|enterAlways" />
    
    <!-- 可折叠工具栏 -->
    <com.google.android.material.appbar.CollapsingToolbarLayout
        app:layout_scrollFlags="scroll|exitUntilCollapsed|snap">
        <ImageView />
    </com.google.android.material.appbar.CollapsingToolbarLayout>
    
    <!-- Tab 标签栏 -->
    <com.google.android.material.tabs.TabLayout />
    
</com.google.android.material.appbar.AppBarLayout>
```





举个例子，在 Toolbar 和 RecyclerView 的页面中，如果使用了 AppBarLayout  。可以看到，向上滚动RecyclerView，Toolbar竟然消失了！而向下滚动

RecyclerView，Toolbar又会重新出现。





### 其他

#### SwipeRefreshLayout

SwipeRefreshLayout 是用于实现下拉刷新功能的核心类。

```
dependencies {
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
}

```







### CollapsingToolbarLayout 

CollapsingToolbarLayout  是一个作用于 Toolbar 基础之上的布局，它也是由 Material 库提供的。

不过，CollapsingToolbarLayout是不能独立存在的，它在设计的时候就被限定只能作为 AppBarLayout 的直接子布局来使用。而 AppBarLayout 又必须是CoordinatorLayout 的子布局。



#### 布局属性详解

- **`app:layout_scrollFlags`**：控制视图的滚动行为
- **`app:layout_collapseMode`**：定义折叠模式（`pin`固定模式或 `parallax`视差模式）
- **`app:contentScrim`**：折叠后工具栏的背景色
- **`app:expandedTitleMargin`**：展开状态下标题的边距设置