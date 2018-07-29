package info.akwei.ohmybatis.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface Like {

    String value() default "";

    boolean left() default true;

    boolean right() default true;
}
