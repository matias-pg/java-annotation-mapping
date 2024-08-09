package dev.matiaspg.annotationsmapping.utils.annotations;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.utils.annotations.types.ValueGetter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.NONE)
public class MappingUtils {
    private static final Map<Class<?>, Function<JsonNode, Object>> GETTERS_BY_TYPE = Map.of(
        JsonNode.class, node -> node,
        String.class, JsonNode::asText,
        boolean.class, JsonNode::asBoolean,
        Boolean.class, JsonNode::asBoolean,
        int.class, JsonNode::asInt,
        Integer.class, JsonNode::asInt,
        long.class, JsonNode::asLong,
        Long.class, JsonNode::asLong,
        double.class, JsonNode::asDouble,
        Double.class, JsonNode::asDouble
    );

    public static Optional<ValueGetter> createValueGetter(Class<?> type) {
        if (GETTERS_BY_TYPE.containsKey(type)) {
            return Optional.of(node -> Optional.ofNullable(GETTERS_BY_TYPE.get(type).apply(node)));
        }
        // If there's no getter for the type, check if any superclass has a getter
        // This will be useful to map ObjectNode and ArrayNode, but it could be
        // useful for other things in the future (YAGNI though?)
        if (type.getSuperclass() != null && !type.getSuperclass().equals(Object.class)) {
            return createValueGetter(type.getSuperclass());
        }
        // If there are no superclasses to check, don't keep trying
        return Optional.empty();
    }

    public static Optional<JsonNode> getValueNode(JsonNode node, String path) {
        JsonNode valueNode = node.at(path);

        return valueNode.isMissingNode() || valueNode.isNull()
            ? Optional.empty()
            : Optional.of(valueNode);
    }
}
