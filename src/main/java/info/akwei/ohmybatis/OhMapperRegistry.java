package info.akwei.ohmybatis;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;

public class OhMapperRegistry extends MapperRegistry {

    private MapperRegistry mapperRegistry;

    public OhMapperRegistry(Configuration config, MapperRegistry mapperRegistry) {
        super(config);
        this.mapperRegistry=mapperRegistry;
    }

}
