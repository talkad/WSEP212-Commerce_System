package Server.DAL;

import Server.Domain.UserManager.OfferState;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("OfferDTO")

public class OfferDTO {

    @Property(value = "productID")
    private int productId;

    @Property(value = "storeID")
    private int storeId;

    @Property(value = "offerReply")
    private double offerReply;

    @Property(value = "state")
    private OfferState state;

    @Property(value = "approvals")
    private List<String> approvals;

    public OfferDTO(){
        // For Morphia
    }

    public OfferDTO(int productId, int storeId, double offerReply, OfferState state, List<String> approvals) {
        this.productId = productId;
        this.storeId = storeId;
        this.offerReply = offerReply;
        this.state = state;
        this.approvals = approvals;
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

    public List<String> getApprovals() {
        return approvals == null ? new Vector<>() : approvals;
    }

    public void setApprovals(List<String> approvals) {
        this.approvals = approvals;
    }
}
