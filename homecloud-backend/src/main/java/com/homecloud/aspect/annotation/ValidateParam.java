package com.homecloud.aspect.annotation;


import com.homecloud.entity.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface ValidateParam {

    VerifyRegexEnum regex() default VerifyRegexEnum.NO;

    int min() default -1;

    int max() default -1;

    boolean required() default false;
}
