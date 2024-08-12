package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.dto.nhtsa.CarMakes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CarMakesClassicMapper implements Mapper<CarMakes> {
    private final MakeMappingFunction makeMapping;

    @Override
    public Mono<CarMakes> map(JsonNode input) {
        return Mono.just(input)
            .map(i -> new CarMakes())
            .flatMap(carMakes -> makeMapping.apply(input)
                .doOnNext(carMakes::setMakes)
                .thenReturn(carMakes));
    }

    @Component
    @RequiredArgsConstructor
    public static class MakeMappingFunction implements
        Function<JsonNode, Mono<List<CarMakes.Make>>> {
        private final FindNodeByNameFunction findNodeByName;

        @Override
        public Mono<List<CarMakes.Make>> apply(JsonNode root) {
            return Mono.just(root)
                .flatMap(r -> Mono.justOrEmpty(
                    findNodeByName.apply("/Results", root)))
                .flatMapMany(Flux::fromIterable)
                .flatMap(item -> Mono.just(new CarMakes.Make())
                    .doOnNext(make -> findNodeByName
                        .apply("/Make_ID", item).map(JsonNode::asInt)
                        .ifPresent(make::setId))
                    .doOnNext(make -> findNodeByName
                        .apply("/Make_Name", item).map(JsonNode::asText)
                        .ifPresent(make::setName))
                )
                .collectList();
        }
    }

    @Component
    public static class FindNodeByNameFunction implements
        BiFunction<String, JsonNode, Optional<JsonNode>> {
        @Override
        public Optional<JsonNode> apply(String path, JsonNode node) {
            JsonNode valueNode = node.at(path);
            if (
                valueNode.isMissingNode()
                    || valueNode.isNull()
                    || (valueNode.isTextual() && valueNode.asText().isBlank())
            ) {
                return Optional.empty();
            }
            return Optional.of(valueNode);
        }
    }
}
