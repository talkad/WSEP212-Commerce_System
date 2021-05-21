package Server.Domain.UserManager.DTOs;

import java.time.LocalDate;

import Server.DAL.ProductDTO;
import Server.DAL.ReviewDTO;
import Server.Domain.CommonClasses.Pair;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.Review;
import Server.Domain.ShoppingManager.StoreController;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class PurchaseClientDTO {

    private BasketClientDTO basket;
    private double totalPrice;
    private String purchaseDate;

    public PurchaseClientDTO(BasketClientDTO basket, double totalPrice, LocalDate purchaseDate) {
        this.basket = basket;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate.toString();
    }

    // TODO Refactor constructor name, not arg name
    public PurchaseClientDTO(Server.DAL.PurchaseDTO purchaseDTO){
        // TODO maybe make thread-safe
        this.totalPrice = purchaseDTO.getTotalPrice();
        this.purchaseDate = purchaseDTO.getPurchaseDate();

        Set<ProductClientDTO> productsDTO = new ConcurrentSkipListSet<>();
        Collection<Integer> amounts = new Vector<>();

        for(Pair<Server.DAL.ProductDTO, Integer> pair : purchaseDTO.getBasket()){
            ProductDTO productDTO = pair.getFirst();
            List<Review> reviews = new Vector<>();

            for(ReviewDTO reviewDTO: productDTO.getReviews())
                reviews.add(new Review(reviewDTO));

            productsDTO.add(new ProductClientDTO(productDTO.getName(), productDTO.getProductID(), productDTO.getStoreID(), productDTO.getPrice(), productDTO.getCategories(), productDTO.getKeywords(), reviews, productDTO.getRating(), productDTO.getNumRatings()));
            amounts.add(pair.getSecond());
        }

        this.basket = new BasketClientDTO(purchaseDTO.getStoreID(), StoreController.getInstance().getStore(purchaseDTO.getStoreID()).getResult().getStoreName(), productsDTO, amounts);
    }

    public Server.DAL.PurchaseDTO toDTO(){
        // TODO sort out product issue then implement
        return new Server.DAL.PurchaseDTO();
    }

    public BasketClientDTO getBasket() {
        return basket;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    @Override
    public String toString() {
        return "PurchaseDTO{" +
                "basket=" + basket +
                ", totalPrice=" + totalPrice +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}
