package Server.Domain.UserManager.ExternalSystemsAdapters.ExternalSystemsMock;

import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;

public class SupplySystemMock {

    private SupplySystemMock(){
    }

    // Inner class to provide instance of class
    private static class CreateThreadSafeSingleton
    {
        private static final SupplySystemMock INSTANCE = new SupplySystemMock();
    }

    public static SupplySystemMock getInstance()
    {
        return SupplySystemMock.CreateThreadSafeSingleton.INSTANCE;
    }

    public int supply(SupplyDetails supplyDetails){
        if(supplyDetails.getAddress().length() == 0)
            return -1;
        return 10000;
    }

    public int cancelSupply(int transactionID){
        if(transactionID >= 50000)
            return -1;
        return 1;
    }
}
