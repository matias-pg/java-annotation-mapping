package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JsonMapping {
    // TODO: Configure the ObjectMapper to return null by default, instead of "null"
    private static final ObjectMapper om = new ObjectMapper();

    private final MappingAnnotationHandlers handlers;
    private final ClassMappers classMappersCache;

    public <T> T mapJson(JsonNode json, Class<T> targetClass) {
        Collection<BiConsumer<JsonNode, Object>> mappers = classMappersCache
            // Cache the mappers since they can be reused
            .getOrCreateMapper(targetClass, () -> {
                MappingContext<T> ctx = MappingContext.<T>builder()
                    .mapper(om)
                    .recursive(this::mapJson)
                    .build();
                return getMappers(targetClass, ctx);
            });

        T instance = createInstance(targetClass);
        mappers.forEach(mapper -> mapper.accept(json, instance));

        return instance;

        // TODO (Idea): use Jackson's deserialization capabilities rather than
        //  doing things ourselves. For example, rather than working with
        //  instances, work with ObjectNodes/ArrayNodes, and at the end
        //  transform them to the final object. This should make code simpler
        //  and perhaps (?) more efficient
    }

    private <T> Collection<BiConsumer<JsonNode, Object>> getMappers(
        Class<T> targetClass, MappingContext<T> ctx) {
        List<BiConsumer<JsonNode, Object>> mappers = new ArrayList<>();

        // Annotated fields
        for (Field field : getClassFields(targetClass)) {
            Stream.of(field.getAnnotations())
                // Check if it's a mapping annotation, e.g. @MapFrom
                .map(annotation -> handlers.getHandler(annotation.annotationType()))
                // If it's not a mapping annotation, continue with the rest
                .filter(Optional::isPresent).map(Optional::get)
                // Otherwise, create a field mapper
                .map(handler -> handler.createFieldMapper(field, ctx))
                .forEach(mapper -> mappers.add((input, instance) -> {
                    boolean wasAccessible = field.canAccess(instance);
                    try {
                        // Temporarily set the field as accessible so that
                        // it can be modified
                        field.setAccessible(true);

                        mapper.accept(input, instance);
                    } finally {
                        // Restore the previous value
                        field.setAccessible(wasAccessible);
                    }
                }));
        }

        // Annotated methods
        for (Method method : getClassMethods(targetClass)) {
            Stream.of(method.getAnnotations())
                // Check if it's a mapping annotation, e.g. @MapFrom
                .map(annotation -> handlers.getHandler(annotation.annotationType()))
                // If it's not a mapping annotation, continue with the rest
                .filter(Optional::isPresent).map(Optional::get)
                .map(handler -> handler.createMethodMapper(method, ctx))
                // Call the setter with the current node
                .forEach(mapper -> mappers.add((input, instance) -> {
                    boolean wasAccessible = method.canAccess(instance);
                    try {
                        // Temporarily set the method as accessible so that
                        // it can be invoked
                        method.setAccessible(true);

                        mapper.accept(input, instance);
                    } finally {
                        // Restore the previous value
                        method.setAccessible(wasAccessible);
                    }
                }));
        }

        return mappers;
    }

    private static Field[] getClassFields(Class<?> clazz) {
        // To support inheritance
        Stream<Field> parentFields = clazz.getSuperclass() == null
            ? Stream.empty()
            : Stream.of(getClassFields(clazz.getSuperclass()));
        Stream<Field> classFields = Stream.of(clazz.getDeclaredFields());
        // Return an array with fields from the class and its ancestors
        return Stream.concat(parentFields, classFields).toArray(Field[]::new);
    }

    private static Method[] getClassMethods(Class<?> clazz) {
        // To support inheritance
        Stream<Method> parentMethods = clazz.getSuperclass() == null
            ? Stream.empty()
            : Stream.of(getClassMethods(clazz.getSuperclass()));
        Stream<Method> classMethods = Stream.of(clazz.getDeclaredMethods());
        // Return an array with methods from the class and its ancestors
        return Stream.concat(parentMethods, classMethods).toArray(Method[]::new);
    }

    @SneakyThrows({
        NoSuchMethodException.class,
        InvocationTargetException.class,
        InstantiationException.class,
        IllegalAccessException.class,
    })
    private <T> T createInstance(Class<T> targetClass) {
        return targetClass.getDeclaredConstructor().newInstance();
    }
}
