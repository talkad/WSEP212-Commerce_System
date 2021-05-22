package Server.DAL;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
public class ReviewDTO {

    @Property(value = "username")
    private String username;

    @Property(value = "review")
    private String review;

    public ReviewDTO(){
        // For Morphia
    }

    public ReviewDTO(String username, String review) {
        this.username = username;
        this.review = review;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}