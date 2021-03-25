package Domain.ShoppingManager;

import java.util.List;

public class ProductDTO {

    private String name;
    private double price;
    private List<String> categories;
    private int storeID;

    public ProductDTO(String name, double price, List<String> categories, int storeID) {
        this.name = name;
        this.price = price;
        this.categories = categories;
        this.storeID = storeID;
    }
}
