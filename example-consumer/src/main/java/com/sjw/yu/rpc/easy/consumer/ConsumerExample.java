package com.sjw.yu.rpc.easy.consumer;

import com.sjw.shi.rpc.core.config.RpcConfig;
import com.sjw.shi.rpc.core.model.RpcRequest;
import com.sjw.shi.rpc.core.utils.ConfigUtils;

import static com.sjw.shi.rpc.core.constant.ConfigConstant.DEFAULT_CONFIG_PREFIX;

public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, DEFAULT_CONFIG_PREFIX);
        System.out.println(rpc);
    }
}
