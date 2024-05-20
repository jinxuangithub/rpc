package com.sjw.shi.rpc.core.service;


import com.sjw.shi.rpc.core.model.RpcRequest;
import com.sjw.shi.rpc.core.model.RpcResponse;
import com.sjw.shi.rpc.core.registry.LocalRegistry;
import com.sjw.shi.rpc.core.serializer.JDKSerializer;
import com.sjw.shi.rpc.core.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandle implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        final Serializer serializer = new JDKSerializer();
        System.out.println("Reciverd request:" + httpServerRequest.method() + ":" + httpServerRequest.uri());
        httpServerRequest.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMessage("RpcRequest is null!");
                doResponse(httpServerRequest, rpcResponse, serializer);
                return;
            }
            try {
                Class<?> impclass = LocalRegistry.get(rpcRequest.getInterfaceName());
                Method method = impclass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(impclass.newInstance(), rpcRequest.getParameters());
                rpcResponse.setResult(result);
                rpcResponse.setResultType(method.getReturnType());
                rpcResponse.setMessage("Ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(httpServerRequest, rpcResponse, serializer);
        });

    }

    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = request.response()
                .putHeader("content-type", "application/json");
        try {
            byte[] bytes = serializer.serialize(rpcResponse);


            response.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            response.end(Buffer.buffer());
        }

    }
}
