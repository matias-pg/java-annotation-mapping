package dev.matiaspg.annotationsmapping.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.dto.hackernews.HackerNewsPosts;
import dev.matiaspg.annotationsmapping.dto.nhtsa.CarMakes;
import dev.matiaspg.annotationsmapping.dto.reddit.RedditPosts;
import dev.matiaspg.annotationsmapping.mapping.CarMakesClassicMapper;
import dev.matiaspg.annotationsmapping.mapping.CarMakesMapper;
import dev.matiaspg.annotationsmapping.mapping.HackerNewsPostsMapper;
import dev.matiaspg.annotationsmapping.mapping.RedditPostsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/mapping")
@RequiredArgsConstructor
public class MappingController {
    private final HackerNewsPostsMapper hackerNewsPostsMapper;
    private final CarMakesMapper carMakesMapper;
    private final CarMakesClassicMapper carMakesClassicMapper;
    private final RedditPostsMapper redditPostsMapper;

    @PostMapping("/hackernews")
    public Mono<HackerNewsPosts> hackerNews(@RequestBody JsonNode input) {
        return hackerNewsPostsMapper.map(input);
    }

    @PostMapping("/nhtsa/makes")
    public Mono<CarMakes> nhtsaCarMakes(@RequestBody JsonNode input) {
        long startTime = System.nanoTime();
        return carMakesMapper.map(input)
            .doOnNext(c -> {
                int totalItems = c.getMakes().size();
                long duration = System.nanoTime() - startTime;
                log.info("Mapping {} items the new way took {}ms ({} nanoseconds)",
                    totalItems, duration / 1_000_000, duration);
            });
    }

    @PostMapping("/nhtsa/makes-classic")
    public Mono<CarMakes> nhtsaCarMakesClassic(@RequestBody JsonNode input) {
        long startTime = System.nanoTime();
        return carMakesClassicMapper.map(input)
            .doOnNext(c -> {
                int totalItems = c.getMakes().size();
                long duration = System.nanoTime() - startTime;
                log.info("Mapping {} items the classic way took {}ms ({} nanoseconds)",
                    totalItems, duration / 1_000_000, duration);
            });
    }

    @PostMapping("/reddit")
    public Mono<RedditPosts> reddit(@RequestBody JsonNode input) {
        return redditPostsMapper.map(input);
    }
}
