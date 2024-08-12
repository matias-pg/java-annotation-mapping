package dev.matiaspg.annotationsmapping.utils.annotations;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueMapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JsonMapper {
    private final ValueGetters valueGettersCache;
    private final ValueMappers valueMappersCache;
    private final MappingAnnotationHandlers handlers;

    @SuppressWarnings("unchecked")
    public <T> T map(JsonNode json, Class<T> targetClass) {
        ValueGetter typeValueGetter = valueGettersCache
            .getOrCache(targetClass,
                () -> MappingUtils.getValueGetter(targetClass));
        if (typeValueGetter != null) {
            // If the type is simple enough, try to map it with the getters
            // from MappingUtils#GETTERS_BY_TYPE
            // It's safe to cast since the getter returns values of the same type
            return (T) typeValueGetter.apply(json);
        }
        // Otherwise, try to map to an object using reflection
        return mapObject(json, targetClass);
    }

    private <T> T mapObject(JsonNode json, Class<T> targetClass) {
        Collection<ValueMapper> mappers = valueMappersCache
            // Cache the mappers since they can be reused
            .getOrCache(targetClass,
                () -> getMappers(targetClass, new MappingContext(this::map)));

        // Create an instance of the target class and apply the mappers to it
        T instance = ReflectionUtils.newInstance(targetClass);
        mappers.forEach(mapper -> mapper.accept(json, instance));

        return instance;

        // TODO (Idea): use Jackson's deserialization capabilities rather than
        //  doing things ourselves. For example, rather than working with
        //  instances, work with ObjectNodes/ArrayNodes, and at the end
        //  transform them to the final object. Or check if it's possible to
        //  use a generic custom deserializer. This could allow for simpler
        //  code and perhaps (?) be more efficient
    }

    private <T> Collection<ValueMapper> getMappers(
        Class<T> targetClass, MappingContext ctx
    ) {
        List<ValueMapper> mappers = new ArrayList<>();

        // Annotated fields
        for (Field field : ReflectionUtils.getClassFields(targetClass)) {
            handlers.getHandlers(field.getAnnotations())
                .map(handler -> handler.createFieldMapper(field, ctx))
                .forEach(mappers::add);
        }

        for (Method method : ReflectionUtils.getClassMethods(targetClass)) {
            // Annotated methods (e.g. setters)
            handlers.getHandlers(method.getAnnotations())
                .map(handler -> handler.createMethodMapper(method, ctx))
                .forEach(mappers::add);

            // Methods with annotated parameters
            if (hasAnnotatedParameters(method)) {
                // Create "value getters" for each method parameter
                List<ValueGetter> getters = Stream.of(method.getParameters())
                    .map(param -> createParameterValueGetter(param, ctx)).toList();

                mappers.add((node, instance) -> {
                    // Call the previously created getters
                    Object[] mappedParams = getters.stream()
                        .map(getter -> getter.apply(node)).toArray();

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
    private ValueGetter createParameterValueGetter(
        Parameter parameter, MappingContext ctx
    ) {
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
