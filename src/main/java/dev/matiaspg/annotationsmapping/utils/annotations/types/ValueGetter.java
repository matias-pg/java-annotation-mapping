package dev.matiaspg.annotationsmapping.utils.annotations.types;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;
import java.util.function.Function;

/**
 * A function that gets the value from a {@link JsonNode}.
 * <p>
 * MAY return an empty {@link Optional} if unable to extract the value.
 */
public interface ValueGetter extends Function<JsonNode, Optional<Object>> {
}
