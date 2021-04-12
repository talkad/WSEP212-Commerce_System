package Server.Domain.ShoppingManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ProductDTO {

    private String name;
    private int productID;
    private int storeID;
    private double price;
    private List<String> categories;
    private List<String> keywords;
    private Collection<Review> reviews;
    private double rating;
    private int numRatings;

    /**
     *constructor for objects from database
     */
    public ProductDTO(String name, int productID, int storeID, double price, List<String> categories, List<String> keywords, Collection<Review> reviews, double rating, int numRatings) {
        this.name = name;
        this.productID = productID;
        this.storeID = storeID;
        this.price = price;
        this.categories = categories;
        this.keywords = keywords;
        this.reviews = reviews;
        this.rating = rating;
        this.numRatings = numRatings;
    }

    /**
     *constructor for new products
     */
    public ProductDTO(String name, int storeID, double price, List<String> categories, List<String> keywords, Collection<Review> reviews) {
        this.name = name;
        this.productID = -1;
        this.storeID = storeID;
        this.price = price;
        this.categories = categories;
        this.keywords = keywords;
        this.reviews = reviews;
        this.rating = 0;
        this.numRatings = 0;
    }

    public String getName() {
        return name;
    }

    public int getProductID() {
        return productID;
    }

    public int getStoreID() {
        return storeID;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public Collection<Review> getReviews() {
        return reviews;
    }

    public double getRating() {
        return rating;
    }

    public int getNumRatings() {
        return numRatings;
    }

    @Override
    public String toString() {
        return "Product id " + productID + ":\n" +
                "name: " + name + ", price: " + price + ", categories=" + Arrays.toString(categories.toArray()) +
                ", keywords=" + Arrays.toString(keywords.toArray()) + "\n" +
                "Rating: " + rating +", keywords=" + Arrays.toString(reviews.toArray()) + "\n";
    }
}
