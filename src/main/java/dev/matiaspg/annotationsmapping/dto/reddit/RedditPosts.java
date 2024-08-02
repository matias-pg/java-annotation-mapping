package dev.matiaspg.annotationsmapping.dto.reddit;

import dev.matiaspg.annotationsmapping.mapping.annotations.AfterMapping;
import dev.matiaspg.annotationsmapping.mapping.annotations.MapEachFrom;
import lombok.Data;

import java.util.List;

@Data
public class RedditPosts {
    @MapEachFrom(value = "/data/children", itemType = RedditPost.class)
    private List<RedditPost> posts;

    // TODO: Implement annotation
    @AfterMapping()
    private void doSomething() {
        // Just an example if you want to do something after everything was mapped
    }
}
