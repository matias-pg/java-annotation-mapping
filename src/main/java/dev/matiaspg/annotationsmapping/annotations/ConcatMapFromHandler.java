package dev.matiaspg.annotationsmapping.annotations;

import dev.matiaspg.annotationsmapping.utils.annotations.AnnotationsProvider;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingAnnotationHandler;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingContext;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingUtils;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static dev.matiaspg.annotationsmapping.annotations.MapFrom.NO_DEFAULT_VALUE;

@Getter
@Component
@RequiredArgsConstructor
public class ConcatMapFromHandler implements MappingAnnotationHandler<ConcatMapFrom> {
    private final AnnotationsProvider annotationsProvider;

    @Override
    public Class<ConcatMapFrom> getSupportedAnnotation() {
        return ConcatMapFrom.class;
    }

    @Override
    public ValueGetter createValueGetter(
        Type type, ConcatMapFrom annotation, MappingContext ctx
    ) {
        return node -> {
            List<String> values = Stream.of(annotation.paths())
                // Get the value in each path
                .map(path -> MappingUtils.getValueNode(node, path)
                    .map(valueNode -> ctx.recurse(valueNode, String.class)))
                // Ignore the values of nodes that are null or missing
                .filter(Optional::isPresent).map(Optional::get)
                .toList();

            if (!values.isEmpty()) {
                return Optional.of(String.join(annotation.delimiter(), values));
            }

            if (!NO_DEFAULT_VALUE.equals(annotation.defaultValue())) {
                // Return a default value if one was set
                return Optional.of(annotation.defaultValue());
            } else if (annotation.defaultEmptyString()) {
                // Return an empty string as default value
                return Optional.of(NO_DEFAULT_VALUE);
            }
            // Return a MissingNode so no mapping is done
            return Optional.empty();
        };
    }
}
