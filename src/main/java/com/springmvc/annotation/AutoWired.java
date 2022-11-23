package com.springmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD) //只对域有用
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented//文档注释用
public @interface AutoWired {
    String value() default "";

}
