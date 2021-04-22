package Server.Domain.ExternalComponents;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;


/**
 * This class represents external delivery system
 * We only communicates with this class using the ProductSupplyAdapter
 */
public class ProductSupply {
    /*
    Actual delivery from the external system
    requires map of <Product ID, Amount>
     */
    public void deliver (String location, Map<Integer,Map<ProductDTO, Integer>> products){
        /* as to version 1, this delivery will always work */
    }

    /**
     * Only delivery to israel is possible
     * @param location - address for delivery
     * @return if the system can deliver to specific location
     */
    public boolean canDeliver(String location, Map<Integer,Map<ProductDTO, Integer>> details){
        return location.contains("Israel") || location.contains("israel");
    }

}
