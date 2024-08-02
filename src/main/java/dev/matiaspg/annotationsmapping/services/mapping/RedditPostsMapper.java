package dev.matiaspg.annotationsmapping.services.mapping;

import dev.matiaspg.annotationsmapping.dto.reddit.RedditPosts;
import dev.matiaspg.annotationsmapping.mapping.Mapper;
import org.springframework.stereotype.Service;

@Service
public class RedditPostsMapper implements Mapper<RedditPosts> {
    @Override
    public Class<RedditPosts> getTargetClass() {
        return RedditPosts.class;
    }
}
