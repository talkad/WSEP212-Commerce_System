package Domain.ShoppingManager;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import Domain.CommonClasses.Rating;

public class Product {

    private final int productID;
    private final int storeID;
    private final String name;
    private AtomicReference<Double> price;
    private List<String> categories;

    private AtomicReference<Double> rating;
    private AtomicInteger numRatings;
    private Collection<String> reviews;


    public Product(int productID, int storeID, String name, double price, List<String> categories){
        this.productID = productID;
        this.storeID = storeID;
        this.name = name;
        this.price = new AtomicReference<>(price);
        this.categories = categories;

        this.rating = new AtomicReference<>(0.0);
        this.numRatings = new AtomicInteger(0);
        this.reviews = Collections.synchronizedCollection(new LinkedList<>());
    }

    public void updatePrice(Double newPrice){
        Double currentPrice;

        do {
            currentPrice = price.get();
        }while (!price.compareAndSet(currentPrice, newPrice));
    }

    public int getStoreID(){
        return storeID;
    }

    public String getName() {
        return name;
    }

    public int getProductID() {
        return productID;
    }

    public double getPrice() {
        return price.get();
    }

    public ProductDTO getDTO() {
        return new ProductDTO(name, price.get(), categories, storeID);
    }

    public void addReview(String review){
        reviews.add(review);
    }

    public Collection<String> getReviews(){
        return reviews;
    }

    public void addRating(Rating rate){
        int prevNum;
        Double currentRating;
        Double newRating;

        do {
            currentRating = rating.get();
            prevNum = numRatings.get();
            newRating = (prevNum * currentRating + rate.rate) / (prevNum + 1);

        }while (!rating.compareAndSet(currentRating, newRating));

        numRatings.getAndIncrement();
    }

    public double getRating(){
        return rating.get();
    }

    public List<String> getCategories() {
        return categories;
    }
}