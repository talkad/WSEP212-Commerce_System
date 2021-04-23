package Server.Domain.ShoppingManager;

public class Conditional {
    private int productNum;

    public Conditional(int productNum){
        this.productNum = productNum;
    }

    public boolean hasMinAmount(int amount) {
        return amount >= this.productNum;
    }

    @Override
    public String toString() {
        return "Must contain at least " + productNum + "products to receive the discount";
    }
}
