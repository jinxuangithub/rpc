package com.sjw.yu.rpc.easy.provider;

import com.sjw.shi.rpc.core.RpcAppliation;
import com.sjw.shi.rpc.core.registry.LocalRegistry;
import com.sjw.shi.rpc.core.service.HttpServer;
import com.sjw.shi.rpc.core.service.impl.HttpServerImpl;
import com.sjw.shi.rpc.core.yupi.example.common.service.UserService;

public class EsayProviderExample {
    public static void main(String[] args) {
        RpcAppliation.init();
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        HttpServer httpServer=new HttpServerImpl();
        httpServer.doStart(RpcAppliation.getRpcConfig().getServerPort());

    }
}
