package com.sjw.shi.rpc.core.registry;

import com.sjw.shi.rpc.core.config.RegistryConfig;
import com.sjw.shi.rpc.core.yupi.example.common.model.ServiceMetaInfo;
import io.etcd.jetcd.*;

import java.util.List;


public class EtcdRegistry implements Registry {
  private   Client client;
  private KV kvClient;
  public static final String ETCD_ROOT_PATH="/rpc/";

  @Override
  public void init(RegistryConfig registryConfig) {

  }

  @Override
  public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {

  }

  @Override
  public void unRegister(ServiceMetaInfo serviceMetaInfo) {

  }

  @Override
  public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
    return null;
  }

  @Override
  public void heartBeat() {

  }

  @Override
  public void watch(String serviceNodeKey) {

  }

  @Override
  public void destroy() {

  }
}
