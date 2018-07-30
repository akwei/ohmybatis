package info.akwei.ohmybatis.mapper;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class OhMapperScannerConfigurer extends MapperScannerConfigurer {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        super.postProcessBeanDefinitionRegistry(registry);
    }
}
