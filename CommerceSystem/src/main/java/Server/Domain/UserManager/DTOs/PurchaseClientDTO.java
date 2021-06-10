package Server.Domain.UserManager.DTOs;

import java.time.LocalDate;

import Server.DAL.PairDTOs.ProductIntPair;
import Server.DAL.ProductDTO;
import Server.DAL.ReviewDTO;
import Server.DAL.PurchaseDTO;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.Review;
import Server.Domain.ShoppingManager.StoreController;

import java.util.*;

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
    public PurchaseClientDTO(PurchaseDTO purchaseDTO){
        // TODO maybe make thread-safe
        this.totalPrice = purchaseDTO.getTotalPrice();
        this.purchaseDate = purchaseDTO.getPurchaseDate();

        List<ProductClientDTO> productsDTO = new Vector<>();
        Collection<Integer> amounts = new Vector<>();

        for(ProductIntPair pair : purchaseDTO.getBasket()){
            ProductDTO productDTO = pair.getFirst();
            List<Review> reviews = new Vector<>();

            for(ReviewDTO reviewDTO: productDTO.getReviews())
                reviews.add(new Review(reviewDTO));

            productsDTO.add(new ProductClientDTO(productDTO.getName(), productDTO.getProductID(), productDTO.getStoreID(), productDTO.getPrice(), productDTO.getCategories(), productDTO.getKeywords(), reviews, productDTO.getRating(), productDTO.getNumRatings()));
            amounts.add(pair.getSecond());
        }

        this.basket = new BasketClientDTO(purchaseDTO.getStoreID(), StoreController.getInstance().getStoreName(purchaseDTO.getStoreID()), productsDTO, amounts);
    }

    public PurchaseDTO toDTO(){
        // TODO sort out product issue then implement
        List<ProductIntPair> basketList = new Vector<>();

        List<ProductClientDTO> products = new LinkedList<>(basket.getProductsDTO());
        List<Integer> amounts = new LinkedList<>(basket.getAmounts());

        for(int i = 0; i < products.size(); i++){
            basketList.add(new ProductIntPair(Product.createProduct(products.get(i)).toDTO(), amounts.get(i)));
        }

        return new PurchaseDTO(this.basket.getStoreID(), basketList, this.totalPrice, this.purchaseDate);
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

    public void setPrice(double newPrice) {
       this.totalPrice = newPrice;
    }
}
