package Server.Domain.UserManager;

import Server.Domain.ExternalComponents.ProductSupply;

import java.util.Map;

/**
 * This class in the only class who communicates with the external delivery system,
 * and should get one as parameter when created
 */
public class ProductSupplyAdapter {
    ProductSupply externalSupplier;

    public ProductSupplyAdapter (ProductSupply externalSupplier){
        this.externalSupplier = externalSupplier;
    }

    public boolean deliver (Map<Integer,Integer> details){ /* change this map to delivery details as we with */
        return externalSupplier.deliver(details);
    }
}
