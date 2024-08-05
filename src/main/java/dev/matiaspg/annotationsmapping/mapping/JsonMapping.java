package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.matiaspg.annotationsmapping.utils.ReflectionUtils;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static dev.matiaspg.annotationsmapping.utils.ReflectionUtils.*;

@Component
@RequiredArgsConstructor
public class JsonMapping {
    private final ObjectMapper om;
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
            // Create a field mapper
            handlers.getHandlers(field.getAnnotations())
                .map(handler -> handler.createFieldMapper(field, ctx))
                .forEach(mappers::add);
        }

        for (Method method : getClassMethods(targetClass)) {
            // Annotated methods
            handlers.getHandlers(method.getAnnotations())
                .map(handler -> handler.createMethodMapper(method, ctx))
                // Call the setter with the current node
                .forEach(mappers::add);

            // Methods with annotated parameters
            if (hasAnnotatedParameters(method)) {
                // Cache the getters
                List<Function<JsonNode, Optional<Object>>> getters =
                    Stream.of(method.getParameters())
                        .map(param -> createParameterValueGetter(param, ctx)).toList();

                // Call the previously created getters
                mappers.add((node, instance) -> {
                    Object[] mappedParams = getters.stream()
                        .map(getter -> getter.apply(node).orElse(null)).toArray();

                    // Invoke the method passing the mapped values
                    ReflectionUtils.invokeMethod(instance, method, mappedParams);
                });
            }

            // TODO: Ensure methods have annotations on them or on their
            //  parameters, but not both
        }

        return mappers;
    }

    private boolean hasAnnotatedParameters(Method method) {
        return Stream.of(method.getParameters())
            // Check if a param has a mapping annotation, e.g. @MapFrom
            .flatMap(parameter -> handlers
                .getHandlers(parameter.getAnnotations()))
            .findFirst().isPresent();
    }

    @Nonnull
    private Function<JsonNode, Optional<Object>> createParameterValueGetter(
        Parameter parameter, MappingContext<?> ctx) {
        return handlers
            .getHandlers(parameter.getAnnotations()).findFirst()
            // Create a value getter for the first mapping annotation of the field
            .map(handler -> handler
                .createValueGetter(parameter.getParameterizedType(), parameter, ctx))
            // Show a descriptive error if the parameter doesn't have any annotation
            .orElseThrow(() -> new IllegalArgumentException("The `" + parameter.getName()
                + "` parameter in `" + parameter.getDeclaringExecutable().getName() + "()`"
                + " doesn't have any mapping annotation: either add one, or remove the parameter"));
    }
}
