package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.Predicates.Predicate;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public abstract class LeafPurchaseRule implements PurchaseRule {
    protected int id;
    protected Predicate predicate;

    public LeafPurchaseRule(int id, Predicate predicate) {
        this.id = id;
        this.predicate = predicate;
    }

    @Override
    public abstract boolean isValidPurchase(Map<ProductDTO, Integer> shoppingBasket);

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getDescription() {
        return "Simple Purchase rule: " + id;
    }

    @Override
    public void setID(int id){
        this.id = id;
    }
}
