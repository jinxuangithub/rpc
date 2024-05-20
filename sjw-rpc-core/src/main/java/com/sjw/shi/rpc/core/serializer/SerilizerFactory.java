package com.sjw.shi.rpc.core.serializer;

import com.sjw.shi.rpc.core.spi.SpiLoader;



public class SerilizerFactory {
    static {
        SpiLoader.load(Serializer.class);
    }


    public static final Serializer DEFAULT_SERIALIZER = new JDKSerializer();

    public static Serializer creatInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }

}
