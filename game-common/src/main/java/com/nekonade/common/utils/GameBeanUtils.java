package com.nekonade.common.utils;

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
}
