package dev.matiaspg.annotationsmapping.dto.reddit;

import dev.matiaspg.annotationsmapping.dto.hackernews.HackerNewsPosts;
import dev.matiaspg.annotationsmapping.mapping.annotations.AfterMapping;
import dev.matiaspg.annotationsmapping.mapping.annotations.MapEachFrom;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This mapping is overcomplicated just for example purposes.
 *
 * @see HackerNewsPosts HackerNewsPosts - For a simpler and normal example of mapping
 */
@Data
public class RedditPosts {
    private List<RedditPost> posts;
    private int totalPosts;

    // Transform the list before setting it
    // This could also be done in a getter, but with a setter everything is done at once
    @MapEachFrom("/data/children")
    private void setPosts(List<RedditPost> posts) {
        this.posts = posts.stream()
            // Ignore posts that are stickied
            .filter(Predicate.not(RedditPost::getIsStickied))
            // Put the posts with higher score first
            .sorted(Comparator.comparing(RedditPost::getScore, Comparator.reverseOrder()))
            // Take the top 5 posts
            .limit(5)
            .collect(Collectors.toList());
    }

    // TODO: Implement annotation
    @AfterMapping()
    private void doSomething() {
        // Just an example if you want to do something after everything was mapped
        totalPosts = posts.size();
    }
}
