[TOC]

## 一、什么是滑动视图

**滑动视图**允许用户通过水平滑动手势在同级屏幕（例如标签页）之间导航，这种模式也称为「水平分页」。
与早期的 ViewPager 相比，ViewPager2 基于 RecyclerView，支持垂直滑动、RTL、并改进了数据集变更与懒加载等行为，推荐在新项目中使用 ViewPager2。


## 二、实现滑动视图

本主题介绍如何使用 **ViewPager2** 创建带滑动视图的标签页布局，并配合 **TabLayout** 显示标签栏。


### 2.1 添加依赖

使用 ViewPager2 和标签页时，需要添加 ViewPager2 与 Material 组件依赖。可在模块的 `build.gradle` 中加入：

```groovy
dependencies {
    implementation 'androidx.viewpager2:viewpager2:1.1.0'
    implementation 'com.google.android.material:material:1.11.0'
}
```

### 2.2 创建滑动视图

### 2.2.1 布局文件

在 XML 中为 ViewPager2 预留占位。若每个页面占满可用空间，可这样写：

**`res/layout/collection_demo.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <androidx.viewpager2.widget.ViewPager2
        android:id="@+id/pager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />
</LinearLayout>
```

**单页内容布局**（每个 Fragment 使用的布局，例如 `res/layout/fragment_collection_object.xml`）：

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@android:id/text1"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:textSize="24sp" />
</FrameLayout>
```

### 2. Fragment 与 Adapter（Java）

通过 **FragmentStateAdapter** 将多页数据与 ViewPager2 绑定，每页对应一个 Fragment。

**集合宿主 Fragment**（包含 ViewPager2 的 Fragment）：

```java
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CollectionDemoFragment extends Fragment {

    private DemoCollectionAdapter demoCollectionAdapter;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.collection_demo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        demoCollectionAdapter = new DemoCollectionAdapter(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(demoCollectionAdapter);
    }
}
```

**Adapter**（按位置创建对应 Fragment）：

```java
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DemoCollectionAdapter extends FragmentStateAdapter {

    public DemoCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new DemoObjectFragment();
        Bundle args = new Bundle();
        args.putInt(DemoObjectFragment.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 100;
    }
}
```

**单页 Fragment**（代表集合中的一项）：

```java
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DemoObjectFragment extends Fragment {

    public static final String ARG_OBJECT = "object";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_object, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_OBJECT)) {
            ((TextView) view.findViewById(android.R.id.text1))
                    .setText(Integer.toString(args.getInt(ARG_OBJECT)));
        }
    }
}
```

---

## 使用 TabLayout 添加标签页

**TabLayout** 在 ViewPager2 上方提供横向标签，便于在滑动视图各页之间切换。

### 1. 布局中加入 TabLayout

在 ViewPager2 上方增加 `TabLayout`：

**`res/layout/collection_demo.xml`（含 TabLayout）**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.google.android.material.tabs.TabLayout
        android:id="@+id/tab_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:tabMode="scrollable" />

    <androidx.viewpager2.widget.ViewPager2
        android:id="@+id/pager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />
</LinearLayout>
```

### 2. 用 TabLayoutMediator 绑定标签与 ViewPager2（Java）

在 `onViewCreated` 中创建 **TabLayoutMediator**，将 TabLayout 与 ViewPager2 关联，并设置每页标签文案：

```java
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CollectionDemoFragment extends Fragment {

    private DemoCollectionAdapter demoCollectionAdapter;
    private ViewPager2 viewPager;

    // ... onCreateView 同上 ...

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        demoCollectionAdapter = new DemoCollectionAdapter(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(demoCollectionAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("OBJECT " + (position + 1))
        ).attach();
    }
}
```

这样滑动页面时标签会同步高亮，点击标签也会切换对应页面。更多标签设计可参考 [Material Design - Tabs](https://material.io/design/components/tabs.html)。



## 参考资料

[使用 ViewPager2 创建包含标签的滑动视图  | App architecture  | Android Developers](https://developer.android.google.cn/guide/navigation/advanced/swipe-view-2?hl=zh-cn)