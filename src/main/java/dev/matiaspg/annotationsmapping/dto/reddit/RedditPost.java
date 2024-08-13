package dev.matiaspg.annotationsmapping.dto.reddit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.annotations.ConcatMapFrom;
import dev.matiaspg.annotationsmapping.annotations.MapEachFrom;
import dev.matiaspg.annotationsmapping.annotations.MapFrom;
import dev.matiaspg.annotationsmapping.annotations.MapManually;
import dev.matiaspg.annotationsmapping.dto.hackernews.HackerNewsPost;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

    @MapFrom("/data/edited")
    private Long updatedAt;

    @MapFrom("/data/score")
    private Integer score;

    @MapFrom("/data/num_comments")
    private Integer numberOfComments;

    @MapFrom("/data/upvote_ratio")
    private Double ratio;

    @JsonIgnore
    @MapFrom(value = "/data/stickied", defaultValue = "false")
    private Boolean isStickied;

    @MapEachFrom("/data/preview/images")
    private List<Image> images;

    // If one is not found, try with the other
    @MapFrom({"/data/link_flair_text", "/data/link_flair_richtext/0/t"})
    private String flair;

    // Concat the subreddit and the author with a slash
    // Example: r/linux/this_is_a_cool_username
    // Best use case: first name + last name
    @ConcatMapFrom(paths = {"/data/subreddit_name_prefixed", "/data/author"},
        delimiter = "/")
    private String subredditAndAuthor;

    // These fields are set via a setter, see below
    private String createdAtTransformedBySetter;
    private String url;
    private String thumbnail;

    // TODO: Implement annotation
    // Just an example if you want to do something manually
    @MapManually
    private void setManualFields(JsonNode node) {
        url = node.at("/data/url").asText();
    }

    // Equivalent to the annotation above
    // TODO: Decide which one to keep, this is already working
    @MapFrom("")
    public void mapCurrentObject(JsonNode node) {
        url = node.at("/data/url").asText();
    }

    // Example of transforming the value via a setter
    public void setCreatedAtTransformedBySetter(
        @MapFrom("/data/created_utc") Long createdAt,
        // You can also inject already parsed dates
        @MapFrom("/data/created_utc") Date createdAtDate,
        @MapFrom("/data/created_utc") Instant createdAtInstant
    ) {
        createdAtTransformedBySetter = DateTimeFormatter
            .ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'")
            // Notice the parsed Instant is the one being formatted
            .format(createdAtInstant.atOffset(ZoneOffset.UTC));
    }

    // Example of transforming the value via a getter
    public String getCreatedAtTransformedByGetter() {
        return DateTimeFormatter
            .ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'")
            .format(LocalDateTime.ofEpochSecond(createdAt, 0, ZoneOffset.UTC));
    }

    // Example of mapping multiple values to one method, e.g. if you need to
    // apply some logic to a field depending on the value of another field
    // Note that the annotations are in the parameters!
    public void setMultipleValues(
        // If the field is null or missing, assume it's true
        @MapFrom(value = "/data/over_18", defaultValue = "true") Boolean over18,
        @MapFrom("/data/thumbnail") String thumbnail
    ) {
        // Only set the thumbnail of the post if it's safe for work (SFW)
        if (!over18 && !"self".equals(thumbnail)) {
            this.thumbnail = thumbnail;
        }
    }
}
