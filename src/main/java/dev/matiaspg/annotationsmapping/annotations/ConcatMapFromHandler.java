package dev.matiaspg.annotationsmapping.annotations;

import com.fasterxml.jackson.core.JsonPointer;
import dev.matiaspg.annotationsmapping.utils.annotations.AnnotationsProvider;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingContext;
import dev.matiaspg.annotationsmapping.utils.annotations.MappingUtils;
import dev.matiaspg.annotationsmapping.utils.annotations.types.MappingAnnotationHandler;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Predicate;
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
        // Cache things that will be reused
        JsonPointer[] paths = Stream.of(annotation.paths())
            .map(JsonPointer::compile)
            .toArray(JsonPointer[]::new);
        String defaultValue = createDefaultValue(annotation);

        return node -> {
            List<String> values = Stream.of(paths)
                // Get the value in each path
                .map(node::at)
                // Ignore the values of nodes that are null or missing
                .filter(Predicate.not(MappingUtils::isNullOrMissing))
                .map(valueNode -> ctx.recurse(valueNode, String.class))
                // Ignore `null`s or blank values
                .filter(value -> value != null && !value.isBlank())
                .toList();

            if (values.isEmpty()) {
                return defaultValue;
            }
            return String.join(annotation.delimiter(), values);
        };
    }

    @Nullable
    private String createDefaultValue(ConcatMapFrom annotation) {
        if (!NO_DEFAULT_VALUE.equals(annotation.defaultValue())) {
            // Return a default value if one was set
            return annotation.defaultValue();
        } else if (annotation.defaultEmptyString()) {
            // Return an empty string as default value
            return NO_DEFAULT_VALUE;
        }
        // Return null so no mapping is done
        return null;
    }
}
