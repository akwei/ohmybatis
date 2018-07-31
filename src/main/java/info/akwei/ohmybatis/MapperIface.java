package info.akwei.ohmybatis;

import info.akwei.ohmybatis.sqlprovider.SimpleSQLProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface MapperIface<T> {

    @UpdateProvider(type = SimpleSQLProvider.class, method = SimpleSQLProvider.UPDATE_OBJ)
    int updateObj(T t, T old);
//
//    @DeleteProvider(type = EntitySQLProvider.class, method = EntitySQLProvider.DELETE)
//    int deleteObj(Object... keys);
//
//    @SelectProvider(type = EntitySQLProvider.class, method = EntitySQLProvider.SELECT)
//    T getById(Object... objId);
}
