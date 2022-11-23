package com.springmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER) //只对变量有用
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented//文档注释用
public @interface PathVariable {
    String value() default "";

}
