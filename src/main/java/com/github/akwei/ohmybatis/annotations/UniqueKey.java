package com.github.akwei.ohmybatis.annotations;


import java.lang.annotation.*;

/**
 * 表示字段对应的column是unique key
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UniqueKey {

}
