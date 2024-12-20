package com.homecloud.exception;
import com.homecloud.entity.enums.ResponseCodeEnum;

import java.io.IOException;


public class BusinessException extends RuntimeException {

    private ResponseCodeEnum codeEnum;
    private Integer code;
    private String message;


    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(ResponseCodeEnum codeEnum) {
        super(codeEnum.getMsg());
        this.codeEnum = codeEnum;
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMsg();
    }



    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
