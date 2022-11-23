package com.springmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD) //只对方法有用
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented//文档注释用
public @interface ResponseBody {
    String value() default "";
}
