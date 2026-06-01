### 项目概述

本案例集中演示 **内存抖动（Memory Allocation Churn）** 的典型错误写法：在热点路径（如 `onDraw`、高频循环）中频繁创建短生命周期对象，导致 GC 压力增大、帧率下降甚至卡顿。

建议配合 Android Studio Memory Profiler 录制分配（Record allocations），观察分配速率与 GC 事件。

---

### 典型错误案例

| 类型 | 错误写法概要 | 为何导致抖动 |
|------|--------------|--------------|
| **onDraw 内分配** | 在 `View.onDraw()` 中每帧 `new` 对象、拼接字符串或创建 `Paint`/`Path` | 每帧触发大量分配，16ms 内 GC 可能介入，造成 jank |
| **循环 + 字符串拼接** | 在后台或 UI 线程用 `+` 循环拼接大量字符串 | 产生大量临时 `String` 对象，分配速率飙升 |

---

### 项目结构

```
memory-allocation-churn-java-view/
├── app/src/main/java/com/example/memory/allocation/churn/
│   ├── MainActivity.java              # 触发字符串拼接循环、开关 onDraw 抖动
│   ├── BadAllocationOnDrawView.java   # 在 onDraw 中故意分配
│   └── AllocationChurnDemos.java      # 演示用分配逻辑
└── app/src/main/res/layout/activity_main.xml
```

### 学习目标

- 理解「分配速率过高 → 频繁 GC → 卡顿」的链路
- 学会在 Profiler 中定位热点分配路径
- 建立「绘制与高频路径零分配或对象复用」的意识
