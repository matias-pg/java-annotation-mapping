package dev.matiaspg.annotationsmapping.utils.annotations;

import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueMapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;

public interface MappingAnnotationHandler<T extends Annotation> {
    AnnotationsProvider getAnnotationsProvider();

    Class<T> getSupportedAnnotation();

    default String getAnnotationName() {
        return "@" + getSupportedAnnotation().getSimpleName();
    }

    default T getAnnotation(Field field) {
        return getAnnotationsProvider().getAnnotation(field, getSupportedAnnotation());
    }

    default T getAnnotation(Method method) {
        return getAnnotationsProvider().getAnnotation(method, getSupportedAnnotation());
    }

    default T getAnnotation(Parameter parameter) {
        return getAnnotationsProvider().getAnnotation(parameter, getSupportedAnnotation());
    }

    // Instead of retrieving the value directly, create a "getter" so that
    // reflection info can be cached
    default ValueGetter createValueGetter(
        Type type, T annotation, MappingContext ctx) {
        // An empty optional by default since in the future some handlers will
        // not return anything, such as the handler of @AfterMapping
        // Note: if that happens, isn't it better to segregate interfaces?
        return node -> Optional.empty();
    }

    default ValueGetter createValueGetter(
        Type type, Parameter param, MappingContext ctx) {
        return createValueGetter(type, getAnnotation(param), ctx);
    }

    /**
     * Creates a function that maps a node value to a field of a class instance.
     *
     * @param field The field where the mapped value will be set
     * @param ctx   Mapping context
     * @return The function
     */
    default ValueMapper createFieldMapper(Field field, MappingContext ctx) {
        ValueGetter valueGetter =
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
    default ValueMapper createMethodMapper(Method method, MappingContext ctx) {
        Type[] parameterTypes = method.getGenericParameterTypes();
        if (parameterTypes.length != 1) {
            throw new IllegalStateException(
                getAnnotationName() + " can only be used on methods with one parameter");
        }

        ValueGetter valueGetter =
            createValueGetter(parameterTypes[0], getAnnotation(method), ctx);

        return (node, instance) -> {
            // Invoke the method passing the mapped value
            valueGetter.apply(node)
                .ifPresent(value -> ReflectionUtils.invokeMethod(instance, method, value));
        };
    }
}
