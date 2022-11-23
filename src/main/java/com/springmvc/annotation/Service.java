package com.springmvc.annotation;


import java.lang.annotation.*;

@Target(ElementType.TYPE) //只对类有用
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented//文档注释用
public @interface Service {
    String value() default "";
}
