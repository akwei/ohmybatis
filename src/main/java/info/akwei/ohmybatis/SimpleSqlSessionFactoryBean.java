package info.akwei.ohmybatis;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ApplicationEvent;

public class SimpleSqlSessionFactoryBean extends SqlSessionFactoryBean {


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
            SimpleMapperAnnotationBuilder builder = new SimpleMapperAnnotationBuilder(this.configuration, aClass);
            builder.parse();
        }
    }
}
