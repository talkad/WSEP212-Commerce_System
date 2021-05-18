package Server.DAL;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

import java.util.List;

@Entity(value = "products")
public class ProductDTO {

    @Id
    @Property(value = "productID")
    private int productID;

    @Property(value = "storeID")
    private int storeID;

    @Property(value = "price")
    private double price;

    @Property(value = "name")
    private String name;

    @Property(value = "categories")
    private List<String> categories;

    @Property(value = "keywords")
    private List<String> keywords;

    @Property(value = "rating")
    private double rating;

    @Property(value = "numRatings")
    private int numRatings;

    @Property(value = "reviews")
    private List<ReviewDTO> reviews;

    public ProductDTO(){
        // For Morphia
    }

    public ProductDTO(int productID, int storeID, double price, String name, List<String> categories, List<String> keywords, double rating, int numRatings, List<ReviewDTO> reviews) {
        this.productID = productID;
        this.storeID = storeID;
        this.price = price;
        this.name = name;
        this.categories = categories;
        this.keywords = keywords;
        this.rating = rating;
        this.numRatings = numRatings;
        this.reviews = reviews;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews;
    }
}
