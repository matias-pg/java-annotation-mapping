package dev.matiaspg.annotationsmapping.annotations;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.utils.annotations.AnnotationsProvider;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingAnnotationHandler;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingContext;
import dev.matiaspg.annotationsmapping.utils.annotations.ReflectionUtils;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ItemFilter;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ItemFilter.AllowAllItems;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Getter
@Component
@RequiredArgsConstructor
public class MapEachFromHandler implements MappingAnnotationHandler<MapEachFrom> {
    private final AnnotationsProvider annotationsProvider;

    @Override
    public Class<MapEachFrom> getSupportedAnnotation() {
        return MapEachFrom.class;
    }

    @Override
    public ValueGetter createValueGetter(
        Type type, MapEachFrom annotation, MappingContext ctx
    ) {
        Class<?> collectionType = ReflectionUtils.getRawType(type);
        Class<?> itemType = getItemType(type);
        Function<Stream<?>, Object> collector = createCollector(collectionType);
        ItemFilter filter = AllowAllItems.class.equals(annotation.itemFilter())
            ? null
            : ReflectionUtils.createInstance(annotation.itemFilter());

        return node -> {
            JsonNode iterableNode = node.at(annotation.value());

            // Don't do anything if the node was not found
            if (iterableNode.isMissingNode()) {
                return Optional.empty();
            }

            Stream<JsonNode> stream = StreamSupport
                .stream(iterableNode.spliterator(), annotation.parallel());
            if (filter != null) {
                stream = stream.filter(filter);
            }

            return Optional.of(collector.apply(stream
                // Map each item recursively
                .map(itemNode -> ctx.recursive().apply(itemNode, itemType))));
        };
    }

    private Class<?> getItemType(Type type) {
        Class<?> itemType = ReflectionUtils.getItemType(type).orElse(Object.class);

        if (itemType.equals(Object.class)) {
            throw new IllegalArgumentException(
                "Unable to get the item type to use with " + getAnnotationName()
                    + " (Object is not supported)");
        }

        return itemType;
    }

    private Function<Stream<?>, Object> createCollector(Class<?> type) {
        if (!type.isArray() && !Iterable.class.isAssignableFrom(type)) {
            throw new IllegalStateException(
                getAnnotationName() + " can only be used on arrays or collections");
        }

        if (type.isArray()) {
            return stream -> stream.toArray(size -> (Object[])
                Array.newInstance(type.getComponentType(), size));
        } else if (Set.class.isAssignableFrom(type)) {
            return stream -> stream.collect(Collectors.toSet());
        } else {
            return stream -> stream.collect(Collectors.toList());
        }
    }
}
