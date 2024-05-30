package com.sjw.shi.rpc.core.registry;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.sjw.shi.rpc.core.config.RegistryConfig;
import com.sjw.shi.rpc.core.yupi.example.common.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.vertx.core.impl.ConcurrentHashSet;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class EtcdRegistry implements Registry {
    private Client client;
    private KV kvClient;
    //根节点
    public static final String ETCD_ROOT_PATH = "/rpc/";

    private final Set<String> localRegistNodeKeySet = new HashSet<>();

    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        heartBeat();
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
        localRegistNodeKeySet.add(registerKey);


    }

    /**
     * @describe 删除节点
     **/
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {

        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
        localRegistNodeKeySet.remove(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //优先从缓存中获取服务
        List<ServiceMetaInfo> cacheServiceMetaInfoList =
                registryServiceCache.readCache();
        if (cacheServiceMetaInfoList != null) {
            return cacheServiceMetaInfoList;
        }
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            ByteSequence byteSequence = ByteSequence.from(searchPrefix, StandardCharsets.UTF_8);
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(byteSequence, getOption).get().getKvs();

            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream().map(keyValue -> {
                        String key
                                = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        //监视key的变化
                        watch(key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;

        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }


    }

    @Override
    public void heartBeat() {

        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                for (String key : localRegistNodeKeySet) {
                    List<KeyValue> keyValues = null;
                    try {
                        keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        if (keyValues.isEmpty()) {
                            continue;
                        }
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "服务续签失败", e);
                    }


                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        //如果从未被监听就开启监听
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent watchEvent : response.getEvents()) {
                    switch (watchEvent.getEventType()) {
                        case DELETE:
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });

        }

    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        for (String key : localRegistNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException(key + "服务下线失败", e);
            }
        }
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }

    }
}
