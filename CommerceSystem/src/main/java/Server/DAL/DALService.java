package Server.DAL;

import Server.DAL.DiscountRuleDTOs.StoreDiscountRuleDTO;
import Server.Domain.UserManager.UserStateEnum;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoConfigurationException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.experimental.MorphiaSession;
import dev.morphia.mapping.MappedClass;
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
    private Map<String, ShoppingCartDTO> guestCarts;

    private String dbName = "commerceDatabase";
    private String dbURL = "mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority";

    private boolean useLocal = false;

    public void useTestDatabase() {
        dbName = "testDatabase";
    }

    private static class CreateSafeThreadSingleton {
        private static final DALService INSTANCE = new DALService();
    }

    public static DALService getInstance() {
        return DALService.CreateSafeThreadSingleton.INSTANCE;
    }

    private DALService() {
        this.stores = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.accounts = new ConcurrentHashMap<>();
        this.admins = new ConcurrentHashMap<>();
        this.products = new ConcurrentHashMap<>();
        this.publisher = new ConcurrentHashMap<>();
        this.guestCarts = new ConcurrentHashMap<>();
    }

    public void savePurchase(UserDTO userDTO, List<StoreDTO> storeDTOs) {
        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCarts.put(userDTO.getName(), userDTO.getShoppingCart());
        }
        if(useLocal){
            if(userDTO.getState() != UserStateEnum.GUEST) {
                this.users.put(userDTO.getName(), userDTO);
            }

            for(StoreDTO storeDTO : storeDTOs){
                this.stores.put(storeDTO.getStoreID(), storeDTO);
            }
        }
        else {
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                try (MorphiaSession session = datastore.startSession()) {
                    session.startTransaction();

                    // stage changes to commit
                    if (userDTO.getState() != UserStateEnum.GUEST)
                        session.save(userDTO);

                    session.save(storeDTOs);

                    // commit changes
                    session.commitTransaction();
                }
                catch (Exception e){
                    System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
                }
            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                savePurchase(userDTO, storeDTOs); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                savePurchase(userDTO, storeDTOs); // timeout, try again
            }
        }
    }

    public PublisherDTO getPublisher() {
        if(useLocal){
            PublisherDTO publisherDTO = this.publisher.get(0);
            return publisherDTO == null ? new PublisherDTO() : publisherDTO;
        }
        else {
            PublisherDTO publisherDTO = null;
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                publisherDTO = datastore.find(PublisherDTO.class).first();

            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                return getPublisher(); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                return getPublisher(); // timeout, try again
            }
            return publisherDTO;
        }
    }

    public void savePublisher(PublisherDTO publisherDTO) {
        if(useLocal){
            this.publisher.put(0, publisherDTO);
        }
        else {
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {

                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                datastore.save(publisherDTO);
            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                savePublisher(publisherDTO); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                savePublisher(publisherDTO); // timeout, try again
            }
        }
    }

    public void addAccount(AccountDTO accountDTO) {
        if(useLocal){
            this.accounts.put(accountDTO.getUsername(), accountDTO);
        }
        else {
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                datastore.save(accountDTO);
            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                addAccount(accountDTO); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                addAccount(accountDTO); // timeout, try again
            }
        }
    }

    public AccountDTO getAccount(String username){
        if(useLocal){
            return this.accounts.get(username);
        }
        else {
            AccountDTO accountDTO = null;
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                accountDTO = datastore.find(AccountDTO.class)
                        // filters find relevant entries
                        .filter(
                                Filters.eq("username", username)
                        ).first();
            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                return getAccount(username); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                return getAccount(username); // timeout, try again
            }

            return accountDTO;
        }
    }

    public void addAdmin(AdminAccountDTO adminAccountDTO) {
        if(useLocal){
            this.admins.put(adminAccountDTO.getUsername(), adminAccountDTO);
        }
        else {
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                datastore.save(adminAccountDTO);

            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                addAdmin(adminAccountDTO); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                addAdmin(adminAccountDTO); // timeout, try again
            }
        }
    }

    public void insertStore(StoreDTO storeDTO) {
        if(useLocal){
            stores.put(storeDTO.getStoreID(), storeDTO);
        }
        else {

            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                datastore.save(storeDTO);
            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                insertStore(storeDTO); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                insertStore(storeDTO); // timeout, try again
            }
        }
    }

    public StoreDTO getStore(int storeId) {
        if(useLocal){
            return this.stores.get(storeId);
        }
        else {
            StoreDTO storeDTO = null;
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                storeDTO = datastore.find(StoreDTO.class)
                        // filters find relevant entries
                        .filter(
                                Filters.eq("storeID", storeId)
                        ).first();
            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                return getStore(storeId); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                return getStore(storeId); // timeout, try again
            }

            return storeDTO;
        }
    }

    public Collection<StoreDTO> getAllStores() {
        if(useLocal){
            return stores.values();
        }
        else {
            List<StoreDTO> storeDTOList = null;
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                storeDTOList = datastore.find(StoreDTO.class)
                        .filter(
                                Filters.gte("storeID", 0)
                        )
                        .iterator(new FindOptions()
                                .sort(Sort.ascending("storeID")))
                        .toList();
            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                return getAllStores(); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                return getAllStores(); // timeout, try again
            }

            return storeDTOList;
        }
    }

    public void saveUserAndStore(UserDTO userDTO, StoreDTO storeDTO){
        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCarts.put(userDTO.getName(), userDTO.getShoppingCart());
        }
        if(useLocal){
            if (userDTO.getState() != UserStateEnum.GUEST)
                this.users.put(userDTO.getName(), userDTO);
            this.stores.put(storeDTO.getStoreID(), storeDTO);
        }
        else {

            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                try (MorphiaSession session = datastore.startSession()) {
                    session.startTransaction();

                    // stage changes to commit
                    if (userDTO.getState() != UserStateEnum.GUEST)
                        session.save(userDTO);

                    session.save(storeDTO);

                    // commit changes
                    session.commitTransaction();
                }
                catch (Exception e){
                    System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
                }

            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                saveUserAndStore(userDTO, storeDTO); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                saveUserAndStore(userDTO, storeDTO); // timeout, try again
            }
        }
    }

    public void saveUserStoreAndProduct(UserDTO userDTO, StoreDTO storeDTO, ProductDTO productDTO){
        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCarts.put(userDTO.getName(), userDTO.getShoppingCart());
        }
        if(useLocal){
            if (userDTO.getState() != UserStateEnum.GUEST)
                users.put(userDTO.getName(), userDTO);
            stores.put(storeDTO.getStoreID(), storeDTO);
            products.put(productDTO.getProductID(), productDTO);
        }
        else {

            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

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
                catch (Exception e){
                    System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
                }

            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                saveUserStoreAndProduct(userDTO, storeDTO, productDTO); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                saveUserStoreAndProduct(userDTO, storeDTO, productDTO); // timeout, try again
            }
        }
    }

    public void saveStoreAndProduct(StoreDTO storeDTO, ProductDTO productDTO){
        if(useLocal){
            stores.put(storeDTO.getStoreID(), storeDTO);
            products.put(productDTO.getProductID(), productDTO);
        }
        else {

            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                try (MorphiaSession session = datastore.startSession()) {
                    session.startTransaction();

                    // stage changes to commit
                    session.save(storeDTO);
                    session.save(productDTO);

                    // commit changes
                    session.commitTransaction();
                }
                catch (Exception e){
                    System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
                }

            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                saveStoreAndProduct(storeDTO, productDTO); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                saveStoreAndProduct(storeDTO, productDTO); // timeout, try again
            }
        }
    }

    public void saveStoreRemoveProduct(StoreDTO storeDTO, ProductDTO productDTO){
        if(useLocal){
            stores.put(storeDTO.getStoreID(), storeDTO);
            products.remove(productDTO.getProductID());
        }
         else {

            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                try (MorphiaSession session = datastore.startSession()) {
                    session.startTransaction();

                    // stage changes to commit
                    session.save(storeDTO);
                    session.delete(productDTO);

                    // commit changes
                    session.commitTransaction();
                }
                catch (Exception e){
                    System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
                }
            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                saveStoreRemoveProduct(storeDTO, productDTO); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                saveStoreRemoveProduct(storeDTO, productDTO); // timeout, try again
            }
        }
    }

    public UserDTO getUser(String username){
        if(useLocal){
            return this.users.get(username);
        }
        else {
            UserDTO userDTO = null;
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                userDTO = datastore.find(UserDTO.class)
                        // filters find relevant entries
                        .filter(
                                Filters.eq("name", username)
                        ).first();
            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                return getUser(username); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                return getUser(username); // timeout, try again
            }
            return userDTO;
        }
    }

    public void insertUser(UserDTO userDTO){
        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCarts.put(userDTO.getName(), userDTO.getShoppingCart());
        }
        if(useLocal) {
            if (userDTO.getState() != UserStateEnum.GUEST)
                users.put(userDTO.getName(), userDTO);
        }
        else {

            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                if (userDTO.getState() != UserStateEnum.GUEST)
                    datastore.save(userDTO);

            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                insertUser(userDTO); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                insertUser(userDTO); // timeout, try again
            }
        }
    }

    public boolean saveUsers(List<UserDTO> userDTOList){
        for(UserDTO userDTO : userDTOList) {
            if (userDTO.getState() == UserStateEnum.GUEST) {
                this.guestCarts.put(userDTO.getName(), userDTO.getShoppingCart());
            }
        }
        if(useLocal){
            for(UserDTO userDTO: userDTOList){
                if (userDTO.getState() != UserStateEnum.GUEST)
                    users.put(userDTO.getName(), userDTO);
            }
            return true;
        }
        else {
            boolean success = true;
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)){
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                try (MorphiaSession session = datastore.startSession()) {
                    session.startTransaction();

                    // stage changes to commit
                    session.save(userDTOList);

                    // commit changes
                    session.commitTransaction();
                } catch (Exception e) {
                    success = false;
                }
            }
            catch(MongoConfigurationException e){
                System.out.println("Exception received: " + e.getMessage());
                return saveUsers(userDTOList); // timeout, try again
            }
            catch(MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                return saveUsers(userDTOList); // timeout, try again
            }

            return success;
        }
    }


    public int getNextAvailableStoreID(){
        if(useLocal){
            return this.stores.size();
        }
        try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
            Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

            Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
            mapper.mapPackage("Server.DAL");
            mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
            mapper.mapPackage("Server.DAL.PredicateDTOs");
            mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
            mapper.mapPackage("Server.DAL.PairDTOs");

            List<StoreDTO> storeDTOs = datastore.find(StoreDTO.class)
                    // filters find relevant entries
                    .filter(
                            Filters.gte("storeID", 0)
                    )
                    // iterator options manipulate the found entries
                    .iterator(
                            new FindOptions()
                                    .sort(Sort.descending("storeID"))
                    ).toList();

            mongoClient.close();

            if (storeDTOs == null || storeDTOs.size() == 0) {
                return 0;
            }
            StoreDTO head = storeDTOs.get(0);
            int id = head.getStoreID();
            return id + 1;
        }
        catch(MongoConfigurationException e){
            System.out.println("Exception received: " + e.getMessage());
            return getNextAvailableStoreID(); // timeout, try again
        }
        catch(MongoTimeoutException e){
            System.out.println("Exception received: " + e.getMessage());
            return getNextAvailableStoreID(); // timeout, try again
        }
//        else {
//            return (int) (Math.random() * (10000 - 1)) + 1;
//        }
    }

    public void resetDatabase(){
        try (MongoClient mongoClient = MongoClients.create(this.dbURL)){
            mongoClient.getDatabase(this.dbName).getCollection("users").drop();
            mongoClient.getDatabase(this.dbName).getCollection("stores").drop();
            mongoClient.getDatabase(this.dbName).getCollection("publishers").drop();
            mongoClient.getDatabase(this.dbName).getCollection("products").drop();
            mongoClient.getDatabase(this.dbName).getCollection("accounts").drop();
            mongoClient.getDatabase(this.dbName).getCollection("adminAccounts").drop();
        }
        catch(MongoConfigurationException e){
            System.out.println("Exception received: " + e.getMessage());
            resetDatabase(); // timeout, try again
        }
        catch(MongoTimeoutException e){
            System.out.println("Exception received: " + e.getMessage());
            resetDatabase(); // timeout, try again
        }
    }




}
