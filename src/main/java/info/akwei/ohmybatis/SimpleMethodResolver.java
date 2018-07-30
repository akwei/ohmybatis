package info.akwei.ohmybatis;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.builder.annotation.MethodResolver;

import java.lang.reflect.Method;

/**
 * @author Eduardo Macarron
 */
public class SimpleMethodResolver extends MethodResolver {

    private SimpleMapperAnnotationBuilder annotationBuilder;

    private Method method;

    public SimpleMethodResolver(MapperAnnotationBuilder annotationBuilder, Method method) {
        super(annotationBuilder, method);
        this.annotationBuilder = (SimpleMapperAnnotationBuilder) annotationBuilder;
        this.method = method;
    }

    public void resolve() {
        annotationBuilder.parseStatement(method);
    }

}