### 项目概述


本案例完整演示了 Android 中 Intent 的各种使用方式，包括显式Intent、隐式Intent、数据传递和结果返回等核心功能。通过多个Activity的交互，全面展示Intent在Android开发中的应用。



### 项目结构

```
multi-activity-java-view/
├── app/src/main/java/com/example/multi/activity/
│   ├── MainActivity.java                    # 主控制面板，演示所有Intent启动方式
│   ├── InternalActivity.java                # 显式Intent演示
│   ├── WebViewActivity.java                 # 隐式Intent演示（响应http/https）
│   ├── DataTransferActivity.java            # 复杂数据传递演示
│   └── ResultActivity.java                  # 带返回结果的Activity
├── app/src/main/res/
│   ├── layout/
│   │   ├── activity_main.xml               # 主界面布局
│   │   ├── activity_internal.xml           # 内部页面布局
│   │   ├── activity_webview.xml            # 网页查看布局
│   │   ├── activity_data_transfer.xml      # 数据传递页面布局
│   │   └── activity_result.xml             # 结果页面布局
│   ├── menu/
│   │   └── main_menu.xml                   # 菜单资源文件
│   └── values/
│       ├── strings.xml                     # 字符串资源
│       └── styles.xml                      # 样式主题配置
└── app/build.gradle                        # 项目依赖配置
```

### 学习目标

通过本项目，您将掌握：
1. Intent 核心概念
   - 显式Intent：明确指定目标组件的启动方式
   - 隐式Intent：通过Action和Data匹配组件的启动方式
   - Intent Filter：组件响应规则的配置
2. 数据传递机制
   - 基本数据类型传递（String、int、boolean等）
   - 复杂数据传递（数组、Bundle、Serializable对象）
   - 双向数据交互（startActivityForResult）
3. Activity 间通信
   - 启动和返回流程
   - 生命周期管理
   - 结果回调处理