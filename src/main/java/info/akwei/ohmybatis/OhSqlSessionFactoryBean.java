package info.akwei.ohmybatis;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ApplicationEvent;

public class OhSqlSessionFactoryBean extends SqlSessionFactoryBean {


    private Configuration configuration;

    @Override
    public void setConfiguration(Configuration configuration) {
        super.setConfiguration(configuration);
        this.configuration = configuration;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        super.onApplicationEvent(event);
        MapperRegistry mapperRegistry = this.configuration.getMapperRegistry();
        for (Class<?> aClass : mapperRegistry.getMappers()) {
            OhMapperAnnotationBuilder builder = new OhMapperAnnotationBuilder(this.configuration, aClass);
            builder.parse();
        }
    }
}
