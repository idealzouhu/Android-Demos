package com.example.aidl.server;

import com.example.aidl.common.IGreeter;

/**
 * 问候 AIDL 接口的实现（服务端）。
 */
public class GreeterServiceImpl extends IGreeter.Stub {

    @Override
    public String greet(String name) {
        if (name == null || name.isEmpty()) {
            return "Hello!";
        }
        return "Hello, " + name + "!";
    }
}
