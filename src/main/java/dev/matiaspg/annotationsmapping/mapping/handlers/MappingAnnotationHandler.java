package dev.matiaspg.annotationsmapping.mapping.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.mapping.MappingContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public interface MappingAnnotationHandler<T> {
    Class<T> getSupportedAnnotation();

    default String getAnnotationName() {
        return "@" + getSupportedAnnotation().getSimpleName();
    }

    BiConsumer<JsonNode, Object> handleField(Field field, MappingContext<?> ctx);

    BiConsumer<JsonNode, Object> handleMethod(Method method, MappingContext<?> ctx);
}
