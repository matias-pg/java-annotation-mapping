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

    /**
     * Creates a function that maps a node value to a field of a class instance.
     *
     * @param field The field where the mapped value will be set
     * @param ctx   Mapping context
     * @return The function
     */
    BiConsumer<JsonNode, Object> createFieldMapper(Field field, MappingContext<?> ctx);

    /**
     * Creates a function that maps a node value to a method of a class instance.
     *
     * @param method The method that will be called with the mapped value
     * @param ctx    Mapping context
     * @return The function
     */
    BiConsumer<JsonNode, Object> createMethodMapper(Method method, MappingContext<?> ctx);
}
