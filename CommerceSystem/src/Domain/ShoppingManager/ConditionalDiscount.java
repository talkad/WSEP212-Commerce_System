package Domain.ShoppingManager;

import java.time.LocalDate;

public class ConditionalDiscount implements Discount{
    double percentage;
    LocalDate dueDate;

    @Override
    public double getDiscount() {
        return 0;
    }
}
