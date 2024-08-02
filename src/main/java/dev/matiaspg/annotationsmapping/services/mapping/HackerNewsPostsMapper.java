package dev.matiaspg.annotationsmapping.services.mapping;

import dev.matiaspg.annotationsmapping.dto.hackernews.HackerNewsPosts;
import dev.matiaspg.annotationsmapping.mapping.Mapper;
import org.springframework.stereotype.Service;

@Service
public class HackerNewsPostsMapper implements Mapper<HackerNewsPosts> {
    @Override
    public Class<HackerNewsPosts> getTargetClass() {
        return HackerNewsPosts.class;
    }
}
