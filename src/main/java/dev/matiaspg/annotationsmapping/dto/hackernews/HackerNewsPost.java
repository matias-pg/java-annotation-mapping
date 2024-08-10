package dev.matiaspg.annotationsmapping.dto.hackernews;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.matiaspg.annotationsmapping.annotations.MapFrom;
import dev.matiaspg.annotationsmapping.dto.reddit.RedditPost;
import lombok.Data;

import java.util.Date;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date createdAt;

    @MapFrom("/updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date updatedAt;

    @MapFrom("/points")
    private Integer points;

    @MapFrom("/num_comments")
    private Integer numberOfComments;

    @MapFrom("/url")
    private String url;
}
