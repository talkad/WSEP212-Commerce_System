package Server.Domain.ShoppingManager;

public class DiscountPolicy {
    private int type;

    public DiscountPolicy(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
