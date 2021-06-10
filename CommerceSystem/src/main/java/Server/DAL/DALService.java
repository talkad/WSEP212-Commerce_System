package Server.DAL;

import Server.DAL.DiscountRuleDTOs.*;
import Server.DAL.PairDTOs.IntPermsListPair;
import Server.DAL.PairDTOs.IntStringListPair;
import Server.DAL.PairDTOs.PredPair;
import Server.DAL.PairDTOs.ProductIntPair;
import Server.DAL.PredicateDTOs.*;
import Server.DAL.PurchaseRuleDTOs.*;
import Server.Domain.CommonClasses.Pair;
import Server.Domain.UserManager.UserStateEnum;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoConfigurationException;
import com.mongodb.MongoTimeoutException;
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
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.time.LocalDate;
import java.util.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class DALService implements Runnable{

    // NOTE: acquisition of locks is in the order they appear in the field list

    private Map<Integer, Pair<StoreDTO, Long>> stores;
    private List<Pair<StoreDTO, DBOperation>> storeSaveCache;
    private ReadWriteLock storeLock;

    private Map<String, Pair<UserDTO, Long>> users;
    private List<Pair<UserDTO, DBOperation>> userSaveCache;
    private ReadWriteLock userLock;

    private Map<String, Pair<AccountDTO, Long>> accounts;
    private List<Pair<AccountDTO, DBOperation>> accountSaveCache;
    private ReadWriteLock accountLock;

    private Map<String, Pair<AdminAccountDTO, Long>> admins;
    private List<Pair<AdminAccountDTO, DBOperation>> adminAccountSaveCache;
    private ReadWriteLock adminAccountLock;

    private Map<Integer, Pair<ProductDTO, Long>> products;
    private List<Pair<ProductDTO, DBOperation>> productSaveCache;
    private ReadWriteLock productLock;

    private Map<Integer, Pair<PublisherDTO, Long>> publisher;
    private List<Pair<PublisherDTO, DBOperation>> publisherSaveCache;
    private ReadWriteLock publisherLock;

    private Map<String, Pair<ShoppingCartDTO, Long>> guestCarts;
    private ReadWriteLock guestCartLock;

    private Map<String, Pair<DailyCountersDTO, Long>> counters;
    private List<Pair<DailyCountersDTO, DBOperation>> countersSaveCache;
    private ReadWriteLock countersLock;

    private String dbName = "commerceDatabase";
    private String dbURL = "mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority";

    private boolean useLocal = true;

    private boolean cleaningCache = false;

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
        this.counters = new ConcurrentHashMap<>();

        this.storeSaveCache = new Vector<>();
        this.userSaveCache = new Vector<>();
        this.accountSaveCache = new Vector<>();
        this.adminAccountSaveCache = new Vector<>();
        this.productSaveCache = new Vector<>();
        this.publisherSaveCache = new Vector<>();
        this.countersSaveCache = new Vector<>();

        this.storeLock = new ReentrantReadWriteLock();
        this.userLock = new ReentrantReadWriteLock();
        this.accountLock = new ReentrantReadWriteLock();
        this.adminAccountLock = new ReentrantReadWriteLock();
        this.productLock = new ReentrantReadWriteLock();
        this.publisherLock = new ReentrantReadWriteLock();
        this.guestCartLock = new ReentrantReadWriteLock();
        this.countersLock = new ReentrantReadWriteLock();
    }

    public void startDB(){
        if(!cleaningCache && !useLocal) {
            System.out.println("Starting cache cleaning thread");
            CacheCleaner cacheCleaner = new CacheCleaner();
            Thread cleaningThread = new Thread(cacheCleaner);
            cleaningThread.start();
            cleaningCache = true;
            System.out.println("Cache cleaning thread started");
        }
        if(!useLocal) {
            System.out.println("Starting DAL thread");
            Thread thread = new Thread(this);
            thread.start();
            System.out.println("DAL thread started");
        }
    }

    public void run(){
        while(!this.useLocal){
            this.storeLock.writeLock().lock();
            this.userLock.writeLock().lock();
            this.accountLock.writeLock().lock();
            this.adminAccountLock.writeLock().lock();
            this.productLock.writeLock().lock();
            this.publisherLock.writeLock().lock();
            this.countersLock.writeLock().lock();

            List<Pair<StoreDTO, DBOperation>> storeList = new Vector<>(this.storeSaveCache);
            this.storeSaveCache = new Vector<>();

            List<Pair<UserDTO, DBOperation>> userList = new Vector<>(this.userSaveCache);
            this.userSaveCache = new Vector<>();

            List<Pair<AccountDTO, DBOperation>> accountList = new Vector<>(this.accountSaveCache);
            this.accountSaveCache = new Vector<>();

            List<Pair<AdminAccountDTO, DBOperation>> adminAccountList = new Vector<>(this.adminAccountSaveCache);
            this.adminAccountSaveCache = new Vector<>();

            List<Pair<ProductDTO, DBOperation>> productList = new Vector<>(this.productSaveCache);
            this.productSaveCache = new Vector<>();

            List<Pair<PublisherDTO, DBOperation>> publisherList = new Vector<>(this.publisherSaveCache);
            this.publisherSaveCache = new Vector<>();

            List<Pair<DailyCountersDTO, DBOperation>> countersList = new Vector<>(this.countersSaveCache);
            this.countersSaveCache = new Vector<>();

            boolean allEmpty = storeList.isEmpty() && userList.isEmpty() && accountList.isEmpty() && adminAccountList.isEmpty() && productList.isEmpty() && publisherList.isEmpty() && countersList.isEmpty();

            if(allEmpty){
                try {
                    synchronized(this){
                        this.storeLock.writeLock().unlock();
                        this.userLock.writeLock().unlock();
                        this.accountLock.writeLock().unlock();
                        this.adminAccountLock.writeLock().unlock();
                        this.productLock.writeLock().unlock();
                        this.publisherLock.writeLock().unlock();
                        this.countersLock.writeLock().unlock();
                        wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            else{
                this.storeLock.writeLock().unlock();
                this.userLock.writeLock().unlock();
                this.accountLock.writeLock().unlock();
                this.adminAccountLock.writeLock().unlock();
                this.productLock.writeLock().unlock();
                this.publisherLock.writeLock().unlock();
                this.countersLock.writeLock().unlock();

                saveToDatabase(storeList, userList, accountList, adminAccountList, productList, publisherList, countersList);
            }
        }
    }

    private void saveToDatabase(List<Pair<StoreDTO, DBOperation>> storeList, List<Pair<UserDTO, DBOperation>> userList, List<Pair<AccountDTO, DBOperation>> accountList, List<Pair<AdminAccountDTO, DBOperation>> adminAccountList, List<Pair<ProductDTO, DBOperation>> productList, List<Pair<PublisherDTO, DBOperation>> publisherList, List<Pair<DailyCountersDTO, DBOperation>> countersList){
        boolean allEmpty = storeList.isEmpty() && userList.isEmpty() && accountList.isEmpty() && adminAccountList.isEmpty() && productList.isEmpty() && publisherList.isEmpty() && countersList.isEmpty();

        if(!allEmpty) {
            System.out.println("Accessing DB for save iteration");
//            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
//                    AndCompositionDiscountRuleDTO.class,
//                    CategoryDiscountRuleDTO.class,
//                    CompoundDiscountRuleDTO.class,
//                    ConditionalCategoryDiscountRuleDTO.class,
//                    ConditionalProductDiscountRuleDTO.class,
//                    ConditionalStoreDiscountRuleDTO.class,
//                    DiscountRuleDTO.class,
//                    LeafDiscountRuleDTO.class,
//                    MaximumCompositionDiscountRuleDTO.class,
//                    OrCompositionDiscountRuleDTO.class,
//                    ProductDiscountRuleDTO.class,
//                    StoreDiscountRuleDTO.class,
//                    SumCompositionDiscountRuleDTO.class,
//                    TermsCompositionDiscountRuleDTO.class,
//                    XorCompositionDiscountRuleDTO.class,
//
//                    IntPermsListPair.class,
//                    IntStringListPair.class,
//                    PredPair.class,
//                    ProductIntPair.class,
//
//                    BasketPredicateDTO.class,
//                    CategoryPredicateDTO.class,
//                    PredicateDTO.class,
//                    ProductPredicateDTO.class,
//                    StorePredicateDTO.class,
//
//                    AndCompositionPurchaseRuleDTO.class,
//                    BasketPurchaseRuleDTO.class,
//                    CategoryPurchaseRuleDTO.class,
//                    CompoundPurchaseRuleDTO.class,
//                    ConditioningCompositionPurchaseRuleDTO.class,
//                    LeafPurchaseRuleDTO.class,
//                    OrCompositionPurchaseRuleDTO.class,
//                    ProductPurchaseRuleDTO.class,
//                    PurchaseRuleDTO.class
//            ).build();
//            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));

            try (MongoClient mongoClient = MongoClients.create(this.dbURL)) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                mapper.map(AndCompositionDiscountRuleDTO.class);
                mapper.map(CategoryDiscountRuleDTO.class);
                mapper.map(CompoundDiscountRuleDTO.class);
                mapper.map(ConditionalCategoryDiscountRuleDTO.class);
                mapper.map(ConditionalProductDiscountRuleDTO.class);
                mapper.map(ConditionalStoreDiscountRuleDTO.class);
                mapper.map(DiscountRuleDTO.class);
                mapper.map(LeafDiscountRuleDTO.class);
                mapper.map(MaximumCompositionDiscountRuleDTO.class);
                mapper.map(OrCompositionDiscountRuleDTO.class);
                mapper.map(ProductDiscountRuleDTO.class);
                mapper.map(StoreDiscountRuleDTO.class);
                mapper.map(SumCompositionDiscountRuleDTO.class);
                mapper.map(TermsCompositionDiscountRuleDTO.class);
                mapper.map(XorCompositionDiscountRuleDTO.class);

                mapper.map(IntPermsListPair.class);
                mapper.map(IntStringListPair.class);
                mapper.map(PredPair.class);
                mapper.map(ProductIntPair.class);

                mapper.map(BasketPredicateDTO.class);
                mapper.map(CategoryPredicateDTO.class);
                mapper.map(PredicateDTO.class);
                mapper.map(ProductPredicateDTO.class);
                mapper.map(StorePredicateDTO.class);

                mapper.map(AndCompositionPurchaseRuleDTO.class);
                mapper.map(BasketPurchaseRuleDTO.class);
                mapper.map(CategoryPurchaseRuleDTO.class);
                mapper.map(CompoundPurchaseRuleDTO.class);
                mapper.map(ConditioningCompositionPurchaseRuleDTO.class);
                mapper.map(LeafPurchaseRuleDTO.class);
                mapper.map(OrCompositionPurchaseRuleDTO.class);
                mapper.map(ProductPurchaseRuleDTO.class);
                mapper.map(PurchaseRuleDTO.class);

                try (MorphiaSession session = datastore.startSession()) {
                    session.startTransaction();

                    // stage changes to commit
                    if(!storeList.isEmpty()) {
                        for(Pair<StoreDTO, DBOperation> pair : storeList){
                            if(pair.getSecond().equals(DBOperation.SAVE)){
                                session.save(pair.getFirst());
                            }
                            else{
                                session.delete(pair.getFirst());
                            }
                        }
                    }

                    if(!userList.isEmpty()){
                        for(Pair<UserDTO, DBOperation> pair : userList){
                            if(pair.getSecond().equals(DBOperation.SAVE)){
                                session.save(pair.getFirst());
                            }
                            else{
                                session.delete(pair.getFirst());
                            }
                        }
                    }

                    if(!accountList.isEmpty()){
                        for(Pair<AccountDTO, DBOperation> pair : accountList){
                            if(pair.getSecond().equals(DBOperation.SAVE)){
                                session.save(pair.getFirst());
                            }
                            else{
                                session.delete(pair.getFirst());
                            }
                        }
                    }

                    if(!adminAccountList.isEmpty()){
                        for(Pair<AdminAccountDTO, DBOperation> pair : adminAccountList){
                            if(pair.getSecond().equals(DBOperation.SAVE)){
                                session.save(pair.getFirst());
                            }
                            else{
                                session.delete(pair.getFirst());
                            }
                        }
                    }

                    if(!productList.isEmpty()) {
                        for(Pair<ProductDTO, DBOperation> pair : productList){
                            if(pair.getSecond().equals(DBOperation.SAVE)){
                                session.save(pair.getFirst());
                            }
                            else{
                                session.delete(pair.getFirst());
                            }
                        }
                    }

                    if(!publisherList.isEmpty()){
                        for(Pair<PublisherDTO, DBOperation> pair : publisherList){
                            if(pair.getSecond().equals(DBOperation.SAVE)){
                                session.save(pair.getFirst());
                            }
                            else{
                                session.delete(pair.getFirst());
                            }
                        }
                    }

                    if(!countersList.isEmpty()){
                        for(Pair<DailyCountersDTO, DBOperation> pair : countersList){
                            if(pair.getSecond().equals(DBOperation.SAVE)){
                                session.save(pair.getFirst());
                            }
                            else{
                                session.delete(pair.getFirst());
                            }
                        }
                    }

                    // commit changes
                    session.commitTransaction();
                } catch (Exception e) {
                    System.out.println("CRITICAL TRANSACTION ERROR: " + e.getMessage());
                    saveToDatabase(storeList, userList, accountList, adminAccountList, productList, publisherList, countersList);
                }
            } catch (Exception e) {
                System.out.println("Exception received: " + e.getMessage());
                saveToDatabase(storeList, userList, accountList, adminAccountList, productList, publisherList, countersList); // timeout, try again
            }
            System.out.println("Completed save iteration");
        }
    }

    public void cleanCache(){
        this.storeLock.writeLock().lock();
        this.userLock.writeLock().lock();
        this.accountLock.writeLock().lock();
        this.adminAccountLock.writeLock().lock();
        this.productLock.writeLock().lock();
        this.publisherLock.writeLock().lock();
        this.guestCartLock.writeLock().lock();
        this.countersLock.writeLock().lock();

        System.out.println("Cleaning cache");

        long currTimeMillis = System.currentTimeMillis();
        long interval = 2*60*1000;

        List<Integer> ids = new Vector<>(this.stores.keySet());
        for(int storeID : ids){
            long oldTimeMillis = this.stores.get(storeID).getSecond();
            if(currTimeMillis >= oldTimeMillis + interval){ // 5 minutes have passed since last read/write
                this.stores.remove(storeID);
                System.out.println("Removed store " + storeID + " from cache");
            }
        }

        List<String> usernames = new Vector<>(this.users.keySet());
        for(String username : usernames){
            long oldTimeMillis = this.users.get(username).getSecond();
            if(currTimeMillis >= oldTimeMillis + interval){ // 5 minutes have passed since last read/write
                this.users.remove(username);
                System.out.println("Removed user " + username + " from cache");
            }
        }

        usernames = new Vector<>(this.accounts.keySet());
        for(String username : usernames){
            long oldTimeMillis = this.accounts.get(username).getSecond();
            if(currTimeMillis >= oldTimeMillis + interval){ // 5 minutes have passed since last read/write
                this.accounts.remove(username);
                System.out.println("Removed account " + username + " from cache");
            }
        }

        usernames = new Vector<>(this.admins.keySet());
        for(String username : usernames){
            long oldTimeMillis = this.admins.get(username).getSecond();
            if(currTimeMillis >= oldTimeMillis + interval){ // 5 minutes have passed since last read/write
                this.admins.remove(username);
                System.out.println("Removed admin " + username + " from cache");
            }
        }

        ids = new Vector<>(this.products.keySet());
        for(int productID : ids){
            long oldTimeMillis = this.products.get(productID).getSecond();
            if(currTimeMillis >= oldTimeMillis + interval){ // 5 minutes have passed since last read/write
                this.products.remove(productID);
                System.out.println("Removed product " + productID + " from cache");
            }
        }

        ids = new Vector<>(this.publisher.keySet());
        for(int publisherID : ids){
            long oldTimeMillis = this.publisher.get(publisherID).getSecond();
            if(currTimeMillis >= oldTimeMillis + interval){ // 5 minutes have passed since last read/write
                this.publisher.remove(publisherID);
                System.out.println("Removed publisher " + publisherID + " from cache");
            }
        }

        usernames = new Vector<>(this.guestCarts.keySet());
        for(String username : usernames){
            long oldTimeMillis = this.guestCarts.get(username).getSecond();
            if(currTimeMillis >= oldTimeMillis + interval){ // 5 minutes have passed since last read/write
                this.guestCarts.remove(username);
                System.out.println("Removed cart of " + username + " from cache");
            }
        }

        List<String> dates = new Vector<>(this.counters.keySet());
        for(String date : dates){
            long oldTimeMillis = this.counters.get(date).getSecond();
            if(currTimeMillis >= oldTimeMillis + interval){ // 5 minutes have passed since last read/write
                this.counters.remove(date);
                System.out.println("Removed counters for " + date + " from cache");
            }
        }

        this.storeLock.writeLock().unlock();
        this.userLock.writeLock().unlock();
        this.accountLock.writeLock().unlock();
        this.adminAccountLock.writeLock().unlock();
        this.productLock.writeLock().unlock();
        this.publisherLock.writeLock().unlock();
        this.guestCartLock.writeLock().unlock();
        this.countersLock.writeLock().unlock();

        System.out.println("Finished cleaning cache");
    }

    public void useTestDatabase() {
        dbName = "testDatabase";
    }

    public void setUseLocal(boolean useLocal){
        this.useLocal = useLocal;
    }

    public void setDbName(String dbName){ this.dbName = dbName;}

    public void setDbURL(String dbURL){ this.dbURL = dbURL; }

    public DailyCountersDTO getDailyCounters(LocalDate date){
        DailyCountersDTO dailyCountersDTO;
        this.countersLock.writeLock().lock();
        if(this.counters.containsKey(date.toString())){
            dailyCountersDTO = this.counters.get(date.toString()).getFirst();
            this.counters.put(dailyCountersDTO.getCurrentDate(), new Pair<>(dailyCountersDTO, System.currentTimeMillis()));
        }
        else if(useLocal){
            dailyCountersDTO = new DailyCountersDTO(date.toString(), 0, 0, 0, 0, 0);
            this.counters.put(dailyCountersDTO.getCurrentDate(), new Pair<>(dailyCountersDTO, System.currentTimeMillis()));
        }
        else{
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
                    AndCompositionDiscountRuleDTO.class,
                    CategoryDiscountRuleDTO.class,
                    CompoundDiscountRuleDTO.class,
                    ConditionalCategoryDiscountRuleDTO.class,
                    ConditionalProductDiscountRuleDTO.class,
                    ConditionalStoreDiscountRuleDTO.class,
                    DiscountRuleDTO.class,
                    LeafDiscountRuleDTO.class,
                    MaximumCompositionDiscountRuleDTO.class,
                    OrCompositionDiscountRuleDTO.class,
                    ProductDiscountRuleDTO.class,
                    StoreDiscountRuleDTO.class,
                    SumCompositionDiscountRuleDTO.class,
                    TermsCompositionDiscountRuleDTO.class,
                    XorCompositionDiscountRuleDTO.class,

                    IntPermsListPair.class,
                    IntStringListPair.class,
                    PredPair.class,
                    ProductIntPair.class,

                    BasketPredicateDTO.class,
                    CategoryPredicateDTO.class,
                    PredicateDTO.class,
                    ProductPredicateDTO.class,
                    StorePredicateDTO.class,

                    AndCompositionPurchaseRuleDTO.class,
                    BasketPurchaseRuleDTO.class,
                    CategoryPurchaseRuleDTO.class,
                    CompoundPurchaseRuleDTO.class,
                    ConditioningCompositionPurchaseRuleDTO.class,
                    LeafPurchaseRuleDTO.class,
                    OrCompositionPurchaseRuleDTO.class,
                    ProductPurchaseRuleDTO.class,
                    PurchaseRuleDTO.class
            ).build();
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));

            try (MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(this.dbURL))
                    .codecRegistry(pojoCodecRegistry).build())) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                mapper.map(AndCompositionDiscountRuleDTO.class);
                mapper.map(CategoryDiscountRuleDTO.class);
                mapper.map(CompoundDiscountRuleDTO.class);
                mapper.map(ConditionalCategoryDiscountRuleDTO.class);
                mapper.map(ConditionalProductDiscountRuleDTO.class);
                mapper.map(ConditionalStoreDiscountRuleDTO.class);
                mapper.map(DiscountRuleDTO.class);
                mapper.map(LeafDiscountRuleDTO.class);
                mapper.map(MaximumCompositionDiscountRuleDTO.class);
                mapper.map(OrCompositionDiscountRuleDTO.class);
                mapper.map(ProductDiscountRuleDTO.class);
                mapper.map(StoreDiscountRuleDTO.class);
                mapper.map(SumCompositionDiscountRuleDTO.class);
                mapper.map(TermsCompositionDiscountRuleDTO.class);
                mapper.map(XorCompositionDiscountRuleDTO.class);

                mapper.map(IntPermsListPair.class);
                mapper.map(IntStringListPair.class);
                mapper.map(PredPair.class);
                mapper.map(ProductIntPair.class);

                mapper.map(BasketPredicateDTO.class);
                mapper.map(CategoryPredicateDTO.class);
                mapper.map(PredicateDTO.class);
                mapper.map(ProductPredicateDTO.class);
                mapper.map(StorePredicateDTO.class);

                mapper.map(AndCompositionPurchaseRuleDTO.class);
                mapper.map(BasketPurchaseRuleDTO.class);
                mapper.map(CategoryPurchaseRuleDTO.class);
                mapper.map(CompoundPurchaseRuleDTO.class);
                mapper.map(ConditioningCompositionPurchaseRuleDTO.class);
                mapper.map(LeafPurchaseRuleDTO.class);
                mapper.map(OrCompositionPurchaseRuleDTO.class);
                mapper.map(ProductPurchaseRuleDTO.class);
                mapper.map(PurchaseRuleDTO.class);

                dailyCountersDTO = datastore.find(DailyCountersDTO.class)
                        // filters find relevant entries
                        .filter(
                                Filters.eq("currentDate", date.toString())
                        ).first();
            }
            catch(Exception e){
                System.out.println("Exception received: " + e.getMessage());
                this.countersLock.writeLock().unlock();
                return getDailyCounters(date); // timeout, try again
            }

            if(dailyCountersDTO == null) {
                dailyCountersDTO = new DailyCountersDTO(date.toString(), 0, 0, 0, 0, 0);
            }
            this.counters.put(dailyCountersDTO.getCurrentDate(), new Pair<>(dailyCountersDTO, System.currentTimeMillis()));

        }
        this.countersLock.writeLock().unlock();
        return dailyCountersDTO;
    }

    public void saveCounters(DailyCountersDTO dailyCountersDTO){
        this.countersLock.writeLock().lock();
        this.counters.put(dailyCountersDTO.getCurrentDate(), new Pair<>(dailyCountersDTO, System.currentTimeMillis()));
        if(!useLocal){
            this.countersSaveCache.add(new Pair<>(dailyCountersDTO, DBOperation.SAVE));

            synchronized (this){
                notifyAll();
            }
        }
        this.countersLock.writeLock().unlock();
    }

    public void savePurchase(UserDTO userDTO, List<StoreDTO> storeDTOs) {
        this.storeLock.writeLock().lock();
        this.userLock.writeLock().lock();

        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCartLock.writeLock().lock();
            this.guestCarts.put(userDTO.getName(), new Pair<>(userDTO.getShoppingCart(), System.currentTimeMillis()));
            this.guestCartLock.writeLock().unlock();
        }

        if(userDTO.getState() != UserStateEnum.GUEST) {
            this.users.put(userDTO.getName(), new Pair<>(userDTO, System.currentTimeMillis()));
        }
        for(StoreDTO storeDTO : storeDTOs){
            this.stores.put(storeDTO.getStoreID(), new Pair<>(storeDTO, System.currentTimeMillis()));
        }

        if(!useLocal) {
            this.userSaveCache.add(new Pair<>(userDTO, DBOperation.SAVE));
            for(StoreDTO storeDTO : storeDTOs){
                this.storeSaveCache.add(new Pair<>(storeDTO, DBOperation.SAVE));
            }

            synchronized (this){
                notifyAll();
            }
        }

        this.storeLock.writeLock().unlock();
        this.userLock.writeLock().unlock();
    }

    public PublisherDTO getPublisher() {
        PublisherDTO publisherDTO;
        this.publisherLock.writeLock().lock();

        if(this.publisher.containsKey(0)){
            publisherDTO = this.publisher.get(0).getFirst();
            this.publisher.put(0, new Pair<>(publisherDTO, System.currentTimeMillis()));
        }
        else if(useLocal){
            publisherDTO = new PublisherDTO();
            this.publisher.put(0, new Pair<>(publisherDTO, System.currentTimeMillis()));
        }
        else{
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
                    AndCompositionDiscountRuleDTO.class,
                    CategoryDiscountRuleDTO.class,
                    CompoundDiscountRuleDTO.class,
                    ConditionalCategoryDiscountRuleDTO.class,
                    ConditionalProductDiscountRuleDTO.class,
                    ConditionalStoreDiscountRuleDTO.class,
                    DiscountRuleDTO.class,
                    LeafDiscountRuleDTO.class,
                    MaximumCompositionDiscountRuleDTO.class,
                    OrCompositionDiscountRuleDTO.class,
                    ProductDiscountRuleDTO.class,
                    StoreDiscountRuleDTO.class,
                    SumCompositionDiscountRuleDTO.class,
                    TermsCompositionDiscountRuleDTO.class,
                    XorCompositionDiscountRuleDTO.class,

                    IntPermsListPair.class,
                    IntStringListPair.class,
                    PredPair.class,
                    ProductIntPair.class,

                    BasketPredicateDTO.class,
                    CategoryPredicateDTO.class,
                    PredicateDTO.class,
                    ProductPredicateDTO.class,
                    StorePredicateDTO.class,

                    AndCompositionPurchaseRuleDTO.class,
                    BasketPurchaseRuleDTO.class,
                    CategoryPurchaseRuleDTO.class,
                    CompoundPurchaseRuleDTO.class,
                    ConditioningCompositionPurchaseRuleDTO.class,
                    LeafPurchaseRuleDTO.class,
                    OrCompositionPurchaseRuleDTO.class,
                    ProductPurchaseRuleDTO.class,
                    PurchaseRuleDTO.class
            ).build();
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));

            try (MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(this.dbURL))
                    .codecRegistry(pojoCodecRegistry).build())) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                mapper.map(AndCompositionDiscountRuleDTO.class);
                mapper.map(CategoryDiscountRuleDTO.class);
                mapper.map(CompoundDiscountRuleDTO.class);
                mapper.map(ConditionalCategoryDiscountRuleDTO.class);
                mapper.map(ConditionalProductDiscountRuleDTO.class);
                mapper.map(ConditionalStoreDiscountRuleDTO.class);
                mapper.map(DiscountRuleDTO.class);
                mapper.map(LeafDiscountRuleDTO.class);
                mapper.map(MaximumCompositionDiscountRuleDTO.class);
                mapper.map(OrCompositionDiscountRuleDTO.class);
                mapper.map(ProductDiscountRuleDTO.class);
                mapper.map(StoreDiscountRuleDTO.class);
                mapper.map(SumCompositionDiscountRuleDTO.class);
                mapper.map(TermsCompositionDiscountRuleDTO.class);
                mapper.map(XorCompositionDiscountRuleDTO.class);

                mapper.map(IntPermsListPair.class);
                mapper.map(IntStringListPair.class);
                mapper.map(PredPair.class);
                mapper.map(ProductIntPair.class);

                mapper.map(BasketPredicateDTO.class);
                mapper.map(CategoryPredicateDTO.class);
                mapper.map(PredicateDTO.class);
                mapper.map(ProductPredicateDTO.class);
                mapper.map(StorePredicateDTO.class);

                mapper.map(AndCompositionPurchaseRuleDTO.class);
                mapper.map(BasketPurchaseRuleDTO.class);
                mapper.map(CategoryPurchaseRuleDTO.class);
                mapper.map(CompoundPurchaseRuleDTO.class);
                mapper.map(ConditioningCompositionPurchaseRuleDTO.class);
                mapper.map(LeafPurchaseRuleDTO.class);
                mapper.map(OrCompositionPurchaseRuleDTO.class);
                mapper.map(ProductPurchaseRuleDTO.class);
                mapper.map(PurchaseRuleDTO.class);

                publisherDTO = datastore.find(PublisherDTO.class).first();

            }
            catch(Exception e){
                System.out.println("Exception received: " + e.getMessage());
                this.publisherLock.writeLock().unlock();
                return getPublisher(); // timeout, try again
            }

            if(publisherDTO == null)
                publisherDTO = new PublisherDTO();

            this.publisher.put(0, new Pair<>(publisherDTO, System.currentTimeMillis()));
        }

        this.publisherLock.writeLock().unlock();
        return publisherDTO;
    }

    public void savePublisher(PublisherDTO publisherDTO) {
        this.publisherLock.writeLock().lock();

        this.publisher.put(0, new Pair<>(publisherDTO, System.currentTimeMillis()));

        if(!useLocal) {
            this.publisherSaveCache.add(new Pair<>(publisherDTO, DBOperation.SAVE));

            synchronized (this){
                notifyAll();
            }
        }

        this.publisherLock.writeLock().unlock();
    }

    public void addAccount(AccountDTO accountDTO) {
        this.accountLock.writeLock().lock();

        this.accounts.put(accountDTO.getUsername(), new Pair<>(accountDTO, System.currentTimeMillis()));

        if(!useLocal) {
            this.accountSaveCache.add(new Pair<>(accountDTO, DBOperation.SAVE));

            synchronized (this){
                notifyAll();
            }
        }

        this.accountLock.writeLock().unlock();
    }

    public AccountDTO getAccount(String username){
        AccountDTO accountDTO = null;
        this.accountLock.writeLock().lock();

        if(this.accounts.containsKey(username)){
            accountDTO = this.accounts.get(username).getFirst();
            this.accounts.put(username, new Pair<>(accountDTO, System.currentTimeMillis()));
        }
        else if(!useLocal){
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
                    AndCompositionDiscountRuleDTO.class,
                    CategoryDiscountRuleDTO.class,
                    CompoundDiscountRuleDTO.class,
                    ConditionalCategoryDiscountRuleDTO.class,
                    ConditionalProductDiscountRuleDTO.class,
                    ConditionalStoreDiscountRuleDTO.class,
                    DiscountRuleDTO.class,
                    LeafDiscountRuleDTO.class,
                    MaximumCompositionDiscountRuleDTO.class,
                    OrCompositionDiscountRuleDTO.class,
                    ProductDiscountRuleDTO.class,
                    StoreDiscountRuleDTO.class,
                    SumCompositionDiscountRuleDTO.class,
                    TermsCompositionDiscountRuleDTO.class,
                    XorCompositionDiscountRuleDTO.class,

                    IntPermsListPair.class,
                    IntStringListPair.class,
                    PredPair.class,
                    ProductIntPair.class,

                    BasketPredicateDTO.class,
                    CategoryPredicateDTO.class,
                    PredicateDTO.class,
                    ProductPredicateDTO.class,
                    StorePredicateDTO.class,

                    AndCompositionPurchaseRuleDTO.class,
                    BasketPurchaseRuleDTO.class,
                    CategoryPurchaseRuleDTO.class,
                    CompoundPurchaseRuleDTO.class,
                    ConditioningCompositionPurchaseRuleDTO.class,
                    LeafPurchaseRuleDTO.class,
                    OrCompositionPurchaseRuleDTO.class,
                    ProductPurchaseRuleDTO.class,
                    PurchaseRuleDTO.class
            ).build();
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));

            try (MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(this.dbURL))
                    .codecRegistry(pojoCodecRegistry).build())) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                mapper.map(AndCompositionDiscountRuleDTO.class);
                mapper.map(CategoryDiscountRuleDTO.class);
                mapper.map(CompoundDiscountRuleDTO.class);
                mapper.map(ConditionalCategoryDiscountRuleDTO.class);
                mapper.map(ConditionalProductDiscountRuleDTO.class);
                mapper.map(ConditionalStoreDiscountRuleDTO.class);
                mapper.map(DiscountRuleDTO.class);
                mapper.map(LeafDiscountRuleDTO.class);
                mapper.map(MaximumCompositionDiscountRuleDTO.class);
                mapper.map(OrCompositionDiscountRuleDTO.class);
                mapper.map(ProductDiscountRuleDTO.class);
                mapper.map(StoreDiscountRuleDTO.class);
                mapper.map(SumCompositionDiscountRuleDTO.class);
                mapper.map(TermsCompositionDiscountRuleDTO.class);
                mapper.map(XorCompositionDiscountRuleDTO.class);

                mapper.map(IntPermsListPair.class);
                mapper.map(IntStringListPair.class);
                mapper.map(PredPair.class);
                mapper.map(ProductIntPair.class);

                mapper.map(BasketPredicateDTO.class);
                mapper.map(CategoryPredicateDTO.class);
                mapper.map(PredicateDTO.class);
                mapper.map(ProductPredicateDTO.class);
                mapper.map(StorePredicateDTO.class);

                mapper.map(AndCompositionPurchaseRuleDTO.class);
                mapper.map(BasketPurchaseRuleDTO.class);
                mapper.map(CategoryPurchaseRuleDTO.class);
                mapper.map(CompoundPurchaseRuleDTO.class);
                mapper.map(ConditioningCompositionPurchaseRuleDTO.class);
                mapper.map(LeafPurchaseRuleDTO.class);
                mapper.map(OrCompositionPurchaseRuleDTO.class);
                mapper.map(ProductPurchaseRuleDTO.class);
                mapper.map(PurchaseRuleDTO.class);

                accountDTO = datastore.find(AccountDTO.class)
                        // filters find relevant entries
                        .filter(
                                Filters.eq("username", username)
                        ).first();
            }
            catch(Exception e){
                System.out.println("Exception received: " + e.getMessage());
                this.accountLock.writeLock().unlock();
                return getAccount(username); // timeout, try again
            }

            if(accountDTO != null)
                this.accounts.put(username, new Pair<>(accountDTO, System.currentTimeMillis()));
        }

        this.accountLock.writeLock().unlock();
        return accountDTO;
    }

    public void addAdmin(AdminAccountDTO adminAccountDTO) {
        this.adminAccountLock.writeLock().lock();

        this.admins.put(adminAccountDTO.getUsername(), new Pair<>(adminAccountDTO, System.currentTimeMillis()));

        if(!useLocal) {
            this.adminAccountSaveCache.add(new Pair<>(adminAccountDTO, DBOperation.SAVE));

            synchronized (this){
                notifyAll();
            }
        }

        this.adminAccountLock.writeLock().unlock();
    }

    public void insertStore(StoreDTO storeDTO) {
        this.storeLock.writeLock().lock();

        this.stores.put(storeDTO.getStoreID(), new Pair<>(storeDTO, System.currentTimeMillis()));

        if(!useLocal) {
            this.storeSaveCache.add(new Pair<>(storeDTO, DBOperation.SAVE));

            synchronized (this){
                notifyAll();
            }
        }

        this.storeLock.writeLock().unlock();
    }

    public StoreDTO getStore(int storeId) {
        StoreDTO storeDTO = null;
        this.storeLock.writeLock().lock();

        if(this.stores.containsKey(storeId)){
            storeDTO = this.stores.get(storeId).getFirst();
            this.stores.put(storeDTO.getStoreID(), new Pair<>(storeDTO, System.currentTimeMillis()));
        }
        else if(!useLocal){
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
                    AndCompositionDiscountRuleDTO.class,
                    CategoryDiscountRuleDTO.class,
                    CompoundDiscountRuleDTO.class,
                    ConditionalCategoryDiscountRuleDTO.class,
                    ConditionalProductDiscountRuleDTO.class,
                    ConditionalStoreDiscountRuleDTO.class,
                    DiscountRuleDTO.class,
                    LeafDiscountRuleDTO.class,
                    MaximumCompositionDiscountRuleDTO.class,
                    OrCompositionDiscountRuleDTO.class,
                    ProductDiscountRuleDTO.class,
                    StoreDiscountRuleDTO.class,
                    SumCompositionDiscountRuleDTO.class,
                    TermsCompositionDiscountRuleDTO.class,
                    XorCompositionDiscountRuleDTO.class,

                    IntPermsListPair.class,
                    IntStringListPair.class,
                    PredPair.class,
                    ProductIntPair.class,

                    BasketPredicateDTO.class,
                    CategoryPredicateDTO.class,
                    PredicateDTO.class,
                    ProductPredicateDTO.class,
                    StorePredicateDTO.class,

                    AndCompositionPurchaseRuleDTO.class,
                    BasketPurchaseRuleDTO.class,
                    CategoryPurchaseRuleDTO.class,
                    CompoundPurchaseRuleDTO.class,
                    ConditioningCompositionPurchaseRuleDTO.class,
                    LeafPurchaseRuleDTO.class,
                    OrCompositionPurchaseRuleDTO.class,
                    ProductPurchaseRuleDTO.class,
                    PurchaseRuleDTO.class
            ).build();
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));

            try (MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(this.dbURL))
                    .codecRegistry(pojoCodecRegistry).build())) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                mapper.map(AndCompositionDiscountRuleDTO.class);
                mapper.map(CategoryDiscountRuleDTO.class);
                mapper.map(CompoundDiscountRuleDTO.class);
                mapper.map(ConditionalCategoryDiscountRuleDTO.class);
                mapper.map(ConditionalProductDiscountRuleDTO.class);
                mapper.map(ConditionalStoreDiscountRuleDTO.class);
                mapper.map(DiscountRuleDTO.class);
                mapper.map(LeafDiscountRuleDTO.class);
                mapper.map(MaximumCompositionDiscountRuleDTO.class);
                mapper.map(OrCompositionDiscountRuleDTO.class);
                mapper.map(ProductDiscountRuleDTO.class);
                mapper.map(StoreDiscountRuleDTO.class);
                mapper.map(SumCompositionDiscountRuleDTO.class);
                mapper.map(TermsCompositionDiscountRuleDTO.class);
                mapper.map(XorCompositionDiscountRuleDTO.class);

                mapper.map(IntPermsListPair.class);
                mapper.map(IntStringListPair.class);
                mapper.map(PredPair.class);
                mapper.map(ProductIntPair.class);

                mapper.map(BasketPredicateDTO.class);
                mapper.map(CategoryPredicateDTO.class);
                mapper.map(PredicateDTO.class);
                mapper.map(ProductPredicateDTO.class);
                mapper.map(StorePredicateDTO.class);

                mapper.map(AndCompositionPurchaseRuleDTO.class);
                mapper.map(BasketPurchaseRuleDTO.class);
                mapper.map(CategoryPurchaseRuleDTO.class);
                mapper.map(CompoundPurchaseRuleDTO.class);
                mapper.map(ConditioningCompositionPurchaseRuleDTO.class);
                mapper.map(LeafPurchaseRuleDTO.class);
                mapper.map(OrCompositionPurchaseRuleDTO.class);
                mapper.map(ProductPurchaseRuleDTO.class);
                mapper.map(PurchaseRuleDTO.class);

                storeDTO = datastore.find(StoreDTO.class)
                        // filters find relevant entries
                        .filter(
                                Filters.eq("storeID", storeId)
                        ).first();
            }
            catch(Exception e){
                System.out.println("Exception received: " + e.getMessage());
                this.storeLock.writeLock().unlock();
                return getStore(storeId); // timeout, try again
            }

            if(storeDTO != null)
                this.stores.put(storeDTO.getStoreID(), new Pair<>(storeDTO, System.currentTimeMillis()));
        }

        this.storeLock.writeLock().unlock();
        return storeDTO;
    }

    public Collection<StoreDTO> getAllStores() {
        if(useLocal){
            this.storeLock.writeLock().lock();
            Collection<Pair<StoreDTO, Long>> storeValues = stores.values();
            this.storeLock.writeLock().unlock();

            Collection<StoreDTO> storeDTOS = new Vector<>();
            for(Pair<StoreDTO, Long> pair : storeValues){
                storeDTOS.add(pair.getFirst());
            }

            return storeDTOS;
        }
        else {
            List<StoreDTO> storeDTOList = null;
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
                    AndCompositionDiscountRuleDTO.class,
                    CategoryDiscountRuleDTO.class,
                    CompoundDiscountRuleDTO.class,
                    ConditionalCategoryDiscountRuleDTO.class,
                    ConditionalProductDiscountRuleDTO.class,
                    ConditionalStoreDiscountRuleDTO.class,
                    DiscountRuleDTO.class,
                    LeafDiscountRuleDTO.class,
                    MaximumCompositionDiscountRuleDTO.class,
                    OrCompositionDiscountRuleDTO.class,
                    ProductDiscountRuleDTO.class,
                    StoreDiscountRuleDTO.class,
                    SumCompositionDiscountRuleDTO.class,
                    TermsCompositionDiscountRuleDTO.class,
                    XorCompositionDiscountRuleDTO.class,

                    IntPermsListPair.class,
                    IntStringListPair.class,
                    PredPair.class,
                    ProductIntPair.class,

                    BasketPredicateDTO.class,
                    CategoryPredicateDTO.class,
                    PredicateDTO.class,
                    ProductPredicateDTO.class,
                    StorePredicateDTO.class,

                    AndCompositionPurchaseRuleDTO.class,
                    BasketPurchaseRuleDTO.class,
                    CategoryPurchaseRuleDTO.class,
                    CompoundPurchaseRuleDTO.class,
                    ConditioningCompositionPurchaseRuleDTO.class,
                    LeafPurchaseRuleDTO.class,
                    OrCompositionPurchaseRuleDTO.class,
                    ProductPurchaseRuleDTO.class,
                    PurchaseRuleDTO.class
            ).build();
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));

            try (MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(this.dbURL))
                    .codecRegistry(pojoCodecRegistry).build())) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                mapper.map(AndCompositionDiscountRuleDTO.class);
                mapper.map(CategoryDiscountRuleDTO.class);
                mapper.map(CompoundDiscountRuleDTO.class);
                mapper.map(ConditionalCategoryDiscountRuleDTO.class);
                mapper.map(ConditionalProductDiscountRuleDTO.class);
                mapper.map(ConditionalStoreDiscountRuleDTO.class);
                mapper.map(DiscountRuleDTO.class);
                mapper.map(LeafDiscountRuleDTO.class);
                mapper.map(MaximumCompositionDiscountRuleDTO.class);
                mapper.map(OrCompositionDiscountRuleDTO.class);
                mapper.map(ProductDiscountRuleDTO.class);
                mapper.map(StoreDiscountRuleDTO.class);
                mapper.map(SumCompositionDiscountRuleDTO.class);
                mapper.map(TermsCompositionDiscountRuleDTO.class);
                mapper.map(XorCompositionDiscountRuleDTO.class);

                mapper.map(IntPermsListPair.class);
                mapper.map(IntStringListPair.class);
                mapper.map(PredPair.class);
                mapper.map(ProductIntPair.class);

                mapper.map(BasketPredicateDTO.class);
                mapper.map(CategoryPredicateDTO.class);
                mapper.map(PredicateDTO.class);
                mapper.map(ProductPredicateDTO.class);
                mapper.map(StorePredicateDTO.class);

                mapper.map(AndCompositionPurchaseRuleDTO.class);
                mapper.map(BasketPurchaseRuleDTO.class);
                mapper.map(CategoryPurchaseRuleDTO.class);
                mapper.map(CompoundPurchaseRuleDTO.class);
                mapper.map(ConditioningCompositionPurchaseRuleDTO.class);
                mapper.map(LeafPurchaseRuleDTO.class);
                mapper.map(OrCompositionPurchaseRuleDTO.class);
                mapper.map(ProductPurchaseRuleDTO.class);
                mapper.map(PurchaseRuleDTO.class);

                storeDTOList = datastore.find(StoreDTO.class)
                        .filter(
                                Filters.gte("storeID", 0)
                        )
                        .iterator(new FindOptions()
                                .sort(Sort.ascending("storeID")))
                        .toList();
            }
            catch(Exception e){
                System.out.println("Exception received: " + e.getMessage());
                return getAllStores(); // timeout, try again
            }

            return storeDTOList;
        }
    }

    public void saveUserAndStore(UserDTO userDTO, StoreDTO storeDTO){
        this.storeLock.writeLock().lock();
        this.userLock.writeLock().lock();

        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCartLock.writeLock().lock();
            this.guestCarts.put(userDTO.getName(), new Pair<>(userDTO.getShoppingCart(), System.currentTimeMillis()));
            this.guestCartLock.writeLock().unlock();
        }

        if(userDTO.getState() != UserStateEnum.GUEST)
            this.users.put(userDTO.getName(), new Pair<>(userDTO, System.currentTimeMillis()));

        this.stores.put(storeDTO.getStoreID(), new Pair<>(storeDTO, System.currentTimeMillis()));

        if(!useLocal){
            this.userSaveCache.add(new Pair<>(userDTO, DBOperation.SAVE));
            this.storeSaveCache.add(new Pair<>(storeDTO, DBOperation.SAVE));

            synchronized (this){
                notifyAll();
            }
        }

        this.storeLock.writeLock().unlock();
        this.userLock.writeLock().unlock();
    }

    public void saveUserStoreAndProduct(UserDTO userDTO, StoreDTO storeDTO, ProductDTO productDTO){
        this.storeLock.writeLock().lock();
        this.userLock.writeLock().lock();
        this.productLock.writeLock().lock();

        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCartLock.writeLock().lock();
            this.guestCarts.put(userDTO.getName(), new Pair<>(userDTO.getShoppingCart(), System.currentTimeMillis()));
            this.guestCartLock.writeLock().unlock();
        }

        if(userDTO.getState() != UserStateEnum.GUEST)
            this.users.put(userDTO.getName(), new Pair<>(userDTO, System.currentTimeMillis()));

        this.stores.put(storeDTO.getStoreID(), new Pair<>(storeDTO, System.currentTimeMillis()));

        this.products.put(productDTO.getProductID(), new Pair<>(productDTO, System.currentTimeMillis()));

        if(!useLocal){
            this.userSaveCache.add(new Pair<>(userDTO, DBOperation.SAVE));
            this.storeSaveCache.add(new Pair<>(storeDTO, DBOperation.SAVE));
            this.productSaveCache.add(new Pair<>(productDTO, DBOperation.SAVE));

            synchronized (this){
                notifyAll();
            }
        }

        this.storeLock.writeLock().unlock();
        this.userLock.writeLock().unlock();
        this.productLock.writeLock().unlock();
    }

    public void saveStoreAndProduct(StoreDTO storeDTO, ProductDTO productDTO){
        this.storeLock.writeLock().lock();
        this.productLock.writeLock().lock();

        stores.put(storeDTO.getStoreID(), new Pair<>(storeDTO, System.currentTimeMillis()));
        products.put(productDTO.getProductID(), new Pair<>(productDTO, System.currentTimeMillis()));

        if(!useLocal){
            this.storeSaveCache.add(new Pair<>(storeDTO, DBOperation.SAVE));
            this.productSaveCache.add(new Pair<>(productDTO, DBOperation.SAVE));

            synchronized (this){
                notifyAll();
            }
        }

        this.storeLock.writeLock().unlock();
        this.productLock.writeLock().unlock();
    }

    public void saveStoreRemoveProduct(StoreDTO storeDTO, ProductDTO productDTO){
        this.storeLock.writeLock().lock();
        this.productLock.writeLock().lock();

        stores.put(storeDTO.getStoreID(), new Pair<>(storeDTO, System.currentTimeMillis()));
        products.remove(productDTO.getProductID());

        if(!useLocal){
            this.storeSaveCache.add(new Pair<>(storeDTO, DBOperation.SAVE));
            this.productSaveCache.add(new Pair<>(productDTO, DBOperation.DELETE));

            synchronized (this){
                notifyAll();
            }
        }

        this.storeLock.writeLock().unlock();
        this.productLock.writeLock().unlock();
    }

    public UserDTO getUser(String username){
        UserDTO userDTO = null;
        this.userLock.writeLock().lock();

        if(this.users.containsKey(username)){
            userDTO = this.users.get(username).getFirst();
            this.users.put(username, new Pair<>(userDTO, System.currentTimeMillis()));
        }
        else if(!useLocal){
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
                    AndCompositionDiscountRuleDTO.class,
                    CategoryDiscountRuleDTO.class,
                    CompoundDiscountRuleDTO.class,
                    ConditionalCategoryDiscountRuleDTO.class,
                    ConditionalProductDiscountRuleDTO.class,
                    ConditionalStoreDiscountRuleDTO.class,
                    DiscountRuleDTO.class,
                    LeafDiscountRuleDTO.class,
                    MaximumCompositionDiscountRuleDTO.class,
                    OrCompositionDiscountRuleDTO.class,
                    ProductDiscountRuleDTO.class,
                    StoreDiscountRuleDTO.class,
                    SumCompositionDiscountRuleDTO.class,
                    TermsCompositionDiscountRuleDTO.class,
                    XorCompositionDiscountRuleDTO.class,

                    IntPermsListPair.class,
                    IntStringListPair.class,
                    PredPair.class,
                    ProductIntPair.class,

                    BasketPredicateDTO.class,
                    CategoryPredicateDTO.class,
                    PredicateDTO.class,
                    ProductPredicateDTO.class,
                    StorePredicateDTO.class,

                    AndCompositionPurchaseRuleDTO.class,
                    BasketPurchaseRuleDTO.class,
                    CategoryPurchaseRuleDTO.class,
                    CompoundPurchaseRuleDTO.class,
                    ConditioningCompositionPurchaseRuleDTO.class,
                    LeafPurchaseRuleDTO.class,
                    OrCompositionPurchaseRuleDTO.class,
                    ProductPurchaseRuleDTO.class,
                    PurchaseRuleDTO.class
            ).build();
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));

            try (MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(this.dbURL))
                    .codecRegistry(pojoCodecRegistry).build())) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);
                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                mapper.map(AndCompositionDiscountRuleDTO.class);
                mapper.map(CategoryDiscountRuleDTO.class);
                mapper.map(CompoundDiscountRuleDTO.class);
                mapper.map(ConditionalCategoryDiscountRuleDTO.class);
                mapper.map(ConditionalProductDiscountRuleDTO.class);
                mapper.map(ConditionalStoreDiscountRuleDTO.class);
                mapper.map(DiscountRuleDTO.class);
                mapper.map(LeafDiscountRuleDTO.class);
                mapper.map(MaximumCompositionDiscountRuleDTO.class);
                mapper.map(OrCompositionDiscountRuleDTO.class);
                mapper.map(ProductDiscountRuleDTO.class);
                mapper.map(StoreDiscountRuleDTO.class);
                mapper.map(SumCompositionDiscountRuleDTO.class);
                mapper.map(TermsCompositionDiscountRuleDTO.class);
                mapper.map(XorCompositionDiscountRuleDTO.class);

                mapper.map(IntPermsListPair.class);
                mapper.map(IntStringListPair.class);
                mapper.map(PredPair.class);
                mapper.map(ProductIntPair.class);

                mapper.map(BasketPredicateDTO.class);
                mapper.map(CategoryPredicateDTO.class);
                mapper.map(PredicateDTO.class);
                mapper.map(ProductPredicateDTO.class);
                mapper.map(StorePredicateDTO.class);

                mapper.map(AndCompositionPurchaseRuleDTO.class);
                mapper.map(BasketPurchaseRuleDTO.class);
                mapper.map(CategoryPurchaseRuleDTO.class);
                mapper.map(CompoundPurchaseRuleDTO.class);
                mapper.map(ConditioningCompositionPurchaseRuleDTO.class);
                mapper.map(LeafPurchaseRuleDTO.class);
                mapper.map(OrCompositionPurchaseRuleDTO.class);
                mapper.map(ProductPurchaseRuleDTO.class);
                mapper.map(PurchaseRuleDTO.class);

                userDTO = datastore.find(UserDTO.class)
                        // filters find relevant entries
                        .filter(
                                Filters.eq("name", username)
                        ).first();
            }
            catch(Exception e){
                System.out.println("Exception received: " + e.getMessage());
                this.userLock.writeLock().unlock();
                return getUser(username); // timeout, try again
            }

            if(userDTO != null)
                this.users.put(username, new Pair<>(userDTO, System.currentTimeMillis()));
        }

        this.userLock.writeLock().unlock();
        return userDTO;
    }

    public void insertUser(UserDTO userDTO){
        this.userLock.writeLock().lock();
        if(userDTO.getState() == UserStateEnum.GUEST) {
            this.guestCartLock.writeLock().lock();
            this.guestCarts.put(userDTO.getName(), new Pair<>(userDTO.getShoppingCart(), System.currentTimeMillis()));
            this.guestCartLock.writeLock().unlock();
        }
        else{
            this.users.put(userDTO.getName(), new Pair<>(userDTO, System.currentTimeMillis()));
            if(!useLocal) {
                this.userSaveCache.add(new Pair<>(userDTO, DBOperation.SAVE));

                synchronized (this) {
                    notifyAll();
                }
            }
        }

        this.userLock.writeLock().unlock();
    }

    public boolean saveUsers(List<UserDTO> userDTOList){
        this.userLock.writeLock().lock();
        boolean addedToCache = false;

        for(UserDTO userDTO : userDTOList) {
            if (userDTO.getState() == UserStateEnum.GUEST) {
                this.guestCartLock.writeLock().lock();
                this.guestCarts.put(userDTO.getName(), new Pair<>(userDTO.getShoppingCart(), System.currentTimeMillis()));
                this.guestCartLock.writeLock().unlock();
            }
            else{
                this.users.put(userDTO.getName(), new Pair<>(userDTO, System.currentTimeMillis()));
                if(!useLocal) {
                    this.userSaveCache.add(new Pair<>(userDTO, DBOperation.SAVE));
                    addedToCache = true;
                }
            }
        }
        if(!useLocal && addedToCache){
            synchronized (this) {
                notifyAll();
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
            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
                    AndCompositionDiscountRuleDTO.class,
                    CategoryDiscountRuleDTO.class,
                    CompoundDiscountRuleDTO.class,
                    ConditionalCategoryDiscountRuleDTO.class,
                    ConditionalProductDiscountRuleDTO.class,
                    ConditionalStoreDiscountRuleDTO.class,
                    DiscountRuleDTO.class,
                    LeafDiscountRuleDTO.class,
                    MaximumCompositionDiscountRuleDTO.class,
                    OrCompositionDiscountRuleDTO.class,
                    ProductDiscountRuleDTO.class,
                    StoreDiscountRuleDTO.class,
                    SumCompositionDiscountRuleDTO.class,
                    TermsCompositionDiscountRuleDTO.class,
                    XorCompositionDiscountRuleDTO.class,

                    IntPermsListPair.class,
                    IntStringListPair.class,
                    PredPair.class,
                    ProductIntPair.class,

                    BasketPredicateDTO.class,
                    CategoryPredicateDTO.class,
                    PredicateDTO.class,
                    ProductPredicateDTO.class,
                    StorePredicateDTO.class,

                    AndCompositionPurchaseRuleDTO.class,
                    BasketPurchaseRuleDTO.class,
                    CategoryPurchaseRuleDTO.class,
                    CompoundPurchaseRuleDTO.class,
                    ConditioningCompositionPurchaseRuleDTO.class,
                    LeafPurchaseRuleDTO.class,
                    OrCompositionPurchaseRuleDTO.class,
                    ProductPurchaseRuleDTO.class,
                    PurchaseRuleDTO.class
            ).build();
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));

            try (MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(this.dbURL))
                    .codecRegistry(pojoCodecRegistry).build())) {
                Datastore datastore = Morphia.createDatastore(mongoClient, this.dbName);

                Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
                mapper.mapPackage("Server.DAL");
                mapper.mapPackage("Server.DAL.DiscountRuleDTOs");
                mapper.mapPackage("Server.DAL.PredicateDTOs");
                mapper.mapPackage("Server.DAL.PurchaseRuleDTOs");
                mapper.mapPackage("Server.DAL.PairDTOs");

                mapper.map(AndCompositionDiscountRuleDTO.class);
                mapper.map(CategoryDiscountRuleDTO.class);
                mapper.map(CompoundDiscountRuleDTO.class);
                mapper.map(ConditionalCategoryDiscountRuleDTO.class);
                mapper.map(ConditionalProductDiscountRuleDTO.class);
                mapper.map(ConditionalStoreDiscountRuleDTO.class);
                mapper.map(DiscountRuleDTO.class);
                mapper.map(LeafDiscountRuleDTO.class);
                mapper.map(MaximumCompositionDiscountRuleDTO.class);
                mapper.map(OrCompositionDiscountRuleDTO.class);
                mapper.map(ProductDiscountRuleDTO.class);
                mapper.map(StoreDiscountRuleDTO.class);
                mapper.map(SumCompositionDiscountRuleDTO.class);
                mapper.map(TermsCompositionDiscountRuleDTO.class);
                mapper.map(XorCompositionDiscountRuleDTO.class);

                mapper.map(IntPermsListPair.class);
                mapper.map(IntStringListPair.class);
                mapper.map(PredPair.class);
                mapper.map(ProductIntPair.class);

                mapper.map(BasketPredicateDTO.class);
                mapper.map(CategoryPredicateDTO.class);
                mapper.map(PredicateDTO.class);
                mapper.map(ProductPredicateDTO.class);
                mapper.map(StorePredicateDTO.class);

                mapper.map(AndCompositionPurchaseRuleDTO.class);
                mapper.map(BasketPurchaseRuleDTO.class);
                mapper.map(CategoryPurchaseRuleDTO.class);
                mapper.map(CompoundPurchaseRuleDTO.class);
                mapper.map(ConditioningCompositionPurchaseRuleDTO.class);
                mapper.map(LeafPurchaseRuleDTO.class);
                mapper.map(OrCompositionPurchaseRuleDTO.class);
                mapper.map(ProductPurchaseRuleDTO.class);
                mapper.map(PurchaseRuleDTO.class);

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
            } catch (Exception e) {
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
                mongoClient.getDatabase(this.dbName).getCollection("dailyCounters").drop();

            } catch (Exception e) {
                System.out.println("Exception received: " + e.getMessage());
                resetDatabase(); // timeout, try again
            }
        }
    }




}
