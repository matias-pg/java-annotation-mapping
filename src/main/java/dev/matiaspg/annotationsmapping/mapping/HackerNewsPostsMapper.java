package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.dto.hackernews.HackerNewsPosts;
import dev.matiaspg.annotationsmapping.utils.annotations.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class HackerNewsPostsMapper implements Mapper<HackerNewsPosts> {
    private final JsonMapper jsonMapper;

    @Override
    public Mono<HackerNewsPosts> map(JsonNode input) {
        return Mono.fromSupplier(() ->
            jsonMapper.map(input, HackerNewsPosts.class));
    }
}
