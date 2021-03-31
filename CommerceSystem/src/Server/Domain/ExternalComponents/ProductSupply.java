package Server.Domain.ExternalComponents;

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
    public boolean deliver (Map<Integer,Integer> products){
        return true; /* as to version 1, this delivery will always work */
    }
}
