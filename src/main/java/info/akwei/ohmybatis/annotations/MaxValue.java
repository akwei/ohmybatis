package info.akwei.ohmybatis.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface MaxValue {
    /**
     * bean's field name
     */
    String value();

    boolean include() default true;
}
