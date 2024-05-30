package com.sjw.shi.rpc.core.utils;

import cn.hutool.setting.dialect.Props;

/**
 * 读取配置文件并返回配置对象
 * @author shijiawei
 * @date 2020/11/11 15:43
 */
public class ConfigUtils {
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 支持区分环境
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
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
