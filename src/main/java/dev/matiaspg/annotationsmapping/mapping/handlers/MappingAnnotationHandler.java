package dev.matiaspg.annotationsmapping.mapping.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.mapping.MappingContext;
import dev.matiaspg.annotationsmapping.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface MappingAnnotationHandler<T extends Annotation> {
    Class<T> getSupportedAnnotation();

    default String getAnnotationName() {
        return "@" + getSupportedAnnotation().getSimpleName();
    }

    default T getAnnotation(Field field) {
        return field.getAnnotation(getSupportedAnnotation());
    }

    default T getAnnotation(Method method) {
        return method.getAnnotation(getSupportedAnnotation());
    }

    default T getAnnotation(Parameter parameter) {
        return parameter.getAnnotation(getSupportedAnnotation());
    }

    // Instead of retrieving the value directly, create a "getter" so that
    // reflection info can be cached
    default Function<JsonNode, Optional<Object>> createValueGetter(
        Type type, T annotation, MappingContext<?> ctx) {
        // An empty optional by default since in the future some handlers will
        // not return anything, such as the handler of @AfterMapping
        // Note: if that happens, isn't it better to segregate interfaces?
        return node -> Optional.empty();
    }

    default Function<JsonNode, Optional<Object>> createValueGetter(
        Type type, Parameter param, MappingContext<?> ctx) {
        return createValueGetter(type, getAnnotation(param), ctx);
    }

    /**
     * Creates a function that maps a node value to a field of a class instance.
     *
     * @param field The field where the mapped value will be set
     * @param ctx   Mapping context
     * @return The function
     */
    default BiConsumer<JsonNode, Object> createFieldMapper(Field field, MappingContext<?> ctx) {
        Function<JsonNode, Optional<Object>> valueGetter =
            createValueGetter(field.getGenericType(), getAnnotation(field), ctx);

        return (node, instance) -> {
            // Set the mapped value to the field
            valueGetter.apply(node)
                .ifPresent(value -> ReflectionUtils.setFieldValue(instance, field, value));
        };
    }

    /**
     * Creates a function that maps a node value to a method of a class instance.
     *
     * @param method The method that will be called with the mapped value
     * @param ctx    Mapping context
     * @return The function
     */
    default BiConsumer<JsonNode, Object> createMethodMapper(Method method, MappingContext<?> ctx) {
        Type[] parameterTypes = method.getGenericParameterTypes();
        if (parameterTypes.length != 1) {
            throw new IllegalStateException(
                getAnnotationName() + " can only be used on methods with one parameter");
        }

        Function<JsonNode, Optional<Object>> valueGetter =
            createValueGetter(parameterTypes[0], getAnnotation(method), ctx);

        return (node, instance) -> {
            // Invoke the method passing the mapped value
            valueGetter.apply(node)
                .ifPresent(value -> ReflectionUtils.invokeMethod(instance, method, value));
        };
    }
}
