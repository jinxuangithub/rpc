package com.sjw.shi.rpc.core.proxy;

import com.sjw.shi.rpc.core.RpcAppliation;
import com.sjw.shi.rpc.core.config.RpcConfig;

import java.lang.reflect.Proxy;

public class MockServiceProxyFactory {

    // 创建MockServiceProxy
    public static <T> T createProxy(Class<T> serviceClass) {
        if (RpcAppliation.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        } else {
            return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass}, new ServiceProxy());

        }
    }

    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass}, new MockServiceProxy());
    }
}
