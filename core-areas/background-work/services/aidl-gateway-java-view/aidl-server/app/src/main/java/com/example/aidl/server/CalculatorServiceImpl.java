package com.example.aidl.server;

import com.example.aidl.common.ICalculator;

/**
 * 计算器 AIDL 接口的实现（服务端）。
 */
public class CalculatorServiceImpl extends ICalculator.Stub {

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
