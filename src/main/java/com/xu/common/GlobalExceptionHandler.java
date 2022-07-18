package com.xu.common;

import com.xu.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> doException(SQLIntegrityConstraintViolationException ex) {
        log.info(ex.getMessage());
        return Result.error("用户名重复！");
    }

    @ExceptionHandler(BusinessException.class)
    public Result<String> doException1(BusinessException ex) {
        log.info(ex.getMessage());
        return Result.error(ex.getMessage());
    }
}
