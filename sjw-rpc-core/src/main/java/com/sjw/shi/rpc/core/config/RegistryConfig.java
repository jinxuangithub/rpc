package com.sjw.shi.rpc.core.config;

import lombok.Data;

@Data
public class RegistryConfig {

    private String address = "http://localhost:2379";

    private String username;

    private String password;

    private long timeout = 10000l;

    private String registry = "etcd";


}
