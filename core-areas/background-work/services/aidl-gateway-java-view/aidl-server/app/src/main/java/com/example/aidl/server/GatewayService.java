package com.example.aidl.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 统一 AIDL 网关服务：绑定后返回 IAidlGateway，客户端通过网关获取各业务接口。
 * <p>
 * GatewayService 的作用：
 *   - 这是一个 Android Service，AIDL 服务的实体。
 *   - 负责接收来自客户端的绑定请求（onBind），并返回 GatewayServiceImpl（即 IAidlGateway.Stub）的 Binder 实例，完成跨进程通信的连接。
 * <p>
 * GatewayServiceImpl 的作用与区别：
 *   - GatewayServiceImpl 是 IAidlGateway 接口的具体实现，继承自 IAidlGateway.Stub。
 *   - 它内部持有各个实际业务接口（如 ICalculator、IGreeter）的实现，并通过对外公开的 getCalculator/getGreeter 方法返回这些接口的 Stub。
 *   - 也就是说，GatewayServiceImpl 仅负责业务分发（接口实现和桥接），而 GatewayService 负责服务的生命周期管理和 Binder 暴露。
 * <p>
 * 简单来说：
 *   - GatewayService：管理服务，并把 GatewayServiceImpl 的 Binder 实例暴露给客户端。
 *   - GatewayServiceImpl：真正实现各个业务 AIDL 接口的网关和分发中心。
 */
public class GatewayService extends Service {

    private final GatewayServiceImpl gateway = new GatewayServiceImpl();

    @Override
    public IBinder onBind(Intent intent) {
        return gateway;
    }
}
