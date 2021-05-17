package Server.Service.DataObjects;

public class OfferData {

    String name;
    int productID;
    double priceOffer;

    public OfferData(String name, int productID, double priceOffer) {
        this.name = name;
        this.productID = productID;
        this.priceOffer = priceOffer;
    }
}
