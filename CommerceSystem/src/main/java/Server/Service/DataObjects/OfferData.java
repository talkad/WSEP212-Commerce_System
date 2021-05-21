package Server.Service.DataObjects;

public class OfferData {

    String name;
    String productName;
    int productID;
    int storeID;
    double priceOffer;

    public OfferData(String name, String productName, int productID, int storeID, double priceOffer) {
        this.name = name;
        this.productName = productName;
        this.productID = productID;
        this.storeID = storeID;
        this.priceOffer = priceOffer;
    }
}
