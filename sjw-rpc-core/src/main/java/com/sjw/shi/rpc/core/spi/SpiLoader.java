package com.sjw.shi.rpc.core.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ClassUtil;
import com.sjw.shi.rpc.core.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {
    private static Map<String, Map<String, Class<?>>> loadMap = new ConcurrentHashMap<>();

    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";
    private static final String RPC_CUSTOMER_SPI_DIR = "META-INF/rpc/customer/";
    private static String[] ScanDirs = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOMER_SPI_DIR};
    private static final List<Class<?>> SPI_CLASS_LIST = Arrays.asList(Serializer.class);

    public static void loadAll() {
        log.info("load all spi class");
        for (Class<?> clazz : SPI_CLASS_LIST) {
            load(clazz);
        }

    }

    public static <T> T getInstance(Class<T> tclass, String key) {
        String tClassName = tclass.getName();
        Map<String, Class<?>> classMap = loadMap.get(tClassName);
        if (classMap == null) {
            throw new RuntimeException(String.format("Spi 加载器未加载 %s类型", tClassName));
        }
        if (!classMap.containsKey(key)) {
            throw new RuntimeException(String.format("Spi 加载器 %s不存在key= %s类型", tClassName, key));
        }
        Class<?> clazz = classMap.get(key);
        String className = clazz.getName();
        if (!instanceCache.containsKey(className)) {

            try {
                instanceCache.put(className, (T) clazz.newInstance());
            } catch (Exception e) {
                String errorMessage = String.format("Spi 加载器 %s 实例化失败", className);
                throw new RuntimeException(errorMessage, e);
            }
        }
        return (T) instanceCache.get(className);
    }

    public static Map<String, Class<?>> load(Class<?> tclass) {
        log.info("load spi class is {}", tclass.getName());
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String dir : ScanDirs) {
            List<URL> urls = ResourceUtil.getResources(dir + tclass.getName());
            for (URL url : urls) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] split = line.split("=");
                        if (split.length == 2) {
                            String key = split[0];
                            Class<?> value = Class.forName(split[1]);
                            keyClassMap.put(key, value);
                        }
                    }
                } catch (IOException e) {
                    log.error("load spi class error", e);
                } catch (Exception e) {
                    log.info("load spi class error", e);
                }
            }
        }
        loadMap.put(tclass.getName(), keyClassMap);
        return keyClassMap;

    }
}
