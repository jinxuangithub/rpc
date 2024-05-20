package com.sjw.shi.rpc.core.utils;

import cn.hutool.setting.dialect.Props;

public class ConfigUtils {
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configName = new StringBuilder("application");
        if (environment != null && !environment.equals("")) {
            configName.append("-" + environment);
        }
        configName.append(".properties");
        Props prop = new Props(configName.toString());
        return prop.toBean(tClass, prefix);
    }
}
