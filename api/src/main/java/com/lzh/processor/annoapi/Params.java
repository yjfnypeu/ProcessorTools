package com.lzh.processor.annoapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Administrator
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Params {
    Field[] fields() default {};
}
