package com.xu.exception;

public class BusinessException extends RuntimeException{
    private Integer code; //编码：1成功，0和其它数字为失败

    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }
}
