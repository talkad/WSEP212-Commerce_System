package Server.DAL.DALControllers;

import Server.DAL.DomainDTOs.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class DALProxy implements DALInterface{

    private String DBname;
    private DALService real;
    private DALTestService test;

    private static class CreateSafeThreadSingleton {
        private static final DALProxy INSTANCE = new DALProxy();
    }

    public static DALProxy getInstance() {
        return DALProxy.CreateSafeThreadSingleton.INSTANCE;
    }

    private DALProxy() {
        real = DALService.getInstance();
        test = DALTestService.getInstance();
        DBname = DALService.getInstance().getName();
    }

    public DailyCountersDTO getDailyCounters(LocalDate date){
        if(this.DBname.equals("testDatabase")){
            return test.getDailyCounters(date);
        }
        else return real.getDailyCounters(date);
    }
    public void saveCounters(DailyCountersDTO dailyCountersDTO){
        if(this.DBname.equals("testDatabase")){
            test.saveCounters(dailyCountersDTO);
        }
        else real.saveCounters(dailyCountersDTO);
    }

    public void savePurchase(UserDTO userDTO, List<StoreDTO> storeDTOs){
        if(this.DBname.equals("testDatabase")){
            test.savePurchase(userDTO, storeDTOs);
        }
        else real.savePurchase(userDTO, storeDTOs);
    }

    public PublisherDTO getPublisher(){
        if(this.DBname.equals("testDatabase")){
            return test.getPublisher();
        }
        else return real.getPublisher();
    }

    public void savePublisher(PublisherDTO publisherDTO){
        if(this.DBname.equals("testDatabase")){
            test.savePublisher(publisherDTO);
        }
        else real.savePublisher(publisherDTO);
    }

    public void addAccount(AccountDTO accountDTO){
        if(this.DBname.equals("testDatabase")){
            test.addAccount(accountDTO);
        }
        else real.addAccount(accountDTO);
    }

    public AccountDTO getAccount(String username){
        if(this.DBname.equals("testDatabase")){
            return test.getAccount(username);
        }
        else return real.getAccount(username);
    }


    public void addAdmin(AdminAccountDTO adminAccountDTO){
        if(this.DBname.equals("testDatabase")){
            test.addAdmin(adminAccountDTO);
        }
        else real.addAdmin(adminAccountDTO);
    }

    public void insertStore(StoreDTO storeDTO){
        if(this.DBname.equals("testDatabase")){
            test.insertStore(storeDTO);
        }
        else real.insertStore(storeDTO);
    }

    public StoreDTO getStore(int storeId){
        if(this.DBname.equals("testDatabase")){
            return test.getStore(storeId);
        }
        else return real.getStore(storeId);
    }

    public Collection<StoreDTO> getAllStores(){
        if(this.DBname.equals("testDatabase")){
            return test.getAllStores();
        }
        else return real.getAllStores();
    }

    public void saveUserAndStore(UserDTO userDTO, StoreDTO storeDTO){
        if(this.DBname.equals("testDatabase")){
            test.saveUserAndStore(userDTO, storeDTO);
        }
        else{
            real.saveUserAndStore(userDTO, storeDTO);
        }
    }

    public void saveUserStoreAndProduct(UserDTO userDTO, StoreDTO storeDTO, ProductDTO productDTO){
        if(this.DBname.equals("testDatabase")){
            test.saveUserStoreAndProduct(userDTO, storeDTO, productDTO);
        }
        else{
            real.saveUserStoreAndProduct(userDTO, storeDTO, productDTO);
        }
    }

    public void saveStoreAndProduct(StoreDTO storeDTO, ProductDTO productDTO){
        if(this.DBname.equals("testDatabase")){
            test.saveStoreAndProduct(storeDTO, productDTO);
        }
        else{
            real.saveStoreAndProduct(storeDTO, productDTO);
        }
    }

    public void saveStoreRemoveProduct(StoreDTO storeDTO, ProductDTO productDTO){
        if(this.DBname.equals("testDatabase")){
            test.saveStoreRemoveProduct(storeDTO, productDTO);
        }
        else{
            real.saveStoreRemoveProduct(storeDTO, productDTO);
        }
    }

    public UserDTO getUser(String username){
        if(this.DBname.equals("testDatabase")){
            return test.getUser(username);
        }
        return real.getUser(username);
    }

    public void insertUser(UserDTO userDTO){
        if(this.DBname.equals("testDatabase")){
            test.insertUser(userDTO);
        }
        else{
            real.insertUser(userDTO);
        }
    }

    public boolean saveUsers(List<UserDTO> userDTOList){
        if(this.DBname.equals("testDatabase")){
            return test.saveUsers(userDTOList);
        }
        return real.saveUsers(userDTOList);
    }

    public int getNextAvailableStoreID(){
        if(this.DBname.equals("testDatabase")){
            return test.getNextAvailableStoreID();
        }
        return real.getNextAvailableStoreID();
    }

    public void resetDatabase(){
        if(this.DBname.equals("testDatabase")){
            test.resetDatabase();
        }
        else{
            real.resetDatabase();
        }
    }

    public void setName(String dbName){
        if(this.DBname.equals("testDatabase")){
            test.setName(dbName);
        }
        else{
            real.setName(dbName);
        }
    }

    public String getName(){
        if(this.DBname.equals("testDatabase")){
            return test.getName();
        }
        return real.getName();
    }

    public void setURL(String dbURL){
        if(this.DBname.equals("testDatabase")){
            test.setURL(dbURL);
        }
        else{
            real.setURL(dbURL);
        }
    }

    public void setUseLocal(boolean useLocal){
        if(this.DBname.equals("testDatabase")){
            test.setUseLocal(useLocal);
        }
        else{
            real.setUseLocal(useLocal);
        }
    }

    public boolean checkConnection(){
        if(this.DBname.equals("testDatabase")){
            return test.checkConnection();
        }
        return real.checkConnection();
    }

}
