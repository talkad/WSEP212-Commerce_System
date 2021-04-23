package Server.Domain.ShoppingManager;

import java.time.LocalDate;
import java.util.List;

public class ConditionalDiscount implements Discount{
    int productId;
    double percentage;
    LocalDate dueDate;
    Conditional condition;
    public ConditionalDiscount(int productId, double percentage, long duration, Conditional condition){
        this.productId = productId;
        this.percentage = percentage;
        this.dueDate = LocalDate.now().plusDays(duration);
        this.condition = condition;
    }

    @Override
    public double getDiscount(List<Product> productList) {
        int amount = 0;
        for(Product product : productList)
            if(product.getProductID() == productId)
                ++amount;
        return (condition.hasMinAmount(amount) ? (amount * (percentage / 100)) : 0 );
    }

    @Override
    public String toString() {
        return  "Discount info:\n" +
                "productId: " + productId +
                "\n percentage: " + percentage +
                "%\n dueDate: " + dueDate +
                "\n condition: " + condition;
    }
}
