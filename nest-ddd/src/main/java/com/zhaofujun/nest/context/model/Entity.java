package com.zhaofujun.nest.context.model;

import com.zhaofujun.nest.NullException;
import com.zhaofujun.nest.context.ServiceContextManager;
import com.zhaofujun.nest.context.loader.ConstructEntityLoader;
import com.zhaofujun.nest.context.loader.RepositoryEntityLoader;
import com.zhaofujun.nest.core.DomainObject;
import com.zhaofujun.nest.core.Identifier;
import com.zhaofujun.nest.utils.EntityUtils;

import javax.validation.NoProviderFoundException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.io.Serializable;

public abstract class Entity<T extends Identifier> extends DomainObject implements Serializable {
    protected T id;

    public T getId() {
        return id;
    }


    public <U extends Role> U act(Class<U> clazz, Identifier identifier) {
        if (identifier == null)
            throw new NullException("角色的ID不能为空");
        U u = new RepositoryEntityLoader<U>(clazz).create(identifier);
        if (u == null) {
            u = new ConstructEntityLoader<U>(clazz).create(identifier);
            if (Role.class.isInstance(u)) {
                EntityUtils.setValue(Role.class, u, "actor", this);
            }
        }
        return u;
    }

    public <U extends Role> U act(Class<U> clazz) {
        return act(clazz, this.getId());
    }


    //是否处于加载中状态
    private boolean __loading;

    //是否是新增的对象
    private boolean __newInstance;

    //是否有变更
    private boolean __changed;

    //版本号
    private int _version;

    public boolean is__loading() {
        return __loading;
    }

    public boolean is__newInstance() {
        return __newInstance;
    }

    public boolean is__changed() {
        return __changed;
    }

    public int get_version() {
        return _version;
    }

    //    private void updateEntityObject() {
//        if (verify()) {
//            ServiceContext.getCurrent()
//                    .getContextUnitOfWork().updateEntityObject(this);
//        } else {
//            throw new VerifyFailedException("验证实体失败");
//        }
//    }
//
//    private void newEntityObject() {
//        if (verify()) {
//            ServiceContext.getCurrent()
//                    .getContextUnitOfWork().addEntityObject(this);
//        } else {
//            throw new VerifyFailedException("验证实体失败");
//        }
//    }


    public void verify() {
        ValidatorFactory validatorFactory;
        try {
            validatorFactory = Validation.buildDefaultValidatorFactory();
        } catch (NoProviderFoundException exception) {
            return;
        }
        Object[] strings = validatorFactory
                .getValidator()
                .validate(this)
                .stream()
                .map(p -> p.getMessage())
                .toArray();

        if (strings.length > 0)
            throw new VerifyFailedException("对象验证失败", strings);
    }

    public void delete() {
        if (!this.__newInstance) //  不是新对象的时候才需要调用移除操作
        {
            this.__changed = true;
            ServiceContextManager.getCurrent().getContextUnitOfWork().removeEntityObject(this);
        }
    }

//    @Override
//    public int hashCode() {
//        return EntityUtils.getClassName(this).hashCode() + this.id.hashCode();
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//
//        if (Entity.class.isAssignableFrom(obj.getClass())) {
//            Entity entity = (Entity) obj;
//            return this.id.equals(entity.id) && EntityUtils.getClassName(entity).equals(EntityUtils.getClassName(this));
//        }
//        return false;
//    }
}

