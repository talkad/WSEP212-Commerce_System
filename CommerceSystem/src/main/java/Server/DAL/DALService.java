package Server.DAL;

import Server.DAL.DiscountRuleDTOs.StoreDiscountRuleDTO;
import Server.Domain.UserManager.UserStateEnum;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.experimental.MorphiaSession;
import dev.morphia.mapping.Mapper;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.codec.PrimitiveCodecRegistry;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import dev.morphia.query.experimental.filters.Filters;
import org.bson.codecs.configuration.CodecRegistries;

import java.util.*;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DALService {

    private static class CreateSafeThreadSingleton {
        private static final DALService INSTANCE = new DALService();
    }

    public static DALService getInstance() {
        return DALService.CreateSafeThreadSingleton.INSTANCE;
    }

    private DALService() {
//        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
//        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");
//
//        Mapper mapper = new Mapper(datastore, MongoClientSettings.getDefaultCodecRegistry(), MapperOptions.DEFAULT);
//        mapper.mapPackage("Server.DAL");
//        List<Class> classes = new Vector<>();
//        classes.add(StoreDiscountRuleDTO.class);
//        mapper.map(classes);
//
//        mongoClient.close();
    }

    public void savePurchase(UserDTO userDTO, List<StoreDTO> storeDTOs) {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        try(MorphiaSession session = datastore.startSession()){
            session.startTransaction();

            // stage changes to commit
            if(userDTO.getState() != UserStateEnum.GUEST)
                session.save(userDTO);

            session.save(storeDTOs);

            // commit changes
            session.commitTransaction();
        }

        mongoClient.close();
    }

    public PublisherDTO getPublisher() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        PublisherDTO publisherDTO = datastore.find(PublisherDTO.class).first();

        mongoClient.close();

        return publisherDTO;
    }

    public void savePublisher(PublisherDTO publisherDTO) {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        datastore.save(publisherDTO);

        mongoClient.close();
    }

    public void addAccount(AccountDTO accountDTO) {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        datastore.save(accountDTO);

        mongoClient.close();
    }

    public AccountDTO getAccount(String username){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        AccountDTO accountDTO = datastore.find(AccountDTO.class)
                // filters find relevant entries
                .filter(
                        Filters.eq("username", username)
                ).first();

        mongoClient.close();

        return accountDTO;
    }

    public void addAdmin(AdminAccountDTO adminAccountDTO) {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        datastore.save(adminAccountDTO);

        mongoClient.close();
    }

    public void insertStore(StoreDTO storeDTO) {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        datastore.save(storeDTO);

        mongoClient.close();
    }

    public StoreDTO getStore(int storeId) {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        StoreDTO storeDTO = datastore.find(StoreDTO.class)
                // filters find relevant entries
                .filter(
                        Filters.eq("storeID", storeId)
                ).first();

        mongoClient.close();

        return storeDTO;
    }

    public Collection<StoreDTO> getAllStores() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        List<StoreDTO> storeDTOList= datastore.find(StoreDTO.class)
                .filter(
                        Filters.gte("storeID", 0)
                )
//                .iterator(new FindOptions())
//                        .sort(Sort.ascending("storeID")))
                .iterator()
        .toList();

        mongoClient.close();

        return storeDTOList;
    }

    public void saveUserAndStore(UserDTO userDTO, StoreDTO storeDTO){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        try(MorphiaSession session = datastore.startSession()){
            session.startTransaction();

            // stage changes to commit
            if(userDTO.getState() != UserStateEnum.GUEST)
                session.save(userDTO);

            session.save(storeDTO);

            // commit changes
            session.commitTransaction();
        }

        mongoClient.close();
    }

    public void saveUserStoreAndProduct(UserDTO userDTO, StoreDTO storeDTO, ProductDTO productDTO){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        try(MorphiaSession session = datastore.startSession()){
            session.startTransaction();

            // stage changes to commit
            if(userDTO.getState() != UserStateEnum.GUEST)
                session.save(userDTO);

            session.save(storeDTO);
            session.save(productDTO);

            // commit changes
            session.commitTransaction();
        }

        mongoClient.close();
    }

    public void saveStoreAndProduct(StoreDTO storeDTO, ProductDTO productDTO){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        try(MorphiaSession session = datastore.startSession()){
            session.startTransaction();

            // stage changes to commit
            session.save(storeDTO);
            session.save(productDTO);

            // commit changes
            session.commitTransaction();
        }

        mongoClient.close();
    }

    public void saveStoreRemoveProduct(StoreDTO storeDTO, ProductDTO productDTO){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        try(MorphiaSession session = datastore.startSession()){
            session.startTransaction();

            // stage changes to commit
            session.save(storeDTO);
            session.delete(productDTO);

            // commit changes
            session.commitTransaction();
        }

        mongoClient.close();
    }

    public UserDTO getUser(String username){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        UserDTO userDTO = datastore.find(UserDTO.class)
                // filters find relevant entries
                .filter(
                        Filters.eq("name", username)
                ).first();

        mongoClient.close();

        return userDTO;
    }

    public void insertUser(UserDTO userDTO){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        if(userDTO.getState() != UserStateEnum.GUEST)
            datastore.save(userDTO);

        mongoClient.close();
    }

    public boolean saveUsers(List<UserDTO> userDTOList){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        Datastore datastore = Morphia.createDatastore(mongoClient, "commerceDatabase");

        boolean success = true;

        try(MorphiaSession session = datastore.startSession()){
            session.startTransaction();

            // stage changes to commit
            session.save(userDTOList);

            // commit changes
            session.commitTransaction();
        }
        catch(Exception e){
            success = false;
        }

        mongoClient.close();
        return success;
    }


    public int getNextAvailableStoreID(){
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
        return (int) (Math.random() * (10000 - 1)) + 1;
    }

    public void resetDatabase(){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://commerceserver:commerceserver@cluster0.gx2cx.mongodb.net/database1?retryWrites=true&w=majority");
        mongoClient.getDatabase("commerceDatabase").getCollection("users").drop();
        mongoClient.getDatabase("commerceDatabase").getCollection("stores").drop();
        mongoClient.getDatabase("commerceDatabase").getCollection("publishers").drop();
        mongoClient.getDatabase("commerceDatabase").getCollection("products").drop();
        mongoClient.getDatabase("commerceDatabase").getCollection("accounts").drop();
        mongoClient.getDatabase("commerceDatabase").getCollection("adminAccounts").drop();
        mongoClient.close();
    }




}
