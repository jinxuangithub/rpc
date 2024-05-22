package com.sjw.shi.rpc.core.registry;

import com.sjw.shi.rpc.core.spi.SpiLoader;

public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }

    private static final Registry REGISTRY = new EtcdRegistry();

    public static Registry getRegistry(String key) {
        return SpiLoader.getInstance(Registry.class, key);

    }
}
