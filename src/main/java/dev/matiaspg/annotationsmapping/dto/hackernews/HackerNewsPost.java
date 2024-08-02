package dev.matiaspg.annotationsmapping.dto.hackernews;

import dev.matiaspg.annotationsmapping.dto.reddit.RedditPost;
import dev.matiaspg.annotationsmapping.mapping.annotations.MapFrom;
import lombok.Data;

/**
 * This is what most mappings would look like.
 *
 * @see RedditPost RedditPost - For a more complex example of mapping
 */
@Data
public class HackerNewsPost {
    @MapFrom("/story_id")
    private String id;

    @MapFrom("/title")
    private String title;

    @MapFrom("/created_at")
    private String createdAt;

    @MapFrom("/updated_at")
    private String updatedAt;

    @MapFrom("/points")
    private Integer points;

    @MapFrom("/num_comments")
    private Integer numberOfComments;

    @MapFrom("/url")
    private String url;
}
