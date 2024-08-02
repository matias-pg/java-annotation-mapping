package dev.matiaspg.annotationsmapping.mapping.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.mapping.MappingContext;
import dev.matiaspg.annotationsmapping.mapping.annotations.MapEachFrom;
import dev.matiaspg.annotationsmapping.utils.Exceptions;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class MapEachFromHandler implements MappingAnnotationHandler<MapEachFrom> {
    @Override
    public Class<MapEachFrom> getSupportedAnnotation() {
        return MapEachFrom.class;
    }

    public BiConsumer<JsonNode, Object> createFieldMapper(Field field, MappingContext<?> ctx) {
        return Exceptions.wrap((node, instance) -> {
            Class<?> collectionType = field.getType();
            ensureCollection(collectionType);

            MapEachFrom annotation = field.getAnnotation(getSupportedAnnotation());
            JsonNode iterableNode = node.at(annotation.value());

            // Don't do anything if the node was not found
            if (iterableNode.isMissingNode()) {
                return;
            }

            // Class<?> itemType = getItemType(collectionType);
            Class<?> itemType = annotation.itemType();

            Stream<?> stream = StreamSupport
                .stream(iterableNode.spliterator(), annotation.parallel())
                .map(itemNode -> ctx.recursive().apply(itemNode, itemType));

            // Set the mapped values to the field
            Object value = collect(stream, collectionType);
            field.set(instance, value);
        });
    }

    @Override
    public BiConsumer<JsonNode, Object> createMethodMapper(Method method, MappingContext<?> ctx) {
        return Exceptions.wrap((node, instance) -> {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new IllegalStateException(
                    getAnnotationName() + " can only be used on methods with one parameter");
            }

            Class<?> collectionType = parameterTypes[0];
            ensureCollection(collectionType);
            MapEachFrom annotation = method.getAnnotation(getSupportedAnnotation());
            JsonNode iterableNode = node.at(annotation.value());

            // Don't do anything if the node was not found
            if (iterableNode.isMissingNode()) {
                return;
            }

            // Class<?> itemType = getItemType(collectionType);
            Class<?> itemType = annotation.itemType();

            Stream<?> stream = StreamSupport
                .stream(iterableNode.spliterator(), annotation.parallel())
                .map(itemNode -> ctx.recursive().apply(itemNode, itemType));

            // Invoke the method passing the mapped values
            Object value = collect(stream, collectionType);
            method.invoke(instance, value);
        });
    }

    private void ensureCollection(Class<?> type) {
        if (!type.isArray() && !Collection.class.isAssignableFrom(type)) {
            throw new IllegalStateException(
                getAnnotationName() + " can only be used on arrays or collections");
        }
    }

    private static Object collect(Stream<?> stream, Class<?> collectionType) {
        // TODO: Collect arrays
        if (collectionType.isArray()) {
            // return stream.toArray(collectionType);
            throw new UnsupportedOperationException(
                "Mapping of " + collectionType + " is not supported for now");
        }

        if (Set.class.isAssignableFrom(collectionType)) {
            return stream.collect(Collectors.toSet());
        } else {
            return stream.collect(Collectors.toList());
        }
    }
}
