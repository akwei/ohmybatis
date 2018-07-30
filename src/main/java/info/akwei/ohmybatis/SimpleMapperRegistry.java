package info.akwei.ohmybatis;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;

public class SimpleMapperRegistry extends MapperRegistry {

    private MapperRegistry mapperRegistry;

    public SimpleMapperRegistry(Configuration config, MapperRegistry mapperRegistry) {
        super(config);
        this.mapperRegistry=mapperRegistry;
    }

}
