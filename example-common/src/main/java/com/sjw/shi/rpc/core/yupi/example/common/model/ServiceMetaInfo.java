package com.sjw.shi.rpc.core.yupi.example.common.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class ServiceMetaInfo {
    //服务名称
    private String serviceName;
   //服务版本
    private String serviceVersion="1.0";
   //服务分组
    private String serviceGroup;
    //服务地址
    private String serviceHost;
    //服务端口
    private int servicePort;
    //获取服务键名
    public String getServiceKey(){
        return String.format("%s:%s",serviceName,serviceVersion);
    }
    //获取服务注册节点键名
    public String getServiceNodeKey() {
       return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }
    public String getServiceAddress() {
        if(!StrUtil.contains(serviceHost,"http")){
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }

}
