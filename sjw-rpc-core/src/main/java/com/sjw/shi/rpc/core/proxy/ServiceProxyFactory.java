package com.sjw.shi.rpc.core.proxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {
    public static <T> T getProxyInstance(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }


}
