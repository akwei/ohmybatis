package com.github.akwei.ohmybatis.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface Like {

    /**
     * bean's field name
     */
    String value() default "";

    boolean left() default true;

    boolean right() default true;
}
