package dev.matiaspg.annotationsmapping.dto.hackernews;

import dev.matiaspg.annotationsmapping.mapping.annotations.AfterMapping;
import dev.matiaspg.annotationsmapping.mapping.annotations.MapEachFrom;
import lombok.Data;

import java.util.List;

@Data
public class HackerNewsPosts {
    @MapEachFrom(value = "/hits", itemType = HackerNewsPost.class)
    private List<HackerNewsPost> posts;

    // TODO: Implement annotation
    @AfterMapping()
    private void doSomething() {
        // Just an example if you want to do something after everything was mapped
    }
}
