package com.example.aidl.server;

import com.example.aidl.common.IAidlGateway;
import com.example.aidl.common.ICalculator;
import com.example.aidl.common.IGreeter;

/**
 * 统一 AIDL 网关的实现：持有并对外提供各业务接口的 Stub。
 */
public class GatewayServiceImpl extends IAidlGateway.Stub {

    private final ICalculator calculator = new CalculatorServiceImpl();
    private final IGreeter greeter = new GreeterServiceImpl();

    @Override
    public ICalculator getCalculator() {
        return calculator;
    }

    @Override
    public IGreeter getGreeter() {
        return greeter;
    }
}
