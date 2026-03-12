### 项目概述

本案例用于演示 AIDL 的使用方法，通过使用统一网关方式，创建一个统一的 AIDL 接口作为网关，管理服务端中的多个 AIDL 接口。



### 项目结构

```
aidl-gateway-java-view/
├── aidl-client/                             # 客户端应用
│   └── app/src/main/
│       ├── aidl/com/example/aidl/common/    # 与服务端一致的 AIDL 接口定义
│       │   ├── IAidlGateway.aidl            # 统一网关接口
│       │   ├── ICalculator.aidl
│       │   └── IGreeter.aidl
│       ├── java/.../MainActivity.java        # 绑定网关并调用 ICalculator、IGreeter
│       └── res/layout/activity_main.xml
├── aidl-server/                             # 服务端应用
│   └── app/src/main/
│       ├── aidl/com/example/aidl/common/    # AIDL 接口定义
│       ├── java/.../
│       │   ├── GatewayService.java          # 网关 Service，onBind 返回 IAidlGateway
│       │   ├── GatewayServiceImpl.java      # IAidlGateway 实现，提供 getCalculator/getGreeter
│       │   ├── CalculatorServiceImpl.java   # ICalculator 实现
│       │   └── GreeterServiceImpl.java      # IGreeter 实现
│       └── AndroidManifest.xml              # 注册 GatewayService
└── README.md
```

### 使用方式

1. 先安装并运行 **aidl-server**（AIDL 服务端）。
2. 再安装并运行 **aidl-client**（AIDL 客户端）。
3. 在客户端点击「绑定服务端网关」，再点击「3 + 5 = ?」或「问候 "World"」即可通过网关调用服务端的 ICalculator、IGreeter。

### 学习目标

通过该项目，你将掌握：

- 使用 AIDL 定义接口并在服务端实现、在客户端调用
- 统一网关模式：一个 IAidlGateway 暴露多个子接口（如 ICalculator、IGreeter），便于扩展与管理
- 跨进程绑定 Service（显式 Intent 指定包名与类名） 