package Server.DAL;

import Server.Domain.UserManager.OfferState;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
public class OfferDTO {

    @Property(value = "productID")
    private int productId;

    @Property(value = "storeID")
    private int storeId;

    @Property(value = "offerReply")
    private double offerReply;

    @Property(value = "state")
    private OfferState state;

    public OfferDTO(){
        // For Morphia
    }

    public OfferDTO(int productId, int storeId, double offerReply, OfferState state) {
        this.productId = productId;
        this.storeId = storeId;
        this.offerReply = offerReply;
        this.state = state;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public double getOfferReply() {
        return offerReply;
    }

    public void setOfferReply(double offerReply) {
        this.offerReply = offerReply;
    }

    public OfferState getState() {
        return state;
    }

    public void setState(OfferState state) {
        this.state = state;
    }
}
