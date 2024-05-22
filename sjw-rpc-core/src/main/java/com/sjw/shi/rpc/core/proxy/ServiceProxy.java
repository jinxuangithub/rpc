package com.sjw.shi.rpc.core.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.sjw.shi.rpc.core.RpcAppliation;
import com.sjw.shi.rpc.core.config.RpcConfig;
import com.sjw.shi.rpc.core.constant.ConfigConstant;
import com.sjw.shi.rpc.core.model.RpcRequest;
import com.sjw.shi.rpc.core.model.RpcResponse;
import com.sjw.shi.rpc.core.registry.Registry;
import com.sjw.shi.rpc.core.registry.RegistryFactory;
import com.sjw.shi.rpc.core.serializer.Serializer;
import com.sjw.shi.rpc.core.serializer.SerilizerFactory;
import com.sjw.shi.rpc.core.yupi.example.common.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
@Slf4j
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Serializer serializer = SerilizerFactory.creatInstance(RpcAppliation.getRpcConfig().getSerilizer());
        String serviceName=method.getDeclaringClass().getName();
        RpcRequest request = RpcRequest.builder()
                .methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .parameterTypes(method.getParameterTypes()).build();
        // 序列化
        try {
            byte[] bytes = serializer.serialize(request);
            RpcConfig rpcConfig= RpcAppliation.getRpcConfig();
            Registry registry= RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(ConfigConstant.DEFALUT_SERVICE_VERSION);
                List<ServiceMetaInfo> serviceMetaInfoList=registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
                if(CollUtil.isEmpty(serviceMetaInfoList)){
                    throw new RuntimeException("暂时没发现服务地址");
                }
                //暂时先取第一个
                ServiceMetaInfo serviceMetaInfo1=serviceMetaInfoList.get(0);
            try (HttpResponse httpServerResponse = HttpRequest.post(serviceMetaInfo1.getServiceAddress())
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
