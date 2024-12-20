package com.homecloud.controller;
import com.homecloud.entity.enums.ResponseCodeEnum;
import com.homecloud.entity.vo.R;
import com.homecloud.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionHandlerController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(value = Exception.class)
    Object handleException(Exception e, HttpServletRequest request) {
        logger.error("REQUEST URL error:", request.getRequestURL(), e);
        R r = new R();
        r.setStatus(STATUS_ERROR);
        //404
        if (e instanceof NoHandlerFoundException) {
            r.setCode(ResponseCodeEnum.CODE_404.getCode());
            r.setInfo(ResponseCodeEnum.CODE_404.getMsg());
        } else if (e instanceof BusinessException) {
            BusinessException bizException = (BusinessException) e;
            r.setCode(bizException.getCode() == null ? ResponseCodeEnum.CODE_600.getCode() : bizException.getCode());
            r.setInfo(bizException.getMessage());
        } else if (e instanceof BindException|| e instanceof MethodArgumentTypeMismatchException) {
            r.setCode(ResponseCodeEnum.CODE_600.getCode());
            r.setInfo(ResponseCodeEnum.CODE_600.getMsg());
        } else if (e instanceof DuplicateKeyException) {
            r.setCode(ResponseCodeEnum.CODE_601.getCode());
            r.setInfo(ResponseCodeEnum.CODE_601.getMsg());
        } else {
            r.setCode(ResponseCodeEnum.CODE_500.getCode());
            r.setInfo(ResponseCodeEnum.CODE_500.getMsg());
        }
        return r;
    }
}
