package Server.Domain.UserManager;

import Server.DAL.OfferDTO;

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

    public Offer(OfferDTO offerDTO){
        this.productId = offerDTO.getProductId();
        this.storeId = offerDTO.getStoreId();
        this.offerReply = offerDTO.getOfferReply();
        this.state = offerDTO.getState();
    }

    public OfferDTO toDTO(){
        return new OfferDTO(this.getProductId(), this.getStoreId(), this.getOfferReply(), this.getState());
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
