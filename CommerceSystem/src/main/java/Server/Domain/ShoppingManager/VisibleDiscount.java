package Server.Domain.ShoppingManager;

import java.time.LocalDate;
import java.util.List;

public class VisibleDiscount implements Discount{
    private int productId;
    private double percentage;
    private LocalDate dueDate;

    public VisibleDiscount(int productId, double percentage, long duration){
        this.productId = productId;
        this.percentage = percentage;
        this.dueDate = LocalDate.now().plusDays(duration);
    }

    @Override
    public double getDiscount(List<Product> productList) {
        double sum = 0.0;
        for(Product product : productList)
            if(product.getProductID() == productId)
                sum += product.getPrice() * (percentage / 100);
        return sum;
    }

    @Override
    public String toString() {
        return  "Discount info:\n" +
                "productId: " + productId +
                "\n percentage: " + percentage +
                "%\n dueDate: " + dueDate;
    }
}
