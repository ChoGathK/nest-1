package com.zhaofujun.nest.context.loader;


import com.zhaofujun.nest.context.model.Entity;
import com.zhaofujun.nest.utils.EntityUtils;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EntityMethodInterceptor implements MethodInterceptor, Serializable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Set<String> withoutMethod = new HashSet<>(Arrays.asList(
            "hashCode", "equals", "toString", "notify",
            "notifyAll", "wait", "act", "verify", "findRoles",
            "delete", "is__loading", "is__newInstance", "is__changed", "get_version"));

    // 实现MethodInterceptor接口方法
    public Object intercept(Object obj, Method method, Object[] args,
                            MethodProxy proxy) throws Throwable {

        Object result = proxy.invokeSuper(obj, args);

        if (!Modifier.isPublic(method.getModifiers())) return result;

        String methodName = method.getName();
        if (withoutMethod.contains(methodName)) return result;

        if (methodName.startsWith("get") || methodName.startsWith("is")) return result;

        Entity entity = (Entity) obj;
        if (!EntityUtils.isLoading(entity)) {
            if (!EntityUtils.isChanged(entity))
                EntityUtils.setChanged(entity, true);
            //调用实体验证方法
            //entity.verify();
            logger.debug("领域实体发生更改，调用方法{}", method.getName());
        }

        return result;
    }

}