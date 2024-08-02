package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.matiaspg.annotationsmapping.mapping.handlers.MappingAnnotationHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
public class JsonMapping {
    private static final ObjectMapper om = new ObjectMapper();

    private final MappingAnnotationHandlers handlers;

    public <T> T mapJson(JsonNode json, Class<T> targetClass) {
        MappingContext<T> ctx = MappingContext.<T>builder()
            .mapper(om)
            .rootNode(json)
            .currentNode(json)
            .targetClass(targetClass)
            .recursive(this::mapJson)
            .build();
        Collection<BiConsumer<JsonNode, Object>> mappers = getMappers(ctx);

        T instance = createInstance(targetClass);
        mappers.forEach(m -> m.accept(json, instance));

        return instance;

        // TODO (Idea): use Jackson's deserialization capabilities rather than
        //  doing things ourselves. For example, rather than working with
        //  instances, work with ObjectNodes/ArrayNodes, and at the end
        //  transform them to the final object. This should make code simpler
        //  and perhaps (?) more efficient
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

    private <T> Collection<BiConsumer<JsonNode, Object>> getMappers(MappingContext<T> ctx) {
        Class<T> targetClass = ctx.targetClass();
        List<BiConsumer<JsonNode, Object>> mappers = new ArrayList<>();

        // Annotated fields
        for (Field field : targetClass.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                Optional<MappingAnnotationHandler<?>> handler = handlers
                    .getHandler(annotation.annotationType());

                if (handler.isEmpty()) {
                    // If the annotation is not for mapping, continue with the rest
                    continue;
                }

                BiConsumer<JsonNode, Object> mapper = handler.get().handleField(field, ctx);

                mappers.add((input, instance) -> {
                    boolean wasAccessible = field.canAccess(instance);
                    try {
                        field.setAccessible(true);

                        mapper.accept(input, instance);
                    } finally {
                        field.setAccessible(wasAccessible);
                    }
                });
            }
        }

        // Annotated methods
        for (Method method : targetClass.getDeclaredMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                Optional<MappingAnnotationHandler<?>> handler = handlers
                    .getHandler(annotation.annotationType());

                if (handler.isEmpty()) {
                    // If the annotation is not for mapping, continue with the rest
                    continue;
                }

                BiConsumer<JsonNode, Object> mapper = handler.get().handleMethod(method, ctx);

                // Call the setter with the current node
                mappers.add(mapper);
            }
        }

        return mappers;
    }

    /*private <T> void asdas(Class<T> targetClass) throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(targetClass, Object.class);
        PropertyDescriptor[] props = info.getPropertyDescriptors();

        for (PropertyDescriptor pd : props) {
            pd.getWriteMethod();
        }
    }*/
}
