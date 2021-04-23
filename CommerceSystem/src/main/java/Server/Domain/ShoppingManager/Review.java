package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Response;

/**
 * Immutable review object
 */
public class Review {

    private final String username;
    private final String review;

    private Review(String username, String review) {
        this.username = username;
        this.review = review;
    }

    public static Response<Review> createReview(String username, String review){
        if(review == null || review.length() == 0)
            return new Response<>(null, true, "Review cannot be empty");

        return new Response<>(new Review(username, review), false, "Create review successfully");
    }

    public String getUsername() {
        return username;
    }

    public String getReview() {
        return review;
    }

    @Override
    public String toString() {
        return username + ": " + review;
    }
}
