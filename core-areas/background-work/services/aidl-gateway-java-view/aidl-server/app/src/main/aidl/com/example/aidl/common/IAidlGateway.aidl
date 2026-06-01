package com.example.aidl.common;

import com.example.aidl.common.ICalculator;
import com.example.aidl.common.IGreeter;

/**
 * 统一 AIDL 网关接口：客户端通过此接口获取服务端提供的各个业务接口。
 */
interface IAidlGateway {
    ICalculator getCalculator();
    IGreeter getGreeter();
}
