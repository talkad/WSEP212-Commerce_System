package Server.DAL;

import Server.Domain.ShoppingManager.Store;

import java.util.Collection;

public class DALService {

    private DALService() {
        
    }

    public void insertStore(StoreDTO toDTO) {
    }

    public Store getStore(int storeId) {
    }

    public Collection<Store> getAllStores() {
    }

    private static class CreateSafeThreadSingleton {
        private static final DALService INSTANCE = new DALService();
    }
    
    public static DALService getInstance() {
        return DALService.CreateSafeThreadSingleton.INSTANCE;
    }
}
