package com.nekonade.common.utils;

import com.nekonade.common.db.pojo.Mail;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class FunctionMapper {

    public static <T,R> Function<T,R> Mapper(Class<T> tClass,Class<R> rClass){
        return source -> {
            R r = null;
            try {
                r = rClass.getDeclaredConstructor().newInstance();
                Field[] fields = rClass.getDeclaredFields();
                for(Field field: fields){
                    field.setAccessible(true);
                    String name = field.getName();
                    String simpleName = field.getType().getSimpleName();
                    try {
                        Field getField = tClass.getDeclaredField(name);
                        getField.setAccessible(true);
                        if(getField.getType().getSimpleName().equals(simpleName)){
                            field.set(r,getField.get(source));
                        }
                    } catch (NoSuchFieldException ignored) {
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return r;
        };
//        ReflectionUtils.doWithFields(source.getClass(), new ReflectionUtils.FieldCallback() {
//            @Override
//            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
//                ReflectionUtils.makeAccessible(field);
//                // 如果字段添加了我们自定义的AutoIncKey注解
//                if (field.isAnnotationPresent(AutoIncKey.class)
//                        //判断注解的字段是否为number类型且值是否等于0.如果大于0说明有ID不需要生成ID
//                        && field.get(source) instanceof Number
//                        && field.getLong(source) == 0) {
//                    // 设置自增ID
//                    AutoIncKey annotation = field.getAnnotation(AutoIncKey.class);
//                    switch (annotation.use()){
//                        case "":
//                        default:
//                            field.set(source, getNextId(source.getClass().getCanonicalName()));
//                            break;
//                        case "redis":
//                            field.set(source, getNextIdByRedis(annotation.key(),annotation.id()));
//                            break;
//                    }
//                    logger.info("increase key, source = {} , nextId = {}", source, field.get(source));
//                }
//            }
//        });
    }
}
