package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Rating;
import Server.Domain.CommonClasses.Response;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Product {

    private static AtomicInteger indexer = new AtomicInteger(0); //assign productID to new products

    private int productID;
    private final int storeID;
    private AtomicReference<Double> price;
    private AtomicReference<String> name;
    private final List<String> categories;
    private final List<String> keywords;
    private AtomicReference<Double> rating;
    private AtomicInteger numRatings;
    private List<Review> reviews;


    private Product(ProductDTO productDTO){
        this.productID = productDTO.getProductID();
        this.storeID = productDTO.getStoreID();
        this.name = new AtomicReference<>(productDTO.getName());
        this.price = new AtomicReference<>(productDTO.getPrice());
        this.categories = productDTO.getCategories();
        this.keywords = productDTO.getKeywords();

        this.rating = new AtomicReference<>(productDTO.getRating());
        this.numRatings = new AtomicInteger(productDTO.getNumRatings());

        this.reviews = productDTO.getReviews()!=null? productDTO.getReviews(): new Vector<>();
    }

    public static Product createProduct(ProductDTO productDTO){
        Product product = new Product(productDTO);

        if(productDTO.getProductID() == -1){
            product.setProductID(indexer.getAndIncrement());
        }

        return product;
    }

    private void setProductID(int id) {
        this.productID = id;
    }

    public void updatePrice(Double newPrice){
        Double currentPrice;

        do {
            currentPrice = price.get();
        }while (!price.compareAndSet(currentPrice, newPrice));
    }

    public void updateName(String newName){
        String currentName;

        do {
            currentName = name.get();
        }while (!name.compareAndSet(currentName, newName));
    }

    public int getStoreID(){
        return storeID;
    }

    public String getName() {
        return name.get();
    }

    public int getProductID() {
        return productID;
    }

    public double getPrice() {
        return price.get();
    }

    public Response<Boolean> addReview(Review review){
        reviews.add(review);
        return new Response<>(true, false, "Product: Review added successfully");
    }

    public Collection<Review> getReviews(){
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

    public boolean containsCategory(String category){
        if(categories == null || category == null || category.length() == 0)
            return false;

        for(String c: categories)
            if(c.contains(category))
                return true;

        return false;
    }

    public boolean containsKeyword(String key){
        return keywords != null && keywords.contains(key);
    }

    public ProductDTO getProductDTO(){
        return new ProductDTO(name.get(), productID, storeID, price.get(), categories, keywords, reviews, rating.get(), numRatings.get());
    }
}
