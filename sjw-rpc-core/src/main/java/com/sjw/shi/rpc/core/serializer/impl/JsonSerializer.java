package com.sjw.shi.rpc.core.serializer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjw.shi.rpc.core.model.RpcRequest;
import com.sjw.shi.rpc.core.model.RpcResponse;
import com.sjw.shi.rpc.core.serializer.Serializer;

import java.io.IOException;
/*
* Json 序列化器
 */
public class JsonSerializer implements Serializer {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, clazz);
        if (obj instanceof RpcRequest) {
            return hadleRpcRequest((RpcRequest) obj, clazz);
        } else if (obj instanceof RpcResponse) {
            return handleRpcResponse((RpcResponse) obj, clazz);
        }
        return obj;
    }
    /*
    *因为Object的原始对象会被擦除掉，导致反序列化时被认为是LinkedHashMap,导致无法完成类型转换，因此需要特殊处理
     */

    public <T> T hadleRpcRequest(RpcRequest rpcRequest, Class<T> classType) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getParameters();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (!args.getClass().isAssignableFrom(parameterType)) {
                byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(bytes, parameterType);
            }
        }
        return classType.cast(rpcRequest);

    }

    public <T> T handleRpcResponse(RpcResponse rpcResponse, Class<T> classType) throws IOException {
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getResult());
        rpcResponse.setResult(OBJECT_MAPPER.readValue(bytes, rpcResponse.getResultType()));
        return classType.cast(rpcResponse);
    }
}
