package info.akwei.ohmybatis.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface MaxValue {

    String value();

    boolean include() default true;
}
