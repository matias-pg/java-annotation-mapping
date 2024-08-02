package dev.matiaspg.annotationsmapping.dto.hackernews;

import dev.matiaspg.annotationsmapping.dto.reddit.RedditPosts;
import dev.matiaspg.annotationsmapping.mapping.annotations.MapEachFrom;
import lombok.Data;

import java.util.List;

/**
 * This is what most mappings would look like.
 *
 * @see RedditPosts RedditPosts - For a more complex example of mapping
 */
@Data
public class HackerNewsPosts {
    @MapEachFrom(value = "/hits", itemType = HackerNewsPost.class)
    private List<HackerNewsPost> posts;
}
