package info.akwei.ohmybatis.annotations;

import java.lang.annotation.*;

/**
 * mark as value field that be update
 * for example:
 * <pre>
 * method:
 *      void update(int id, @UpdateField Sring name)
 *      if name with @UpdateField, then build sql as: update table set name=#{arg} where id=#{id}
 *      else will throw exception for no column be update
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface UpdateField {

}
