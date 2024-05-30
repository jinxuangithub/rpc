package com.sjw.shi.rpc.core.registry;

import com.sjw.shi.rpc.core.yupi.example.common.model.ServiceMetaInfo;

import java.util.ArrayList;
import java.util.List;

public class RegistryServiceCache {
    /**
     * 注册中心服务本地缓存
     */
    List<ServiceMetaInfo> serviceCache=new ArrayList<>();

    /**
     *  写入缓存
     * @param serviceMetaInfos
     */
    void writeCache( List<ServiceMetaInfo> serviceMetaInfos){
        this.serviceCache=serviceMetaInfos;
    }

    /**
     *  读取缓存
     * @return
     */
    List<ServiceMetaInfo>readCache(){
        return this.serviceCache;
    }

    /**
     *  清除缓存
     */
    void clearCache(){
        this.serviceCache=null;
    }
}
