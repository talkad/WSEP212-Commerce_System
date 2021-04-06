package Server.Domain.UserManager;

import Server.Domain.ExternalComponents.ProductSupply;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

/**
 * This class in the only class who communicates with the external delivery system,
 * and should get one as parameter when created
 */

public class ProductSupplyAdapter
{
    private ProductSupply externalSupplier;

    private ProductSupplyAdapter(ProductSupply externalSupplier){
        this.externalSupplier = externalSupplier;
    }

    // Inner class to provide instance of class
    private static class CreateThreadSafeSingleton
    {
        private static final ProductSupplyAdapter INSTANCE = new ProductSupplyAdapter(new ProductSupply());
    }

    public static ProductSupplyAdapter getInstance()
    {
        return CreateThreadSafeSingleton.INSTANCE;
    }

    public boolean deliver (String location, Map<Integer,Map<ProductDTO, Integer>> details){ /* change this map to delivery details as we with */
        return externalSupplier.deliver(location, details);
    }
}
