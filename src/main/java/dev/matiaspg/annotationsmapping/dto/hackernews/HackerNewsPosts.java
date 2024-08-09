package dev.matiaspg.annotationsmapping.dto.hackernews;

import dev.matiaspg.annotationsmapping.annotations.MapEachFrom;
import dev.matiaspg.annotationsmapping.dto.reddit.RedditPosts;
import lombok.Data;

import java.util.List;

/**
 * This is what most mappings would look like.
 *
 * @see RedditPosts RedditPosts - For a more complex example of mapping
 */
@Data
public class HackerNewsPosts {
    @MapEachFrom("/hits")
    private List<HackerNewsPost> posts;
}
