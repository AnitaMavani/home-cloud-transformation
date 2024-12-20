package com.homecloud.aspect;

import com.homecloud.aspect.annotation.Interceptor;
import com.homecloud.aspect.annotation.ValidateParam;
import com.homecloud.entity.enums.ResponseCodeEnum;
import com.homecloud.exception.BusinessException;
import com.homecloud.utils.StringUtil;
import com.homecloud.utils.VerifyUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


@Component("operationAspect")
@Aspect
public class AdviceAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdviceAspect.class);
    private static final String TYPE_STRING = "java.lang.String";
    private static final String TYPE_INTEGER = "java.lang.Integer";
    private static final String TYPE_LONG = "java.lang.Long";

    @Pointcut("@annotation(com.homecloud.aspect.annotation.Interceptor)")
    private void requestInterceptor() {
    }

    @Before("requestInterceptor()")
    public void performInterception(JoinPoint point) throws BusinessException {
        try {
            executeValidation(point);
        } catch (BusinessException e) {
            LOGGER.error("Interception failed", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Interception failed", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }

    private void executeValidation(JoinPoint point) throws NoSuchMethodException {
        Object target = point.getTarget();
        Object[] args = point.getArgs();
        String methodName = point.getSignature().getName();
        Class<?>[] paramTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        Method method = target.getClass().getMethod(methodName, paramTypes);
        Interceptor interceptor = method.getAnnotation(Interceptor.class);

        if (interceptor != null && interceptor.checkInput()) {
            validateParameters(method, args);
        }
    }

    private void validateParameters(Method method, Object[] args) throws BusinessException {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            validateParameter(parameters[i], args[i]);
        }
    }

    private void validateParameter(Parameter parameter, Object value) {
        ValidateParam validateParam = parameter.getAnnotation(ValidateParam.class);
        if (validateParam == null) return;

        String typeName = parameter.getParameterizedType().getTypeName();
        if (isPrimitiveType(typeName)) {
            checkPrimitiveValue(value, validateParam);
        } else {
            validateObjectValue(parameter, value);
        }
    }

    private boolean isPrimitiveType(String typeName) {
        return TYPE_STRING.equals(typeName) || TYPE_LONG.equals(typeName) || TYPE_INTEGER.equals(typeName);
    }

    private void validateObjectValue(Parameter parameter, Object value) {
        try {
            Class<?> clazz = Class.forName(parameter.getParameterizedType().getTypeName());
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                ValidateParam fieldValidateParam = field.getAnnotation(ValidateParam.class);
                if (fieldValidateParam != null) {
                    Object fieldValue = field.get(value);
                    checkPrimitiveValue(fieldValue, fieldValidateParam);
                }
            }
        } catch (BusinessException e) {
            LOGGER.error("Parameter validation failed", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Parameter validation failed", e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    private void checkPrimitiveValue(Object value, ValidateParam validateParam) throws BusinessException {
        boolean isEmpty = value == null || StringUtil.isEmpty(value.toString());
        int length = value == null ? 0 : value.toString().length();

        if (isEmpty && validateParam.required()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (!isEmpty) {
            checkLengthConstraints(length, validateParam);
            checkRegexConstraints(value, validateParam);
        }
    }

    private void checkLengthConstraints(int length, ValidateParam validateParam) throws BusinessException {
        if (lengthExceedsMax(length, validateParam) || lengthBelowMin(length, validateParam)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    private boolean lengthExceedsMax(int length, ValidateParam validateParam) {
        return validateParam.max() != -1 && validateParam.max() < length;
    }

    private boolean lengthBelowMin(int length, ValidateParam validateParam) {
        return validateParam.min() != -1 && validateParam.min() > length;
    }

    private void checkRegexConstraints(Object value, ValidateParam validateParam) throws BusinessException {
        if (!StringUtil.isEmpty(validateParam.regex().getRegex()) && !VerifyUtils.verify(validateParam.regex(), String.valueOf(value))) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
}
