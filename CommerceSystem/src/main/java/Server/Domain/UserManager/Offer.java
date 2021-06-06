package Server.Domain.UserManager;

import Server.DAL.OfferDTO;
import Server.Domain.ShoppingManager.StoreController;

import java.util.List;
import java.util.Vector;

public class Offer {
    private int productId;
    private int storeId;
    private double offerReply;
    private OfferState state;
    private List<String> approvals;

    // todo - jacob need to update its DTO

    public Offer(int productId, int storeId, double offerReply, List<String> approvals){
        this.productId = productId;
        this.storeId = storeId;
        this.offerReply = offerReply;
        this.state = OfferState.PENDING;
        this.approvals = approvals;
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
        this.approvals = offerDTO.getApprovals();
    }

    public OfferDTO toDTO(){
        return new OfferDTO(this.getProductId(), this.getStoreId(), this.getOfferReply(), this.getState(), this.getApprovals());
    }

    public int getProductId() {
        return this.productId;
    }

    public int getStoreId() {
        return this.productId;
    }

    public double getOfferReply(){
        return this.offerReply;
    }

    public void setOfferReply(double offerReply) {
        this.offerReply = offerReply;
    }

    public OfferState getState() {
        return state;
    }

    public void setState(String username, OfferState state) {
        if(state == OfferState.APPROVED){
            approvals.remove(username);

            if(approvals.size() == 0)
                this.state = OfferState.APPROVED;
        }
    }

    public List<String> getApprovals(){
        return this.approvals;
    }
}
