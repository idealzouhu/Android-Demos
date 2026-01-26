### 项目描述

该项目重点介绍：

- 如何向应用添加 WorkManager、创建 worker 以清理在对图片进行模糊处理时生成的临时文件、对图片进行模糊- 处理，以及保存图片的最终副本，并且点击 **See File** 按钮可查看该副本。
- 学习如何监控后台工作的状态，并相应地更新应用的界面。





### 学习目标

- 确保[工作具有唯一性](https://developer.android.google.cn/guide/background/persistent/how-to/manage-work?hl=zh-cn#unique-work)。
- 如何取消工作。
- 如何定义[工作约束条件](https://developer.android.google.cn/topic/libraries/architecture/workmanager/how-to/define-work?hl=zh-cn#work-constraints)。
- 如何编写自动化测试来验证 worker 功能。
- 使用**后台任务检查器**检查已加入队列的 worker 的基础知识。