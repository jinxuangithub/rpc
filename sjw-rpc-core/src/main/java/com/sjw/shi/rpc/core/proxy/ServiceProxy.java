package com.sjw.shi.rpc.core.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.sjw.shi.rpc.core.RpcAppliation;
import com.sjw.shi.rpc.core.model.RpcRequest;
import com.sjw.shi.rpc.core.model.RpcResponse;
import com.sjw.shi.rpc.core.serializer.SerilizerFactory;
import com.sjw.shi.rpc.core.serializer.Serializer;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Serializer serializer = SerilizerFactory.creatInstance(RpcAppliation.getRpcConfig().getSerilizer());
        RpcRequest request = RpcRequest.builder()
                .methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .parameterTypes(method.getParameterTypes()).build();
        // 序列化
        try {
            byte[] bytes = serializer.serialize(request);
            try (HttpResponse httpServerResponse = HttpRequest.post("http://localhost:8080")
                    .body(bytes)
                    .execute()) {
                byte[] result =
                        httpServerResponse.bodyBytes();
                RpcResponse response = serializer.deserialize(result, RpcResponse.class);
                return response.getResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
