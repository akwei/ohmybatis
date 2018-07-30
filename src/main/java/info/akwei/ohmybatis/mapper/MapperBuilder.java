package info.akwei.ohmybatis.mapper;

import org.apache.ibatis.session.Configuration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class MapperBuilder implements InitializingBean {

    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    private Configuration configuration;

    private String basePackage;

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        this.configuration.getMappedStatements()
    }
}
