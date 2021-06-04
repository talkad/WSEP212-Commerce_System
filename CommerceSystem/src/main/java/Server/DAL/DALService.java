package Server.DAL;

import Server.DAL.DiscountRuleDTOs.StoreDiscountRuleDTO;
import Server.Domain.UserManager.User;
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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class DALService {

    // NOTE: acquisition of locks is in the order they appear in the field list

    private Map<Integer, StoreDTO> stores;
    private List<StoreDTO> storeSaveCache;
    private ReadWriteLock storeLock;

    private Map<String, UserDTO> users;
    private List<UserDTO> userSaveCache;
    private ReadWriteLock userLock;

    private Map<String, AccountDTO> accounts;
    private List<AccountDTO> accountSaveCache;
    private ReadWriteLock accountLock;

    private Map<String, AdminAccountDTO> admins;
    private List<AdminAccountDTO> adminAccountSaveCache;
    private ReadWriteLock adminAccountLock;

    private Map<Integer, ProductDTO> products;
    private List<ProductDTO> productSaveCache;
    private ReadWriteLock productLock;

    private Map<Integer, PublisherDTO> publisher;
    private List<PublisherDTO> publisherSaveCache;
    private ReadWriteLock publisherLock;

    private Map<String, ShoppingCartDTO> guestCarts;
    private ReadWriteLock guestCartLock;

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
        this.stores = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.accounts = new ConcurrentHashMap<>();
        this.admins = new ConcurrentHashMap<>();
        this.products = new ConcurrentHashMap<>();
        this.publisher = new ConcurrentHashMap<>();
        this.guestCarts = new ConcurrentHashMap<>();

        this.storeSaveCache = new Vector<>();
        this.userSaveCache = new Vector<>();
        this.accountSaveCache = new Vector<>();
        this.adminAccountSaveCache = new Vector<>();
        this.productSaveCache = new Vector<>();
        this.publisherSaveCache = new Vector<>();

        this.storeLock = new ReentrantReadWriteLock();
        this.userLock = new ReentrantReadWriteLock();
        this.accountLock = new ReentrantReadWriteLock();
        this.adminAccountLock = new ReentrantReadWriteLock();
        this.productLock = new ReentrantReadWriteLock();
        this.publisherLock = new ReentrantReadWriteLock();
        this.guestCartLock = new ReentrantReadWriteLock();
    }

    public void run(){
        while(!this.useLocal){
            this.storeLock.writeLock().lock();
            this.userLock.writeLock().lock();
            this.accountLock.writeLock().lock();
            this.adminAccountLock.writeLock().lock();
            this.productLock.writeLock().lock();
            this.publisherLock.writeLock().lock();

            List<StoreDTO> storeList = new Vector<>(this.storeSaveCache);
            this.storeSaveCache = new Vector<>();

            List<UserDTO> userList = new Vector<>(this.userSaveCache);
            this.userSaveCache = new Vector<>();

            List<AccountDTO> accountList = new Vector<>(this.accountSaveCache);
            this.accountSaveCache = new Vector<>();

            List<AdminAccountDTO> adminAccountList = new Vector<>(this.adminAccountSaveCache);
            this.adminAccountSaveCache = new Vector<>();

            List<ProductDTO> productList = new Vector<>(this.productSaveCache);
            this.productSaveCache = new Vector<>();

            List<PublisherDTO> publisherList = new Vector<>(this.publisherSaveCache);
            this.publisherSaveCache = new Vector<>();

            this.storeLock.writeLock().unlock();
            this.userLock.writeLock().unlock();
            this.accountLock.writeLock().unlock();
            this.adminAccountLock.writeLock().unlock();
            this.productLock.writeLock().unlock();
            this.publisherLock.writeLock().unlock();

            saveToDatabase(storeList, userList, accountList, adminAccountList, productList, publisherList);

            try {
                wait(5000);
            }
            catch (InterruptedException ignored){}
        }
    }

    private void saveToDatabase(List<StoreDTO> storeList, List<UserDTO> userList, List<AccountDTO> accountList, List<AdminAccountDTO> adminAccountList, List<ProductDTO> productList, List<PublisherDTO> publisherList){
        boolean allEmpty = storeList.isEmpty() && userList.isEmpty() && accountList.isEmpty() && adminAccountList.isEmpty() && productList.isEmpty() && publisherList.isEmpty();

        if(!allEmpty) {
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
                    if(!storeList.isEmpty())
                        session.save(storeList);

                    if(!userList.isEmpty())
                        session.save(userList);

                    if(!accountList.isEmpty())
                        session.save(accountList);

                    if(!adminAccountList.isEmpty())
                        session.save(adminAccountList);

                    if(!productList.isEmpty()) {
                        for (ProductDTO productDTO : productList) {
                            if(productDTO.toDelete()){
                                session.delete(productDTO.getDTO());
                            }
                            else{
                                session.save(productDTO.getDTO());
                            }
                        }
                    }

                    if(!publisherList.isEmpty())
                        session.save(publisherList);

                    // commit changes
                    session.commitTransaction();
                } catch (Exception e) {
                    System.out.println("CRITICAL TRANSACTION ERROR: " + e.getMessage());
                }
            } catch (MongoConfigurationException | MongoTimeoutException e) {
                System.out.println("Exception received: " + e.getMessage());
                saveToDatabase(storeList, userList, accountList, adminAccountList, productList, publisherList); // timeout, try again
            }
        }
    }

    public void useTestDatabase() {
        dbName = "testDatabase";
    }

    public void setUseLocal(boolean useLocal){
        this.useLocal = useLocal;
    }

    public void savePurchase(UserDTO userDTO, List<StoreDTO> storeDTOs) {
        this.storeLock.writeLock().lock();
        this.userLock.writeLock().lock();

        // TODO need guest lock?
        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCarts.put(userDTO.getName(), userDTO.getShoppingCart());
        }

        if(userDTO.getState() != UserStateEnum.GUEST) {
            this.users.put(userDTO.getName(), userDTO);
        }
        for(StoreDTO storeDTO : storeDTOs){
            this.stores.put(storeDTO.getStoreID(), storeDTO);
        }

        if(!useLocal) {
            this.userSaveCache.add(userDTO);
            this.storeSaveCache.addAll(storeDTOs);
        }

        this.storeLock.writeLock().unlock();
        this.userLock.writeLock().unlock();
    }

    public PublisherDTO getPublisher() {
        PublisherDTO publisherDTO;
        this.publisherLock.writeLock().lock();

        if(this.publisher.containsKey(0)){
            publisherDTO = this.publisher.get(0);
        }
        else if(useLocal){
            publisherDTO = new PublisherDTO();
            this.publisher.put(0, publisherDTO);
        }
        else{
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
            catch(MongoConfigurationException | MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                this.publisherLock.writeLock().unlock();
                return getPublisher(); // timeout, try again
            }

            if(publisherDTO == null)
                publisherDTO = new PublisherDTO();

            this.publisher.put(0, publisherDTO);
        }

        this.publisherLock.writeLock().unlock();
        return publisherDTO;
    }

    public void savePublisher(PublisherDTO publisherDTO) {
        this.publisherLock.writeLock().lock();

        this.publisher.put(0, publisherDTO);

        if(!useLocal)
            this.publisherSaveCache.add(publisherDTO);

        this.publisherLock.writeLock().unlock();
    }

    public void addAccount(AccountDTO accountDTO) {
        this.accountLock.writeLock().lock();

        this.accounts.put(accountDTO.getUsername(), accountDTO);

        if(!useLocal)
            this.accountSaveCache.add(accountDTO);

        this.accountLock.writeLock().unlock();
    }

    public AccountDTO getAccount(String username){
        AccountDTO accountDTO = null;
        this.accountLock.writeLock().lock();

        if(this.accounts.containsKey(username)){
            accountDTO = this.accounts.get(username);
        }
        else if(!useLocal){
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
            catch(MongoConfigurationException | MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                this.accountLock.writeLock().unlock();
                return getAccount(username); // timeout, try again
            }

            if(accountDTO != null)
                this.accounts.put(username, accountDTO);
        }

        this.accountLock.writeLock().unlock();
        return accountDTO;
    }

    public void addAdmin(AdminAccountDTO adminAccountDTO) {
        this.adminAccountLock.writeLock().lock();

        this.admins.put(adminAccountDTO.getUsername(), adminAccountDTO);

        if(!useLocal)
            this.adminAccountSaveCache.add(adminAccountDTO);

        this.adminAccountLock.writeLock().unlock();
    }

    public void insertStore(StoreDTO storeDTO) {
        this.storeLock.writeLock().lock();

        this.stores.put(storeDTO.getStoreID(), storeDTO);

        if(!useLocal)
            this.storeSaveCache.add(storeDTO);

        this.storeLock.writeLock().unlock();
    }

    public StoreDTO getStore(int storeId) {
        StoreDTO storeDTO = null;
        this.storeLock.writeLock().lock();

        if(this.stores.containsKey(storeId)){
            storeDTO = this.stores.get(storeId);
        }
        else if(!useLocal){
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
            catch(MongoConfigurationException | MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                this.storeLock.writeLock().unlock();
                return getStore(storeId); // timeout, try again
            }

            if(storeDTO != null)
                this.stores.put(storeDTO.getStoreID(), storeDTO);
        }

        this.storeLock.writeLock().unlock();
        return storeDTO;
    }

    public Collection<StoreDTO> getAllStores() {
        if(useLocal){
            this.storeLock.writeLock().lock();
            Collection<StoreDTO> storeDTOS = stores.values();
            this.storeLock.writeLock().unlock();

            return storeDTOS;
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
            catch(MongoConfigurationException | MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                return getAllStores(); // timeout, try again
            }

            return storeDTOList;
        }
    }

    public void saveUserAndStore(UserDTO userDTO, StoreDTO storeDTO){
        this.storeLock.writeLock().lock();
        this.userLock.writeLock().lock();

        // TODO guest lock?
        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCarts.put(userDTO.getName(), userDTO.getShoppingCart());
        }

        if(userDTO.getState() != UserStateEnum.GUEST)
            this.users.put(userDTO.getName(), userDTO);

        this.stores.put(storeDTO.getStoreID(), storeDTO);

        if(!useLocal){
            this.userSaveCache.add(userDTO);
            this.storeSaveCache.add(storeDTO);
        }

        this.storeLock.writeLock().unlock();
        this.userLock.writeLock().unlock();
    }

    public void saveUserStoreAndProduct(UserDTO userDTO, StoreDTO storeDTO, ProductDTO productDTO){
        this.storeLock.writeLock().lock();
        this.userLock.writeLock().lock();
        this.productLock.writeLock().lock();

        // TODO guest lock?
        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCarts.put(userDTO.getName(), userDTO.getShoppingCart());
        }

        if(userDTO.getState() != UserStateEnum.GUEST)
            this.users.put(userDTO.getName(), userDTO);

        this.stores.put(storeDTO.getStoreID(), storeDTO);

        this.products.put(productDTO.getProductID(), productDTO);

        if(!useLocal){
            this.userSaveCache.add(userDTO);
            this.storeSaveCache.add(storeDTO);
            this.productSaveCache.add(productDTO);
        }

        this.storeLock.writeLock().unlock();
        this.userLock.writeLock().unlock();
        this.productLock.writeLock().unlock();
    }

    public void saveStoreAndProduct(StoreDTO storeDTO, ProductDTO productDTO){
        this.storeLock.writeLock().lock();
        this.productLock.writeLock().lock();

        stores.put(storeDTO.getStoreID(), storeDTO);
        products.put(productDTO.getProductID(), productDTO);

        if(!useLocal){
            this.storeSaveCache.add(storeDTO);
            this.productSaveCache.add(productDTO);
        }

        this.storeLock.writeLock().unlock();
        this.productLock.writeLock().unlock();
    }

    public void saveStoreRemoveProduct(StoreDTO storeDTO, ProductDTO productDTO){
        this.storeLock.writeLock().lock();
        this.productLock.writeLock().lock();

        stores.put(storeDTO.getStoreID(), storeDTO);
        products.remove(productDTO.getProductID());

        if(!useLocal){
            this.storeSaveCache.add(storeDTO);
            this.productSaveCache.add(new DeleteProductDTO(productDTO));
        }

        this.storeLock.writeLock().unlock();
        this.productLock.writeLock().unlock();
    }

    public UserDTO getUser(String username){
        UserDTO userDTO = null;
        this.userLock.writeLock().lock();

        if(this.users.containsKey(username)){
            userDTO = this.users.get(username);
        }
        else if(!useLocal){
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
            catch(MongoConfigurationException | MongoTimeoutException e){
                System.out.println("Exception received: " + e.getMessage());
                this.userLock.writeLock().unlock();
                return getUser(username); // timeout, try again
            }

            if(userDTO != null)
                this.users.put(username, userDTO);
        }

        this.userLock.writeLock().unlock();
        return userDTO;
    }

    public void insertUser(UserDTO userDTO){
        this.userLock.writeLock().lock();

        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCarts.put(userDTO.getName(), userDTO.getShoppingCart());
        }
        else{
            this.users.put(userDTO.getName(), userDTO);
            this.userSaveCache.add(userDTO);
        }

        this.userLock.writeLock().unlock();
    }

    public boolean saveUsers(List<UserDTO> userDTOList){
        this.userLock.writeLock().lock();

        for(UserDTO userDTO : userDTOList) {
            if (userDTO.getState() == UserStateEnum.GUEST) {
                this.guestCarts.put(userDTO.getName(), userDTO.getShoppingCart());
            }
            else{
                this.users.put(userDTO.getName(), userDTO);
                this.userSaveCache.add(userDTO);
            }
        }

        this.userLock.writeLock().unlock();
        return true;
    }


    public int getNextAvailableStoreID(){
        if(useLocal){
            this.storeLock.writeLock().lock();
            int size = this.stores.size();
            this.storeLock.writeLock().unlock();
            return size;
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
            } catch (MongoConfigurationException | MongoTimeoutException e) {
                System.out.println("Exception received: " + e.getMessage());
                return getNextAvailableStoreID(); // timeout, try again
            }
        }
    }

    public void resetDatabase(){
        if(!useLocal) {
            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                mongoClient.getDatabase(this.dbName).getCollection("users").drop();
                mongoClient.getDatabase(this.dbName).getCollection("stores").drop();
                mongoClient.getDatabase(this.dbName).getCollection("publishers").drop();
                mongoClient.getDatabase(this.dbName).getCollection("products").drop();
                mongoClient.getDatabase(this.dbName).getCollection("accounts").drop();
                mongoClient.getDatabase(this.dbName).getCollection("adminAccounts").drop();
            } catch (MongoConfigurationException | MongoTimeoutException e) {
                System.out.println("Exception received: " + e.getMessage());
                resetDatabase(); // timeout, try again
            }
        }
    }




}
