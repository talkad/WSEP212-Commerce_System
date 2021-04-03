package Server.Domain.ShoppingManager;

import java.util.Collection;
import java.util.List;

public class ProductDTO {

    private String name;
    private int productID;
    private int storeID;
    private double price;
    private List<String> categories;
    private List<String> keywords;
    private Collection<String> reviews;
    private double rating;

    public ProductDTO(String name, int productID, int storeID, double price, List<String> categories, List<String> keywords, Collection<String> reviews, double rating) {
        this.name = name;
        this.productID = productID;
        this.storeID = storeID;
        this.price = price;
        this.categories = categories;
        this.keywords = keywords;
        this.reviews = reviews;
        this.rating = rating;
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

    public Collection<String> getReviews() {
        return reviews;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "ProductDTO{\n" +
                "name=" + name + '\n' +
                ", productID=" + productID + '\n' +
                ", storeID=" + storeID + '\n' +
                ", price=" + price + '\n' +
                ", categories=" + categories.toString()+ '\n' +
                ", keywords=" + keywords.toString() + '\n' +
                ", reviews=" + reviews.toString() + '\n' +
                ", rating=" + rating +
                '}';
    }
}
