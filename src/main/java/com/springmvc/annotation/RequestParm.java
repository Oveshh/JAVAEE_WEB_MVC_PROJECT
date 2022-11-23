package com.springmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER) //只对函数参数有用
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented//文档注释用
public @interface RequestParm {
    String value() default "";
}
