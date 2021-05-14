package Server.Domain.UserManager;

public class Offer {
    private int productId;
    private int storeId;
    private double offerReply;
    private OfferState state;

    public Offer(int productId, int storeId, double offerReply){
        this.productId = productId;
        this.storeId = storeId;
        this.offerReply = offerReply;
        this.state = OfferState.PENDING;
    }

    public Offer(int productId, int storeId, double offerReply, OfferState state){
        this.productId = productId;
        this.storeId = storeId;
        this.offerReply = offerReply;
        this.state = state;
    }


    public int getProductId() {
        return this.productId;
    }

    public int getStoreId() {
        return this.productId;
    }

    public double getOfferReply(){
        return this.productId;
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
