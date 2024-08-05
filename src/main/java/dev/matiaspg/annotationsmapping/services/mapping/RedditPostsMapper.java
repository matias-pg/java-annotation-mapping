package dev.matiaspg.annotationsmapping.services.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.dto.reddit.RedditPosts;
import dev.matiaspg.annotationsmapping.mapping.JsonMapping;
import dev.matiaspg.annotationsmapping.mapping.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedditPostsMapper implements Mapper<RedditPosts> {
    private final JsonMapping jsonMapping;

    @Override
    public Mono<RedditPosts> map(JsonNode input) {
        return Mono.fromSupplier(() ->
            jsonMapping.mapJson(input, RedditPosts.class));
    }
}
