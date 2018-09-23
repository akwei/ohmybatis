package com.github.akwei.ohmybatis;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.builder.annotation.MethodResolver;

import java.lang.reflect.Method;

/**
 * @author Eduardo Macarron
 */
public class OhMethodResolver extends MethodResolver {

    private OhMapperAnnotationBuilder annotationBuilder;

    private Method method;

    public OhMethodResolver(MapperAnnotationBuilder annotationBuilder, Method method) {
        super(annotationBuilder, method);
        this.annotationBuilder = (OhMapperAnnotationBuilder) annotationBuilder;
        this.method = method;
    }

    public void resolve() {
        annotationBuilder.parseStatement(method);
    }

}