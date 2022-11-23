package com.springmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD) //只对方法有用
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented//文档注释用
public @interface RequestMapping {
    String value() default "";
    String method() default "GET";
    static final String DELETE="DELETE";
    static final String GET="GET";
    static final String PUT="PUT";
    static final String POST="POST";
}
