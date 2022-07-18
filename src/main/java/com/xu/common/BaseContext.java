package com.xu.common;

public class BaseContext {
    /**
     *     TODO
     *     菜品：启售 删除 批量操作
     *     套餐：修改 批量操作
     */
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setValue(Long id){
        threadLocal.set(id);
    }

    public static Long getValue(){
        return threadLocal.get();
    }
}
