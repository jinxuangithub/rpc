package com.sjw.yu.rpc.easy.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {
    public static final Map<String ,Class<?>>map=new ConcurrentHashMap<>();

    //注册
    public static void register(String key,Class<?> clazz){
        map.put(key,clazz);
    }
    public  static Class<?> get(String key){
       return map.get(key);
    }
    public static void remove(String key){
        map.remove(key);
    }
}
