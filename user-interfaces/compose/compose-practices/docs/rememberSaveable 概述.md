### 什么是 rememberSaveable

rememberSaveable是 Jetpack Compose 中的一个状态保存机制，专门用于在配置变更和进程被系统杀死后恢复时保存和恢复状态。注意，rememberSaveable是专门为 Compose 设计的 API，只能用于 Compose 中，不能直接用于传统 View 系统。

rememberSaveable 的作用主要有：

- **解决 Compose 中的状态持久化问题** - 在配置变更和进程被杀死时自动保存恢复
- **简化状态管理** - 对于简单 UI 状态，无需复杂的 ViewModel
- **与 ViewModel 互补** - 处理不同类型的状态（UI 状态 vs 业务数据）。简单、独立的 UI 状态，使用 rememberSaveable。复杂业务逻辑、共享数据则使用 ViewModel。





### rememberSaveable和ViewModel 的区别

| 特性               | rememberSaveable      | ViewModel                |
| ------------------ | --------------------- | ------------------------ |
| **作用域**         | 单个可组合函数        | Activity/Fragment/导航图 |
| **数据共享**       | ❌ 仅限于单个组合函数  | ✅ 可在多个组件间共享     |
| **配置变更保存**   | ✅ 保存                | ✅ 保存                   |
| **进程被杀死恢复** | ✅ 保存（通过 Bundle） | ❌ 不保存                 |
| **应用完全退出**   | ❌ 不保存              | ❌ 不保存                 |
| **业务逻辑**       | ❌ 不适合复杂逻辑      | ✅ 适合复杂业务逻辑       |
| **生命周期**       | 跟随 Composition      | 跟随 Activity/Fragment   |







### 数据存储流程

