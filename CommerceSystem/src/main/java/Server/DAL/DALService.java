package Server.DAL;

import Server.DAL.DiscountRuleDTOs.StoreDiscountRuleDTO;
import Server.Domain.UserManager.UserStateEnum;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoConfigurationException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.experimental.MorphiaSession;
import dev.morphia.mapping.Mapper;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import dev.morphia.query.experimental.filters.Filters;
import java.util.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


public class DALService {

    private Map<Integer, StoreDTO> stores;
    private Map<String, UserDTO> users;
    private Map<String, AccountDTO> accounts;
    private Map<String, AdminAccountDTO> admins;
    private Map<Integer, ProductDTO> products;
    private Map<Integer, PublisherDTO> publisher;

    private String dbName = "commerceDatabase";
    private String dbURL = "mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority";

    private boolean useLocal = true;

    private static class CreateSafeThreadSingleton {
        private static final DALService INSTANCE = new DALService();
    }

    public static DALService getInstance() {
        return DALService.CreateSafeThreadSingleton.INSTANCE;
    }

    private DALService() {
        if(useLocal){
            this.stores = new ConcurrentHashMap<>();
            this.users = new ConcurrentHashMap<>();
            this.accounts = new ConcurrentHashMap<>();
            this.admins = new ConcurrentHashMap<>();
            this.products = new ConcurrentHashMap<>();
            this.publisher = new ConcurrentHashMap<>();
        }

        MongoClient mongoClient = MongoClients.create(this.dbURL);
        Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

        Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
        mapper.mapPackage("Server.DAL");
        List<Class> classes = new Vector<>();
        classes.add(StoreDiscountRuleDTO.class);
        mapper.map(classes);

        mongoClient.close();
    }

    public void savePurchase(UserDTO userDTO, List<StoreDTO> storeDTOs) {
        if(useLocal){
            if(userDTO.getState() != UserStateEnum.GUEST) {
                this.users.put(userDTO.getName(), userDTO);
            }

            for(StoreDTO storeDTO : storeDTOs){
                this.stores.put(storeDTO.getStoreID(), storeDTO);
            }
        }
        else {
            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            try (MorphiaSession session = datastore.startSession()) {
                session.startTransaction();

                // stage changes to commit
                if (userDTO.getState() != UserStateEnum.GUEST)
                    session.save(userDTO);

                session.save(storeDTOs);

                // commit changes
                session.commitTransaction();
            }

            mongoClient.close();
        }
    }

    public PublisherDTO getPublisher() {
        if(useLocal){
            PublisherDTO publisherDTO = this.publisher.get(0);
            return publisherDTO == null ? new PublisherDTO() : publisherDTO;
        }
        else {
            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            PublisherDTO publisherDTO = datastore.find(PublisherDTO.class).first();

            mongoClient.close();

            return publisherDTO;
        }
    }

    public void savePublisher(PublisherDTO publisherDTO) {
        if(useLocal){
            this.publisher.put(0, publisherDTO);
        }
        else {
            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            datastore.save(publisherDTO);

            mongoClient.close();
        }
    }

    public void addAccount(AccountDTO accountDTO) {
        if(useLocal){
            this.accounts.put(accountDTO.getUsername(), accountDTO);
        }
        else {
            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            datastore.save(accountDTO);

            mongoClient.close();
        }
    }

    public AccountDTO getAccount(String username){
        if(useLocal){
            return this.accounts.get(username);
        }
        else {
            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            AccountDTO accountDTO = datastore.find(AccountDTO.class)
                    // filters find relevant entries
                    .filter(
                            Filters.eq("username", username)
                    ).first();

            mongoClient.close();

            return accountDTO;
        }
    }

    public void addAdmin(AdminAccountDTO adminAccountDTO) {
        if(useLocal){
            this.admins.put(adminAccountDTO.getUsername(), adminAccountDTO);
        }
        else {
            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            datastore.save(adminAccountDTO);

            mongoClient.close();
        }
    }

    public void insertStore(StoreDTO storeDTO) {
        if(useLocal){
            stores.put(storeDTO.getStoreID(), storeDTO);
        }
        else {

            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            datastore.save(storeDTO);

            mongoClient.close();
        }
    }

    public StoreDTO getStore(int storeId) {
        if(useLocal){
            return this.stores.get(storeId);
        }
        else {
            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            StoreDTO storeDTO = datastore.find(StoreDTO.class)
                    // filters find relevant entries
                    .filter(
                            Filters.eq("storeID", storeId)
                    ).first();

            mongoClient.close();

            return storeDTO;
        }
    }

    public Collection<StoreDTO> getAllStores() {
        if(useLocal){
            return stores.values();
        }
        else {

            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            List<StoreDTO> storeDTOList = datastore.find(StoreDTO.class)
                    .filter(
                            Filters.gte("storeID", 0)
                    )
                .iterator(new FindOptions()
                        .sort(Sort.ascending("storeID")))
                    .toList();

            mongoClient.close();

            return storeDTOList;
        }
    }

    public void saveUserAndStore(UserDTO userDTO, StoreDTO storeDTO){
        if(useLocal){
            if (userDTO.getState() != UserStateEnum.GUEST)
                this.users.put(userDTO.getName(), userDTO);
            this.stores.put(storeDTO.getStoreID(), storeDTO);
        }
        else {

            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            try (MorphiaSession session = datastore.startSession()) {
                session.startTransaction();

                // stage changes to commit
                if (userDTO.getState() != UserStateEnum.GUEST)
                    session.save(userDTO);

                session.save(storeDTO);

                // commit changes
                session.commitTransaction();
            }

            mongoClient.close();
        }
    }

    public void saveUserStoreAndProduct(UserDTO userDTO, StoreDTO storeDTO, ProductDTO productDTO){
        if(useLocal){
            if (userDTO.getState() != UserStateEnum.GUEST)
                users.put(userDTO.getName(), userDTO);
            stores.put(storeDTO.getStoreID(), storeDTO);
            products.put(productDTO.getProductID(), productDTO);
        }
        else {

            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            try (MorphiaSession session = datastore.startSession()) {
                session.startTransaction();

                // stage changes to commit
                if (userDTO.getState() != UserStateEnum.GUEST)
                    session.save(userDTO);

                session.save(storeDTO);
                session.save(productDTO);

                // commit changes
                session.commitTransaction();
            }

            mongoClient.close();
        }
    }

    public void saveStoreAndProduct(StoreDTO storeDTO, ProductDTO productDTO){
        if(useLocal){
            stores.put(storeDTO.getStoreID(), storeDTO);
            products.put(productDTO.getProductID(), productDTO);
        }
        else {

            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            try (MorphiaSession session = datastore.startSession()) {
                session.startTransaction();

                // stage changes to commit
                session.save(storeDTO);
                session.save(productDTO);

                // commit changes
                session.commitTransaction();
            }

            mongoClient.close();
        }
    }

    public void saveStoreRemoveProduct(StoreDTO storeDTO, ProductDTO productDTO){
        if(useLocal){
            stores.put(storeDTO.getStoreID(), storeDTO);
            products.remove(productDTO.getProductID());
        }
         else {

            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            try (MorphiaSession session = datastore.startSession()) {
                session.startTransaction();

                // stage changes to commit
                session.save(storeDTO);
                session.delete(productDTO);

                // commit changes
                session.commitTransaction();
            }

            mongoClient.close();
        }
    }

    public UserDTO getUser(String username){
        if(useLocal){
            return this.users.get(username);
        }
        else {

            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            UserDTO userDTO = datastore.find(UserDTO.class)
                    // filters find relevant entries
                    .filter(
                            Filters.eq("name", username)
                    ).first();

            mongoClient.close();

            return userDTO;
        }
    }

    public void insertUser(UserDTO userDTO){
        if(useLocal) {
            if (userDTO.getState() != UserStateEnum.GUEST)
                users.put(userDTO.getName(), userDTO);
        }
        else {

            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            if (userDTO.getState() != UserStateEnum.GUEST)
                datastore.save(userDTO);

            mongoClient.close();
        }
    }

    public boolean saveUsers(List<UserDTO> userDTOList){
        if(useLocal){
            for(UserDTO userDTO: userDTOList){
                if (userDTO.getState() != UserStateEnum.GUEST)
                    users.put(userDTO.getName(), userDTO);
            }
            return true;
        }
        else {

            MongoClient mongoClient = MongoClients.create(this.dbURL);
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            boolean success = true;

            try (MorphiaSession session = datastore.startSession()) {
                session.startTransaction();

                // stage changes to commit
                session.save(userDTOList);

                // commit changes
                session.commitTransaction();
            } catch (Exception e) {
                success = false;
            }

            mongoClient.close();
            return success;
        }
    }


    public int getNextAvailableStoreID(){
        if(useLocal){
            return this.stores.size();
        }
//        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
//        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");
//
//        Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
//        //mapper.mapPackage("Server.DAL");
//        List<Class> classes = new Vector<>();
//        classes.add(StoreDiscountRuleDTO.class);
//        mapper.map(classes);
//
//        List<StoreDTO> storeDTOs = datastore.find(StoreDTO.class)
//                // filters find relevant entries
//                .filter(
//                        Filters.gte("storeID", 0)
//                )
//                // iterator options manipulate the found entries
//                .iterator(
//                        new FindOptions()
//                                .sort(Sort.descending("storeID"))
//                ).toList();
//
//        mongoClient.close();
//
//        if(storeDTOs == null || storeDTOs.size() == 0){
//            return 0;
//        }
//        StoreDTO head = storeDTOs.get(0);
//        int id = head.getStoreID();
//        return id + 1;
        else {
            return (int) (Math.random() * (10000 - 1)) + 1;
        }
    }

    public void resetDatabase(){
        try (
                MongoClient mongoClient = MongoClients.create(this.dbURL)
        ){
            //MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
            mongoClient.getDatabase(this.dbName).getCollection("users").drop();
            mongoClient.getDatabase(this.dbName).getCollection("stores").drop();
            mongoClient.getDatabase(this.dbName).getCollection("publishers").drop();
            mongoClient.getDatabase(this.dbName).getCollection("products").drop();
            mongoClient.getDatabase(this.dbName).getCollection("accounts").drop();
            mongoClient.getDatabase(this.dbName).getCollection("adminAccounts").drop();
            mongoClient.close();
        }
        catch(MongoConfigurationException e){
            resetDatabase(); // timeout, try again
        }
    }




}
