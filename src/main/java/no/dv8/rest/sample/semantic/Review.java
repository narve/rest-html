package no.dv8.rest.sample.semantic;

import lombok.Builder;
import lombok.Data;

//@ItemType(href="http://schema.org/Review")
@Data
@Builder
public class Review {

    Rating reviewRating;
    String author;
}
