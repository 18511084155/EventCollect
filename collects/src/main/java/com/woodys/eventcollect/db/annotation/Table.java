package com.woodys.eventcollect.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cz on 16/3/4.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String value() default "";
    String primaryKey() default "";//标记主键
    boolean autoIncrement() default false;//主键自增长
    boolean exported() default false;//是否暴露
}
