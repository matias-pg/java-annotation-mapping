package dev.matiaspg.annotationsmapping.mapping.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.mapping.MappingContext;
import dev.matiaspg.annotationsmapping.mapping.annotations.MapFrom;
import dev.matiaspg.annotationsmapping.utils.Exceptions;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

@Component
public class MapFromHandler implements MappingAnnotationHandler<MapFrom> {
    @Override
    public Class<MapFrom> getSupportedAnnotation() {
        return MapFrom.class;
    }

    public BiConsumer<JsonNode, Object> handleField(Field field, MappingContext<?> ctx) {
        return Exceptions.wrap((node, instance) -> {
            MapFrom annotation = field.getAnnotation(getSupportedAnnotation());
            Class<?> type = field.getType();

            // Don't do anything if the node was not found
            JsonNode valueNode = node.at(annotation.value());
            if (valueNode.isMissingNode()) {
                return;
            }

            // Set the mapped value to the field
            Object value = ctx.mapper().treeToValue(valueNode, type);
            field.set(instance, value);
        });
    }

    @Override
    public BiConsumer<JsonNode, Object> handleMethod(Method method, MappingContext<?> ctx) {
        return Exceptions.wrap((node, instance) -> {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new IllegalStateException(
                    getAnnotationName() + " can only be used on methods with one parameter");
            }

            MapFrom annotation = method.getAnnotation(getSupportedAnnotation());
            Class<?> type = parameterTypes[0];

            // Don't do anything if the node was not found
            JsonNode valueNode = node.at(annotation.value());
            if (valueNode.isMissingNode()) {
                return;
            }

            // Invoke the method passing the mapped value
            Object value = ctx.mapper().treeToValue(valueNode, type);
            method.invoke(instance, value);
        });
    }
}
