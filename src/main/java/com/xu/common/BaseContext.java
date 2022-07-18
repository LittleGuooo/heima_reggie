package com.xu.common;

public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setValue(Long id){
        threadLocal.set(id);
    }

    public static Long getValue(){
        return threadLocal.get();
    }
}
