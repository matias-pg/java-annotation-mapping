package dev.matiaspg.annotationsmapping.utils.annotations.types;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Function;

/**
 * A function that gets the value of a {@link JsonNode}.
 * <p>
 * MAY return {@code null} if unable to extract the value.
 */
public interface ValueGetter extends Function<JsonNode, Object> {
}
