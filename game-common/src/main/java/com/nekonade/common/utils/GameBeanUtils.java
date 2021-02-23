package com.nekonade.common.utils;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameBeanUtils {
    private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    private static final Map<String, MapperFacade> mapperFacadeCache = new ConcurrentHashMap<>();

    public static void shallowCopy(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }

    public static <T> T deepCopy(Object source, Class<T> clazz) {
        MapperFacade mapper = mapperFacadeCache.get(clazz.getName());
        if (mapper == null) {
            mapperFactory.classMap(clazz, clazz).byDefault().register();
            mapper = mapperFactory.getMapperFacade();
        }
        T newObj = mapper.map(source, clazz);
        return newObj;
    }

    public static <T> T deepCopyByJson(Object source, Class<T> clazz) {
        String json = JSON.toJSONString(source);
        return JSON.parseObject(json, clazz);
    }

    @SneakyThrows
    public static <T> T deepCopyByJackson(Object source, Class<T> clazz) {
        String json = JacksonUtils.toJsonString(source);
        return JacksonUtils.parseObject(json,clazz);
    }
}
