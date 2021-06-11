package Server.DAL.DALControllers;

import Server.DAL.DomainDTOs.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


 interface DALInterface {
    
     DailyCountersDTO getDailyCounters(LocalDate date);
     void saveCounters(DailyCountersDTO dailyCountersDTO);

     void savePurchase(UserDTO userDTO, List<StoreDTO> storeDTOs);

     PublisherDTO getPublisher();

     void savePublisher(PublisherDTO publisherDTO);

     void addAccount(AccountDTO accountDTO);

     AccountDTO getAccount(String username);

     void addAdmin(AdminAccountDTO adminAccountDTO);

     void insertStore(StoreDTO storeDTO);

     StoreDTO getStore(int storeId);

     Collection<StoreDTO> getAllStores();

     void saveUserAndStore(UserDTO userDTO, StoreDTO storeDTO);

     void saveUserStoreAndProduct(UserDTO userDTO, StoreDTO storeDTO, ProductDTO productDTO);

     void saveStoreAndProduct(StoreDTO storeDTO, ProductDTO productDTO);

     void saveStoreRemoveProduct(StoreDTO storeDTO, ProductDTO productDTO);

     UserDTO getUser(String username);

     void insertUser(UserDTO userDTO);

     boolean saveUsers(List<UserDTO> userDTOList);

     int getNextAvailableStoreID();

     void resetDatabase();

     void setName(String dbName);

     public String getName();

     void setURL(String dbURL);

     void setUseLocal(boolean useLocal);

     boolean checkConnection();

}
