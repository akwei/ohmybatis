package info.akwei.ohmybatis.sqlprovider;

import net.sf.cglib.beans.BeanCopier;

public class EntityCopier {

    public static <T> void copy(T from, T to) {
        BeanCopier beanCopier = BeanCopier.create(from.getClass(), to.getClass(), false);
        beanCopier.copy(from, to, null);
    }

}
