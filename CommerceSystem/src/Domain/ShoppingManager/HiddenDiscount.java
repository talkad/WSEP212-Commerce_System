package Domain.ShoppingManager;

import java.time.LocalDate;

public class HiddenDiscount implements Discount{
    double percentage;
    LocalDate dueDate;
    long couponCode;

    public HiddenDiscount(double percentage, long duration, long couponCode){
        this.percentage = percentage;
        this.dueDate = LocalDate.now().plusDays(duration);
        this.couponCode = couponCode;
    }

    @Override
    public double getDiscount() {
        return 0;
    }
}
