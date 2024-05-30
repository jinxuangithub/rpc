package com.sjw.shi.rpc.core.config;

import com.sjw.shi.rpc.core.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {

    /**
     * 服务端端口
     */
    private int serverPort = 8080;

    /**
     * 注册中心地址
     */
    private String serverHost = "localhost";

    /**
     * 服务名
     */
    private String name = "sjwRpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    private boolean mock = false;

    private String serilizer = SerializerKeys.JDK;

    private RegistryConfig registryConfig = new RegistryConfig();
}
