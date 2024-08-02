package dev.matiaspg.annotationsmapping.dto.reddit;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.dto.hackernews.HackerNewsPost;
import dev.matiaspg.annotationsmapping.mapping.annotations.*;
import lombok.Data;

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
    private String createdAt;

    @MapFrom("/data/score")
    private Integer score;

    @MapFrom("/data/num_comments")
    private Integer numberOfComments;

    private String url;

    @MapFrom("/data/upvote_ratio")
    private Double ratio;

    // TODO: Implement annotation
    // If one is not found, map the other
    @MapAnyOf({"/data/link_flair_text", "/data/link_flair_richtext/0/t"})
    private String flair;

    @MapFrom("/data/author_premium")
    private Boolean isAuthorPremium;

    @MapEachFrom(value = "/data/preview/images", itemType = Image.class)
    private List<Image> images;

    @MapEachFrom(value = "/data/crosspost_parent_list", itemType = RedditPost.class)
    private List<RedditPost> crosspostParents;

    // TODO: Implement annotation
    // Concat the subreddit and the author with a slash
    // Example: linux/this_is_a_cool_username
    // Best use case: first name + last name
    @ConcatMapFrom(delimiter = "/", paths = {"/data/subreddit_name_prefixed", "/data/author"})
    private String subredditAndAuthor;

    // TODO: Implement annotation
    // Just an example if you want to do something manually
    @MapCurrentNode
    private void setManualFields(JsonNode node) {
        // TODO: Configure the ObjectMapper to return null by default, instead of "null"
        url = node.at("/data/url").asText();
    }
}
