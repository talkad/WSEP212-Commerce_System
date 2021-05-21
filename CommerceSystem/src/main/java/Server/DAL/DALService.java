package Server.DAL;

import java.util.*;

import java.util.Collection;

public class DALService {

    public void savePurchase(UserDTO toDTO, List<StoreDTO> storeDTOS) {
    }

    private static class CreateSafeThreadSingleton {
        private static final DALService INSTANCE = new DALService();
    }

    public static DALService getInstance() {
        return DALService.CreateSafeThreadSingleton.INSTANCE;
    }

    private DALService() {
    }

    public void addAccount(AccountDTO accountDTO) {
    }

    public AccountDTO getAccount(String username){
        return null;
    }

    public void addAdmin(AdminAccountDTO adminAccountDTO) {
    }

    public void insertStore(StoreDTO storeDTO) {
    }

    public StoreDTO getStore(int storeId) {
        return null;
    }

    public Collection<StoreDTO> getAllStores() {
        return null;
    }

    public void saveUserAndStore(UserDTO userDTO, StoreDTO storeDTO){

    }

    public void saveUserStoreAndProduct(UserDTO userDTO, StoreDTO storeDTO, ProductDTO productDTO){

    }

    public void saveStoreAndProduct(StoreDTO storeDTO, ProductDTO productDTO){

    }

    public void saveStoreRemoveProduct(StoreDTO storeDTO, ProductDTO productDTO){

    }

    public UserDTO getUser(String username){
        return null;
    }

    public void insertUser(UserDTO userDTO){

    }

    public boolean saveUsers(List<UserDTO> userDTOList){
        return true;
    }

    public int getNextAvailableUserID(){
        return 0;
    }

    public void removeFromUserCache(String username){

    }

    public void resetDatabase(){

    }




}
