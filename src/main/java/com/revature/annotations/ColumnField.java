package com.revature.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RUNTIME)
public @interface ColumnField {
    String columnName();

    boolean pkey() default false;

    boolean isSerial() default false;

    boolean canBeNull() default true;

    boolean fkey() default false;

    Class<?> fClass() default Object.class;

    boolean unique() default false;
}
