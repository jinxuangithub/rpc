package com.sjw.shi.rpc.core.model;

import com.sjw.shi.rpc.core.config.RpcConfig;
import com.sjw.shi.rpc.core.constant.ConfigConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {
    /**
     * 接口名
     */
    private String interfaceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 参数列表
     */
    private Object[] parameters;

    /**
     * 服务版本
     */
    private String version = ConfigConstant.DEFALUT_SERVICE_VERSION;


}
