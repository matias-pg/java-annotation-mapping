package dev.matiaspg.annotationsmapping.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.dto.reddit.RedditPosts;
import dev.matiaspg.annotationsmapping.utils.annotations.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedditPostsMapper implements Mapper<RedditPosts> {
    private final JsonMapper jsonMapper;

    @Override
    public Mono<RedditPosts> map(JsonNode input) {
        return Mono.fromSupplier(() ->
            jsonMapper.map(input, RedditPosts.class));
    }
}
