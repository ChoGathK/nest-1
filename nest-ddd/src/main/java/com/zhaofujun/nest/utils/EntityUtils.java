package com.zhaofujun.nest.utils;

import com.zhaofujun.nest.CustomException;
import com.zhaofujun.nest.context.ServiceContext;
import com.zhaofujun.nest.context.ServiceContextManager;
import com.zhaofujun.nest.context.loader.EntityMethodInterceptor;
import com.zhaofujun.nest.context.model.Entity;
import com.zhaofujun.nest.context.model.Role;
import com.zhaofujun.nest.core.DomainObject;
import com.zhaofujun.nest.core.Identifier;
import com.zhaofujun.nest.SystemException;
import net.sf.cglib.proxy.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;


///**
// * 实体工具
// * Created by Jove on 2017/1/9.
// */
public class EntityUtils {
    //
    private static Logger logger = LoggerFactory.getLogger(EntityUtils.class);


    public static void setValue(Class clazz, Entity entityObject, String fieldName, Object value) {
        try {
            Field declaredField = clazz.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(entityObject, value);
        } catch (Exception ex) {
            logger.warn("设置属性失败", ex);
        }
    }


    //修改实体为加载状态，加载状态的实体属性发生变化时不提交到工作单元
    public static void setLoading(Entity entityObject, boolean loading) {

        try {
            Field field = Entity.class.getDeclaredField("__loading");
            field.setAccessible(true);
            field.set(entityObject, loading);
        } catch (Exception e) {
            throw new SystemException("更新实体加载状态失败", e);
        }

    }


//    public static <T extends Entity> void loading(T entityObject, Consumer<T> consumer){
//        try {
//            Field field = Entity.class.getDeclaredField("__loading");
//            field.setAccessible(true);
//            field.set(entityObject, true);
//            consumer.accept(entityObject);
//            field.set(entityObject, false);
//        }catch (CustomException ex){
//            throw ex;
//        }catch (SystemException ex){
//            throw  ex;
//        } catch (Exception ex){
//            throw new SystemException("更新实体加载状态失败", ex);
//        }
//    }


    public static boolean isLoading(Entity entityObject) {
        return entityObject.is__loading();
//        try {
//            Field field = Entity.class.getDeclaredField("__loading");
//            field.setAccessible(true);
//            return field.getBoolean(entityObject);
//        } catch (Exception e) {
//            return false;
//        }
    }


    public static void setIdentifier(Entity entityObject, Identifier identifier) {
        try {
            Field field = Entity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(entityObject, identifier);
        } catch (Exception e) {
            throw new SystemException("设置实体标识失败", e);
        }
    }

    public static boolean isRepresented(Entity entityObject) {
        try {
            Field field = entityObject.getClass().getDeclaredField("CGLIB$BOUND");
            field.setAccessible(true);
            return field.getBoolean(entityObject);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNewInstance(Entity entityObject) {
        return entityObject.is__newInstance();
//        try {
//            Field field = Entity.class.getDeclaredField("__newInstance");
//            field.setAccessible(true);
//            return field.getBoolean(entityObject);
//        } catch (Exception e) {
//            return false;
//        }
    }

    public static void setNewInstance(Entity entityObject, boolean newInstance) {
        try {
            Field field = Entity.class.getDeclaredField("__newInstance");
            field.setAccessible(true);
            field.set(entityObject, newInstance);
        } catch (Exception e) {
            throw new SystemException("设置实体是否为新实例失败", e);
        }
    }


    public static boolean isChanged(Entity entityObject) {
        return entityObject.is__changed();
//        try {
//            Field field = Entity.class.getDeclaredField("__changed");
//            field.setAccessible(true);
//            return field.getBoolean(entityObject);
//        } catch (Exception e) {
//            return false;
//        }
    }

    public static void setChanged(Entity entityObject, boolean changed) {
        try {
            Field field = Entity.class.getDeclaredField("__changed");
            field.setAccessible(true);
            field.set(entityObject, changed);
        } catch (Exception e) {
            throw new SystemException("设置实体发生变更", e);
        }
    }
//
//    public static void invokeVerify(Entity entityObject) {
//
//        try {
//            Method verifyMethod = Entity.class.getDeclaredMethod("verify");
//            verifyMethod.setAccessible(true);
//            verifyMethod.invoke(entityObject);
//        } catch (CustomException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new SystemException("验证实体时发生错误", e);
//        }
//    }

    public static String getClassName(DomainObject entityObject) {
        final String PROXY_SPLIT_STR = "$";
        final int BEGIN_INDEX = 0;

        String className = entityObject.getClass().getSimpleName();
        int endIndex = className.indexOf(PROXY_SPLIT_STR);
        return endIndex == -1 ? className : className.substring(BEGIN_INDEX, endIndex);
    }

    public static String getFullClassName(DomainObject entityObject) {
        return entityObject.getClass().getPackage().getName() + "." + getClassName(entityObject);
    }

}