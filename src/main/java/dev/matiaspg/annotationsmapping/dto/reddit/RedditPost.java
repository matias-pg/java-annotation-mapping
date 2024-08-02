package dev.matiaspg.annotationsmapping.dto.reddit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.dto.hackernews.HackerNewsPost;
import dev.matiaspg.annotationsmapping.mapping.annotations.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This mapping is overcomplicated just for example purposes.
 *
 * @see HackerNewsPost HackerNewsPost - For a simpler and normal example of mapping
 */
@Data
public class RedditPost {
    @MapFrom("/data/id")
    private String id;

    @MapFrom("/data/title")
    private String title;

    @MapFrom("/data/created_utc")
    private Long createdAt;
    private String createdAtTransformedBySetter; // Example only

    @MapFrom("/data/score")
    private Integer score;

    @MapFrom("/data/num_comments")
    private Integer numberOfComments;

    private String url;

    @MapFrom("/data/upvote_ratio")
    private Double ratio;

    @JsonIgnore
    @MapFrom("/data/stickied")
    private Boolean isStickied;

    @MapEachFrom(value = "/data/preview/images", itemType = Image.class)
    private List<Image> images;

    @MapEachFrom(value = "/data/crosspost_parent_list", itemType = RedditPost.class)
    private List<RedditPost> crosspostParents;

    // TODO: Implement annotation
    // If one is not found, try with the other
    @MapAnyOf({"/data/link_flair_text", "/data/link_flair_richtext/0/t"})
    private String flair;

    // TODO: Implement annotation
    // Concat the subreddit and the author with a slash
    // Example: linux/this_is_a_cool_username
    // Best use case: first name + last name
    @ConcatMapFrom(delimiter = "/", paths = {"/data/subreddit_name_prefixed", "/data/author"})
    private String subredditAndAuthor;

    // TODO: Implement annotation
    // Just an example if you want to do something manually
    @MapManually
    private void setManualFields(JsonNode node) {
        url = node.at("/data/url").asText();
    }

    // Equivalent to the annotation above
    // TODO: Decide which one to keep
    // Just an example if you want to do something manually
    @MapFrom("")
    public void mapCurrentObject(JsonNode node) {
        url = node.at("/data/url").asText();
    }

    // Example of transforming the value via a setter
    @MapFrom("/data/created_utc")
    public void setCreatedAtTransformedBySetter(Long createdAt) {
        createdAtTransformedBySetter = DateTimeFormatter
            .ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'")
            .format(LocalDateTime.ofEpochSecond(createdAt, 0, ZoneOffset.UTC));
    }

    // Example of transforming the value via a getter
    public String getCreatedAtTransformedByGetter() {
        return DateTimeFormatter
            .ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'")
            .format(LocalDateTime.ofEpochSecond(createdAt, 0, ZoneOffset.UTC));
    }
}
