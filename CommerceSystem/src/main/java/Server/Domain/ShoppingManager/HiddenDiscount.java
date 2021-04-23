package Server.Domain.ShoppingManager;

import java.time.LocalDate;
import java.util.List;

public class HiddenDiscount implements Discount{
    int productId;
    double percentage;
    LocalDate dueDate;
    long couponCode;

    public HiddenDiscount(int productId, double percentage, long duration, long couponCode){
        this.productId = productId;
        this.percentage = percentage;
        this.dueDate = LocalDate.now().plusDays(duration);
        this.couponCode = couponCode;
    }

    @Override
    public double getDiscount(List<Product> productList) {
        return 0;
    }
}
