package Domain.ShoppingManager;

import java.time.LocalDate;

public class VisibleDiscount implements Discount{
    private double percentage;
    private LocalDate dueDate;

    public VisibleDiscount(double percentage, long duration){
        this.percentage = percentage;
        this.dueDate = LocalDate.now().plusDays(duration);
    }

    @Override
    public double getDiscount() {
        return 0;
    }
}
