package dev.matiaspg.annotationsmapping.services.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.dto.hackernews.HackerNewsPosts;
import dev.matiaspg.annotationsmapping.mapping.JsonMapping;
import dev.matiaspg.annotationsmapping.mapping.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class HackerNewsPostsMapper implements Mapper<HackerNewsPosts> {
    private final JsonMapping jsonMapping;

    @Override
    public Mono<HackerNewsPosts> map(JsonNode input) {
        return Mono.fromSupplier(() ->
            jsonMapping.mapJson(input, HackerNewsPosts.class));
    }
}
