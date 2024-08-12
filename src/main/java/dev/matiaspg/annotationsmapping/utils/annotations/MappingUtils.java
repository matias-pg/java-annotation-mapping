package dev.matiaspg.annotationsmapping.utils.annotations;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.NONE)
public class MappingUtils {
    private static final Map<Class<?>, Function<JsonNode, Object>> GETTERS_BY_TYPE = Map.ofEntries(
        Map.entry(JsonNode.class, node -> node),
        Map.entry(String.class, JsonNode::asText),
        Map.entry(boolean.class, JsonNode::asBoolean),
        Map.entry(Boolean.class, JsonNode::asBoolean),
        Map.entry(int.class, JsonNode::asInt),
        Map.entry(Integer.class, JsonNode::asInt),
        Map.entry(long.class, JsonNode::asLong),
        Map.entry(Long.class, JsonNode::asLong),
        Map.entry(double.class, JsonNode::asDouble),
        Map.entry(Double.class, JsonNode::asDouble),
        Map.entry(Instant.class, MappingUtils::asInstant),
        Map.entry(Date.class, MappingUtils::asDate)
    );

    @Nullable
    public static ValueGetter getValueGetter(Class<?> type) {
        if (GETTERS_BY_TYPE.containsKey(type)) {
            return node -> GETTERS_BY_TYPE.get(type).apply(node);
        }
        // If there's no getter for the type, check if any superclass has a getter
        // This will be useful to map ObjectNode and ArrayNode, but it could be
        // useful for other things in the future (YAGNI though?)
        if (type.getSuperclass() != null && !type.getSuperclass().equals(Object.class)) {
            return getValueGetter(type.getSuperclass());
        }
        // If there are no superclasses to check, don't keep trying
        return null;
    }

    public static boolean isNullOrMissing(JsonNode node) {
        return node.isMissingNode() || node.isNull();
    }

    public static boolean isBlankText(JsonNode node) {
        return node.isTextual() && node.asText().isBlank();
    }

    private static Instant asInstant(JsonNode node) {
        if (node.isTextual()) {
            return OffsetDateTime.parse(node.asText()).toInstant();
        } else if (node.isIntegralNumber()) {
            return createInstant(node.asLong());
        } else if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        throw new IllegalArgumentException(
            "Unable to create Instant from node value of type " + node.getNodeType());
    }

    private static Date asDate(JsonNode node) {
        Instant instant = asInstant(node);
        if (instant == null) {
            return null;
        }
        return Date.from(instant);
    }

    private static Instant createInstant(long secondsOrMilli) {
        // Timestamps of more than 10 digits normally represent milliseconds
        if (secondsOrMilli > 9999999999L) {
            // Create the instant with epoch millis, otherwise most dates would
            // be at the year 56000+ or so
            return Instant.ofEpochMilli(secondsOrMilli);
        }
        // Create the instant with epoch seconds, otherwise most dates would be
        // at the year 1970 or so
        return Instant.ofEpochSecond(secondsOrMilli);
    }
}
