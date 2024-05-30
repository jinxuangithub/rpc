package com.sjw.shi.rpc.core;

import com.sjw.shi.rpc.core.config.RegistryConfig;
import com.sjw.shi.rpc.core.config.RpcConfig;
import com.sjw.shi.rpc.core.registry.Registry;
import com.sjw.shi.rpc.core.registry.RegistryFactory;
import com.sjw.shi.rpc.core.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

import static com.sjw.shi.rpc.core.constant.ConfigConstant.DEFAULT_CONFIG_PREFIX;

@Slf4j
public class RpcAppliation {
    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newrpcConfig) {
        rpcConfig = newrpcConfig;
        log.info("rpc  init ,config is {}", rpcConfig.toString());
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry  init ,config is {}", registryConfig);
        Runtime.getRuntime()
                .addShutdownHook(new Thread(
                       registry::destroy
                ));
    }

    public static void init() {
        RpcConfig newrpcConfig;
        try {
            newrpcConfig = ConfigUtils.loadConfig(RpcConfig.class, DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            newrpcConfig = new RpcConfig();
        }
        init(newrpcConfig);
    }

    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcAppliation.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
