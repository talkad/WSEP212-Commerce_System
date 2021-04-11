package Server.Domain.ShoppingManager;

public class PurchasePolicy {
    private int type;

    public PurchasePolicy(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
