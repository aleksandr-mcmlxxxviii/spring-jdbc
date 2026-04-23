package com.mvc.springdatajdbc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Sortable {
    String columnName() default "";
    boolean enabled() default true;
    String defaultOrder() default "ASC";
    int priority() default 0;
}
