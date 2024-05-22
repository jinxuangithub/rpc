package com.sjw.shi.rpc.core.registry;

import cn.hutool.json.JSONUtil;
import com.sjw.shi.rpc.core.config.RegistryConfig;
import com.sjw.shi.rpc.core.yupi.example.common.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;


public class EtcdRegistry implements Registry {
    private Client client;
    private KV kvClient;
    //根节点
    public static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();


    }

    /**
     * @param serviceMetaInfo
     * @throws Exception
     * @Descrobe 注册节点
     */

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //设置租约客户端
        Lease lease = client.getLeaseClient();
        //设置失效时间为30s
        long leaseId = lease.grant(30).get().getID();
        //设置服务的key和value
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();


    }

    /**
     * @describe 删除节点
     **/
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {

        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));

    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        String searchPrefix = ETCD_ROOT_PATH + serviceKey;
        try {
            ByteSequence byteSequence = ByteSequence.from(searchPrefix, StandardCharsets.UTF_8);
            GetOption getOption = GetOption.builder().isPrefix(true).build();
           List<KeyValue> keyValues = kvClient.get(byteSequence, getOption).get().getKvs();

           return keyValues.stream().map(keyValue->{
               String value=keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);})
                   .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    @Override
    public void heartBeat() {


    }

    @Override
    public void watch(String serviceNodeKey) {

    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        if(kvClient!=null){
            kvClient.close();
        }
        if(client!=null){
            client.close();
        }

    }
}
