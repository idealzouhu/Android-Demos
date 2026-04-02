## 专题总览

研究性能优化工具的使用方法



### 基础知识

**(1) 性能优化理论及工具**

- [性能优化基本理论.md](docs/性能优化基本理论.md): 说明现有的性能优化基本理论。
- [Perfetto 使用指南.md](docs/Perfetto%20使用指南.md): 说明如何使用 Perfetto 分析系统级性能问题。
- [Profiler 使用指南.md](docs/Profiler%20使用指南.md): 说明如何使用 Android Profiler 观察 CPU、内存和渲染数据。
- [LeakCanary 使用指南.md](docs/LeakCanary%20使用指南.md): 说明如何使用 LeakCanary 检测和分析内存泄漏。



**(2) 常见的性能优化问题**

- [内存抖动问题.md](docs/内存抖动问题.md): 说明内存抖动的典型现象、成因与常见优化方向。
- [ANR 问题.md](docs/ANR%20问题.md): 说明 ANR 的触发原因、排查思路与优化方法。
- [内存泄漏问题.md](docs/内存泄漏问题.md): 说明内存泄漏的常见场景、危害与治理方式。



### 实现项目

- [anr-main-blocking-java-view](anr-main-blocking-java-view/README.md): 列举常见的导致 ANR 问题的错误案例。
- [memory-allocation-churn-java-view](memory-allocation-churn-java-view/README.md): 列举导致内存抖动的经典错误案例。
- [memory-leak-static-holder-java-view](memory-leak-static-holder-java-view/README.md): 列举常见的静态持有导致内存泄漏的错误案例。
- [ui-jank-heavy-layout-java-view](ui-jank-heavy-layout-java-view/README.md): 列举常见的导致界面卡顿与渲染压力过高的错误案例。




### 参考资料

[App performance guide  | App quality  | Android Developers](https://developer.android.com/topic/performance/overview)