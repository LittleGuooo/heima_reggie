package com.xu.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        //设置创建和更新时间
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        //设置创建和更新人id
        Long id = BaseContext.getValue();
        metaObject.setValue("createUser", id);
        metaObject.setValue("updateUser", id);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //设置更新时间和人id
        Long id = BaseContext.getValue();
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", id);
    }
}
