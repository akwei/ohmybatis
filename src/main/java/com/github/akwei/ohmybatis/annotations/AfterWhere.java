package com.github.akwei.ohmybatis.annotations;

import java.lang.annotation.*;

/**
 * sometimes select have order by, group by etc sql, we can use @AfterWhere to help us for these situation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AfterWhere {

    /**
     * value must use table's column name
     */
    String value();
}
