package Server.Domain.UserManager;

import Server.DAL.*;
import Server.Domain.CommonClasses.Pair;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import Server.Domain.UserManager.DTOs.BasketClientDTO;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.DTOs.UserDTOTemp;
import Server.Service.DataObjects.OfferData;
import Server.Service.DataObjects.ReplyMessage;
import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {

    private UserState state;
    private List<Integer> storesOwned;

    public Appointment getAppointments() {
        return appointments;
    }

    private Map<Integer, List<PermissionsEnum>> storesManaged;
    private String name;
    private ShoppingCart shoppingCart;
    private PurchaseHistory purchaseHistory;
    private Appointment appointments;
    private Map<Integer, Offer> offers;

    private PendingMessages pendingMessages;

    private ReadWriteLock ownedLock;
    private Lock ownedWriteLock;
    private Lock ownedReadLock;

    private ReadWriteLock managedLock;
    private Lock managedWriteLock;
    private Lock managedReadLock;

    public User() {
        this.state = new Guest();

        ownedLock = new ReentrantReadWriteLock();
        ownedWriteLock = ownedLock.writeLock();
        ownedReadLock = ownedLock.readLock();

        managedLock = new ReentrantReadWriteLock();
        managedWriteLock = managedLock.writeLock();
        managedReadLock = managedLock.readLock();

        pendingMessages = new PendingMessages();

        this.storesOwned = new Vector<>();
        this.storesManaged = new ConcurrentHashMap<>();
        this.shoppingCart = new ShoppingCart();
        this.purchaseHistory = new PurchaseHistory();
        this.appointments = new Appointment();
    }

    public User(String username) {
        this.state = new Registered();
        this.name = username;

        ownedLock = new ReentrantReadWriteLock();
        ownedWriteLock = ownedLock.writeLock();
        ownedReadLock = ownedLock.readLock();

        managedLock = new ReentrantReadWriteLock();
        managedWriteLock = managedLock.writeLock();
        managedReadLock = managedLock.readLock();

        pendingMessages = new PendingMessages();

        this.storesOwned = new Vector<>();
        this.storesManaged = new ConcurrentHashMap<>();
        this.shoppingCart = new ShoppingCart();
        this.purchaseHistory = new PurchaseHistory();
        this.appointments = new Appointment();
    }

//    //TODO Old UserDTO
//    public User(UserDTOTemp userDTO) {
//        ownedLock = new ReentrantReadWriteLock();
//        ownedWriteLock = ownedLock.writeLock();
//        ownedReadLock = ownedLock.readLock();
//
//        managedLock = new ReentrantReadWriteLock();
//        managedWriteLock = managedLock.writeLock();
//        managedReadLock = managedLock.readLock();
//
//        if (UserDAO.getInstance().isAdmin(userDTO.getName())) {
//            this.state = new Admin();
//        } else {
//            this.state = new Registered();
//        }
//        this.storesOwned = userDTO.getStoresOwned();
//        this.storesManaged = userDTO.getStoresManaged();
//        this.name = userDTO.getName();
//        this.shoppingCart = userDTO.getShoppingCart();
//        this.purchaseHistory = userDTO.getPurchaseHistory();
//        this.appointments = userDTO.getAppointments();
//        this.offers = userDTO.getOffers();
//        this.pendingMessages = userDTO.getPendingMessages();
//    }

    public User(UserDTO userDTO){
        ownedLock = new ReentrantReadWriteLock();
        ownedWriteLock = ownedLock.writeLock();
        ownedReadLock = ownedLock.readLock();

        managedLock = new ReentrantReadWriteLock();
        managedWriteLock = managedLock.writeLock();
        managedReadLock = managedLock.readLock();

        UserStateEnum stateEnum = userDTO.getState();
        switch(stateEnum){
            case ADMIN:
                this.state = new Admin();
                break;
            case REGISTERED:
                this.state = new Registered();
                break;
            default:
                // TODO add error message
                this.state = new Guest();
                break;
        }

        this.storesOwned = new Vector<>(userDTO.getStoresOwned());

        this.storesManaged = new ConcurrentHashMap<>();
        List<Pair<Integer, List<PermissionsEnum>>> managedList = userDTO.getStoresManaged();
        if(managedList != null){
            for(Pair<Integer, List<PermissionsEnum>> pair : managedList){
                this.storesManaged.put(pair.getFirst(), new Vector<>(pair.getSecond()));
            }
        }

        this.name = userDTO.getName();
        this.shoppingCart = new ShoppingCart(userDTO.getShoppingCart());
        this.purchaseHistory = new PurchaseHistory(userDTO.getPurchaseHistory());
        this.appointments = new Appointment(userDTO.getAppointments());

        this.offers = new ConcurrentHashMap<>();
        List<OfferDTO> offersList = userDTO.getOffers();
        if(offersList != null){
            for(OfferDTO offer : offersList){
                this.offers.put(offer.getProductId(), new Offer(offer));
            }
        }

        this.pendingMessages = new PendingMessages(userDTO.getPendingMessages());
    }

    public UserDTO toDTO(){
        List<Pair<Integer, List<PermissionsEnum>>> managedList = new Vector<>();
        List<OfferDTO> offersList = new Vector<>();

        for(int key : this.getStoresManaged().keySet()){
            managedList.add(new Pair<>(key, this.getStoresManaged().get(key)));
        }

        for(int key : this.getOffers().keySet()){
            offersList.add(this.getOffers().get(key).toDTO());
        }

        return new UserDTO( this.state.getStateEnum(),
                            this.getStoresOwned(),
                            managedList,
                            this.getName(),
                            this.shoppingCart.toDTO(),
                            this.purchaseHistory.toDTO(),
                            this.appointments.toDTO(),
                            offersList,
                            this.pendingMessages.toDTO());

    }

    public List<Integer> getStoresOwned() {
        return storesOwned;
    }

    public void addStoresOwned(int storeId) {
        ownedWriteLock.lock();
        this.storesOwned.add(storeId);
        ownedWriteLock.unlock();
        DALService.getInstance().insertUser(this.toDTO());

        // subscribe to get notifications
//        Publisher.getInstance().subscribe(storeId, this.name);

    }

    public Map<Integer, List<PermissionsEnum>> getStoresManaged() {
        return storesManaged;
    }

    public void addStoresManaged(int storeId, List<PermissionsEnum> permission) {
        managedWriteLock.lock();
        this.storesManaged.put(storeId, permission);
        managedWriteLock.unlock();
        DALService.getInstance().insertUser(this.toDTO());
    }

    public String getName() {
        return name;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public PurchaseHistory getPurchaseHistory() {
        return purchaseHistory;
    }

    public Map<Integer, Offer> getOffers() {
        return offers;
    }

    public Response<Boolean> register() {
        if (state.allowed(PermissionsEnum.REGISTER, this)) {
            return new Response<>(true, false, "User is allowed to register");
        }
        return new Response<>(false, true, "User is not allowed to register");
    }


    public Response<Boolean> addToCart(int storeID, int productID) {
        Response<Boolean> response = this.shoppingCart.addProduct(storeID, productID);
        if(!response.isFailure()){
            DALService.getInstance().insertUser(this.toDTO());
        }
        return response;
    }

    public Response<List<BasketClientDTO>> getShoppingCartContents() {
        List<BasketClientDTO> basketsDTO = new LinkedList<>();
        Map<Integer, Map<Product, Integer>> baskets = shoppingCart.getBaskets();

        for(Integer storeID: baskets.keySet()){
            Store store = StoreController.getInstance().getStoreById(storeID);
            Set<ProductClientDTO> productClientDTOS = new ConcurrentSkipListSet<>();
            if(store != null){

                for(Product product: baskets.get(storeID).keySet()){
                    productClientDTOS.add(product.getProductDTO());
                }

                basketsDTO.add(new BasketClientDTO(storeID, store.getName(), productClientDTOS, baskets.get(storeID).values()));
            }
        }

        return new Response<>(basketsDTO, false, "get shopping cart occurred");
    }

    public Response<Boolean> removeProduct(int storeID, int productID) {
        Response<Boolean> response = this.shoppingCart.removeProduct(storeID, productID);
        if(!response.isFailure()){
            DALService.getInstance().insertUser(this.toDTO());
        }
        return response;
    }

    public Response<Boolean> logout() {
        if(this.state.allowed(PermissionsEnum.LOGOUT, this) )
            return new Response<>(true, false, "logged out successfully");
        return new Response<>(false, true, "Cannot logout without being logged in");
    }

    public Response<Integer> openStore(String storeName) {
        Response<Integer> result;
        if (!this.state.allowed(PermissionsEnum.OPEN_STORE, this)) {
            return new Response<>(-1, true, "Not allowed to open store");
        }

        result = StoreController.getInstance().openStore(storeName, this.name);
        if (!result.isFailure()) {
            ownedWriteLock.lock();
            this.storesOwned.add(result.getResult());
            ownedWriteLock.unlock();
            DALService.getInstance().saveUserAndStore(this.toDTO(), new StoreDTO(result.getResult(), storeName, this.name, new InventoryDTO(), true, new DiscountPolicyDTO(), new PurchasePolicyDTO(), 0, 0, new PurchaseHistoryDTO()));
            // subscribe to get notifications
            Publisher.getInstance().subscribe(result.getResult(), this.name);
        }
        return result;
    }

    public Response<List<PurchaseClientDTO>> getPurchaseHistoryContents() {
        if (this.state.allowed(PermissionsEnum.GET_PURCHASE_HISTORY, this)) {
            return new Response<>(this.purchaseHistory.getPurchases(), false, "get purchase history successfully");
        }
        return new Response<>(new LinkedList<>(), true, "User not allowed to view purchase history");
    }

    public Response<Boolean> updateProductQuantity(int storeID, int productID, int amount) {
        Response<Boolean> response = this.shoppingCart.updateProductQuantity(storeID, productID, amount);
        if(!response.isFailure()){
            DALService.getInstance().insertUser(this.toDTO());
        }
        return response;
    }

    public Response<Boolean> addProductReview(int storeID, int productID, String reviewStr) {
        Store store;
        Response<Review> reviewRes = Review.createReview(name, reviewStr);
        Response<Product> product;

        if(reviewRes.isFailure())
            return new Response<>(false, true, reviewRes.getErrMsg());

        if (this.state.allowed(PermissionsEnum.REVIEW_PRODUCT, this)) {

            if (purchaseHistory.isPurchased(productID)) {
                store = StoreController.getInstance().getStoreById(storeID);

                if(store == null){
                    return new Response<>(false, true, "This store doesn't exists");
                }

                product = StoreController.getInstance().getProduct(storeID, productID);
                if(product.isFailure())
                    return new Response<>(false, true, "The product " + product + " doesn't exists in store " + storeID);

                Publisher.getInstance().notify(storeID, new ReplyMessage("notification", "New review to product "+ product.getResult().getName() + " (" +productID + ") : " + reviewStr, "addProductReview"));

                Response<Boolean> response = store.addProductReview(productID, reviewRes.getResult());

                if(!response.isFailure()){
                    DALService.getInstance().saveUserStoreAndProduct(this.toDTO(), store.toDTO(), product.getResult().toDTO());
                }

                return response;
            }

            return new Response<>(false, true, "The given product wasn't purchased before");
        } else {
            return new Response<>(false, true, "User not allowed to review product");
        }
    }

    public Response<Boolean> addProductsToStore(ProductClientDTO productDTO, int amount) {
        Store store;

        if (this.state.allowed(PermissionsEnum.ADD_PRODUCT_TO_STORE, this, productDTO.getStoreID())) {

            store = StoreController.getInstance().getStoreById(productDTO.getStoreID());

            if(store == null){
                return new Response<>(false, true, "This store doesn't exists");
            }

            Response<Boolean> response = store.addProduct(productDTO, amount);

            if(!response.isFailure()){
                DALService.getInstance().saveStoreAndProduct(store.toDTO(), store.getProduct(productDTO.getProductID()).getResult().toDTO());
            }

            return response;
        }
        return new Response<>(false, true, "The user is not allowed to add products to the store");
    }

    public Response<Boolean> removeProductsFromStore(int storeID, int productID, int amount) {
        Store store;

        if (this.state.allowed(PermissionsEnum.REMOVE_PRODUCT_FROM_STORE, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);

            if(store == null){
                return new Response<>(false, true, "This store doesn't exists");
            }
            Response<Product> productResponse = store.getProduct(productID);
            Response<Boolean> response = store.removeProduct(productID, amount);

            if(!response.isFailure()){
                DALService.getInstance().saveStoreRemoveProduct(store.toDTO(), productResponse.getResult().toDTO());
            }

            return response;
        }
        return new Response<>(false, true, "The user is not allowed to remove products from the store");
    }

    public Response<Boolean> updateProductInfo(int storeID, int productID, double newPrice, String newName) {
        if (this.state.allowed(PermissionsEnum.UPDATE_PRODUCT_PRICE, this, storeID)) {
            Response<Boolean> response = StoreController.getInstance().updateProductInfo(storeID, productID, newPrice, newName);
            if(!response.isFailure()){
                Store store = StoreController.getInstance().getStoreById(storeID);
                Product product = store.getProduct(productID).getResult();
                DALService.getInstance().saveStoreAndProduct(store.toDTO(), product.toDTO());
            }
            return response;
        }
        return new Response<>(false, true, "The user is not allowed to edit products information in the store");
    }

//    public Response<Boolean> addDiscountPolicy(int storeID, Policy policy) { //todo - check that
//        if (this.state.allowed(Permissions.ADD_DISCOUNT_POLICY, this, storeID)) {
//
//            Store store = StoreController.getInstance().getStoreById(storeID);
//            if(store != null)
//                return null;
////                return store.aaaa
//
//            return new Response<>(false, true, "The given store doesn't exists");
//        }
//        return new Response<>(false, true, "The user is not allowed to edit products information in the store");
//    }

    public Response<Boolean> appointOwner(String newOwner, int storeId) {
        Response<Boolean> res;
        if (this.state.allowed(PermissionsEnum.APPOINT_OWNER, this, storeId)) {
            UserDTO userDTO = DALService.getInstance().getUser(newOwner);
            if (userDTO != null) {
                boolean managed = false;
                List<Pair<Integer, List<PermissionsEnum>>> managedList = userDTO.getStoresManaged();
                for(Pair<Integer, List<PermissionsEnum>> pair : managedList){
                    if(pair.getFirst() == storeId)
                        managed = true;
                }
                if (!userDTO.getStoresOwned().contains(storeId) && !managed) {
                    this.appointments.addAppointment(storeId, newOwner);
                    // TODO thread safe?
                    List<Integer> ownedList = userDTO.getStoresOwned();
                    ownedList.add(storeId);
                    userDTO.setStoresOwned(ownedList);
                    List<UserDTO> userDTOS = new Vector<>();
                    userDTOS.add(this.toDTO());
                    userDTOS.add(userDTO);

                    res = new Response<>(true, !DALService.getInstance().saveUsers(userDTOS), "saved updated users");

                    if(!res.isFailure())
                        Publisher.getInstance().subscribe(storeId, newOwner);

                    return res;
                }
                return new Response<>(false, true, "User was already appointed in this store");
            } else {
                return new Response<>(false, true, "User does not exist in the system");
            }
        }
        return new Response<>(false, true, "User isn't allowed to appoint owner");
    }

    public Response<Boolean> appointManager(String newManager, int storeId) {
        if (this.state.allowed(PermissionsEnum.APPOINT_MANAGER, this, storeId)) {
            UserDTO userDTO = DALService.getInstance().getUser(newManager);
            if (userDTO != null) {
                boolean managed = false;
                List<Pair<Integer, List<PermissionsEnum>>> managedList = userDTO.getStoresManaged();
                for(Pair<Integer, List<PermissionsEnum>> pair : managedList){
                    if(pair.getFirst() == storeId)
                        managed = true;
                }
                if (!userDTO.getStoresOwned().contains(storeId) && !managed) {
                    this.appointments.addAppointment(storeId, newManager);
                    // todo maybe thread safe
                    List<PermissionsEnum> permissions = new Vector<>();
                    permissions.add(PermissionsEnum.RECEIVE_STORE_WORKER_INFO);
                    managedList.add(new Pair<>(storeId, permissions));
                    userDTO.setStoresManaged(managedList);
                    List<UserDTO> userDTOS = new Vector<>();
                    userDTOS.add(this.toDTO());
                    userDTOS.add(userDTO);

                    return new Response<>(true, !DALService.getInstance().saveUsers(userDTOS), "saved updated users");
                }
                return new Response<>(false, true, "User was already appointed in this store");
            } else {
                return new Response<>(false, true, "User does not exist in the system");
            }
        }
        return new Response<>(false, true, "User isn't allowed to appoint manager");
    }

    public boolean isOwner(int storeId) {
        ownedReadLock.lock();
        boolean result = this.storesOwned.contains(storeId);
        ownedReadLock.unlock();
        return result;
    }

    public boolean isManager(int storeId) {
        managedReadLock.lock();
        boolean result = this.storesManaged.containsKey(storeId);
        managedReadLock.unlock();
        return result;
    }

    public Response<String> removeAppointment(String appointeeName, int storeID) {
        if (this.storesOwned.contains(storeID)) {
            Response<String> res = this.appointments.removeAppointment(storeID, appointeeName);
            if(!res.isFailure()){
                Publisher.getInstance().notify(appointeeName, new ReplyMessage("notification", "Your ownership canceled at store "+ storeID, "removeAppointment"));
                Publisher.getInstance().unsubscribe(storeID, appointeeName);
            }
            return res;
        } else if (this.storesManaged.containsKey(storeID)) {
            return this.appointments.removeAppointment(storeID, appointeeName);
        }
        return new Response<>("problem", true, "user not appointed by this appointer");
    }

    public void removeRole(int storeID) {
        ownedWriteLock.lock();
        if (this.storesOwned.contains(storeID)) {
            this.storesOwned.remove(Integer.valueOf(storeID));
            ownedWriteLock.unlock();
        } else {
            ownedWriteLock.unlock();
            managedWriteLock.lock();
            this.storesManaged.remove(storeID);
            managedWriteLock.unlock();
        }
    }

    public Response<Boolean> appointedAndAllowed(int storeId, String appointeeName, PermissionsEnum permission) {
        if (this.state.allowed(permission, this, storeId)) {
            if (this.appointments.contains(storeId, appointeeName)) {
                return new Response<>(true, false, "success");
            } else {
                return new Response<>(false, true, "Attempted to remove a user that wasn't appointed by him");
            }
        } else {
            return new Response<>(false, true, "User not allowed to " + permission.name());
        }
    }

    public Response<Boolean> addPermission(int storeId, String permitted, PermissionsEnum permission) {     // req 4.6
        if (this.state.allowed(PermissionsEnum.ADD_PERMISSION, this, storeId) && this.appointments.contains(storeId, permitted)) {
            User permittedUser = new User(DALService.getInstance().getUser(permitted));
            if(!permittedUser.getStoresManaged().containsKey(storeId) || !permittedUser.getStoresManaged().get(storeId).contains(permission)) {
                permittedUser.addSelfPermission(storeId, permission);
                return new Response<>(true, false, "Added permission");
            }
            else return new Response<>(false, true, "The user already has the permission");
        } else {
            return new Response<>(false, true, "User not allowed to add permissions to this user");
        }
    }

    public Response<Boolean> removePermission(int storeId, String permitted, PermissionsEnum permission) {     // req 4.6
        if (this.state.allowed(PermissionsEnum.REMOVE_PERMISSION, this, storeId) && this.appointments.contains(storeId, permitted)) {
            User permittedUser = new User(DALService.getInstance().getUser(permitted));
            permittedUser.removeSelfPermission(storeId, permission);
            return new Response<>(true, false, "Removed permission");
        } else {
            return new Response<>(false, true, "User not allowed to remove permissions from this user");
        }
    }

    public void addSelfPermission(int storeId, PermissionsEnum permission) {
        managedWriteLock.lock();
        this.storesManaged.get(storeId).add(permission);
        managedWriteLock.unlock();
        DALService.getInstance().insertUser(this.toDTO());
    }

    public void removeSelfPermission(int storeId, PermissionsEnum permission) {
        managedWriteLock.lock();
        this.storesManaged.get(storeId).remove(permission);
        managedWriteLock.unlock();
        DALService.getInstance().insertUser(this.toDTO());
    }

    public Response<List<PurchaseClientDTO>> getUserPurchaseHistory(String username) {       // req 6.4
        if (this.state.allowed(PermissionsEnum.RECEIVE_GENERAL_HISTORY, this)) {
            UserDTO userDTO = DALService.getInstance().getUser(username);
            if (userDTO != null) {
                return new Response<>(new User(userDTO).getPurchaseHistory().getPurchases(), false, "no error");//todo combine dto pull
            } else {
                return new Response<>(new Vector<>(), true, "User does not exist");
            }
        } else {
            return new Response<>(new Vector<>(), true, "User not allowed to view user's purchase");
        }
    }

    public Response<Collection<PurchaseClientDTO>> getStorePurchaseHistory(int storeID) {
        Store store;

        if (this.state.allowed(PermissionsEnum.RECEIVE_GENERAL_HISTORY, this)) {
            store = StoreController.getInstance().getStoreById(storeID);

            if(store == null){
                return new Response<>(new LinkedList<>(), true, "This store doesn't exists");
            }

            return store.getPurchaseHistory();
        }
        return new Response<>(new LinkedList<>(), true, "User not allowed to view user's purchase");//todo empty list or null
    }

    public Response<Boolean> getStoreWorkersDetails(int storeID) {       // req 4.9
        return new Response<>(true, !this.state.allowed(PermissionsEnum.RECEIVE_STORE_WORKER_INFO, this, storeID), "User not allowed to receive store workers information");
    }

    public Response<Collection<PurchaseClientDTO>> getPurchaseDetails(int storeID) {     // req 4.11
        Store store;

        if (this.state.allowed(PermissionsEnum.RECEIVE_STORE_HISTORY, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);

            if(store == null){
                return new Response<>(new LinkedHashSet<>(), true, "This store doesn't exists");
            }

            return store.getPurchaseHistory();
        } else {
            return new Response<>(new LinkedHashSet<>(), true, "User not allowed to receive store history");
        }
    }

    public void addToPurchaseHistory(List<PurchaseClientDTO> result) {
        if (this.purchaseHistory != null){
            purchaseHistory.addPurchase(result);
        }
    }

    public void emptyCart(){
        this.shoppingCart = new ShoppingCart();
    }

    public List<ReplyMessage> getPendingMessages(){
        return pendingMessages.getPendingMessages();
    }

    public void clearPendingMessages(){
        pendingMessages.clear();
    }

    public void addPendingMessage(ReplyMessage msg){
        pendingMessages.addMessage(msg);
    }

    public void sendPendingNotifications() {
        // send pending notifications to the user
        for(ReplyMessage msg: getPendingMessages()){
            Publisher.getInstance().notify(name, msg);
        }
        clearPendingMessages();
    }

    public Response<Boolean> purchase(List<PurchaseClientDTO> purchase) {
        StringBuilder msg;

        // notify to subscribers about purchase and update DB
        for(Integer storeID : getShoppingCart().getBaskets().keySet()){
            msg = new StringBuilder("Purchase occurred:\n");
            for(Product productDTO: getShoppingCart().getBasket(storeID).keySet()){
                msg.append("product name: ").append(productDTO.getName()).append(", amount: ").append(shoppingCart.getBasket(storeID).get(productDTO)).append("\n");
            }

            Publisher.getInstance().notify(storeID, new ReplyMessage("notification", msg.toString(), "purchase"));
        }

        addToPurchaseHistory(purchase);
        emptyCart();

        List<StoreDTO> storeDTOS = new Vector<>();
        for(PurchaseClientDTO purchaseClientDTO : purchase){
            int storeID = purchaseClientDTO.getBasket().getStoreID();
            Store store = StoreController.getInstance().getStoreById(storeID);
            storeDTOS.add(store.toDTO());
        }

        DALService.getInstance().savePurchase(this.toDTO(), storeDTOS);

        return new Response<>(true, false, "The purchase occurred");
    }

    public Response<List<String>> getPermissions(int storeID){
        List<PermissionsEnum> permissions = null;
        List<String> permissionsStr = new LinkedList<>();

        ownedReadLock.lock();
        if(this.storesOwned.contains(storeID))
        {
            permissions = Arrays.asList( PermissionsEnum.ADD_PRODUCT_TO_STORE, PermissionsEnum.REMOVE_PRODUCT_FROM_STORE,
                    PermissionsEnum.UPDATE_PRODUCT_PRICE, PermissionsEnum.VIEW_DISCOUNT_POLICY,  PermissionsEnum.VIEW_PURCHASE_POLICY,
                    PermissionsEnum.ADD_DISCOUNT_RULE, PermissionsEnum.ADD_PURCHASE_RULE, PermissionsEnum.REMOVE_DISCOUNT_RULE,
                    PermissionsEnum.REMOVE_PURCHASE_RULE, PermissionsEnum.APPOINT_OWNER, PermissionsEnum.REMOVE_OWNER_APPOINTMENT,
                    PermissionsEnum.APPOINT_MANAGER, PermissionsEnum.ADD_PERMISSION, PermissionsEnum.REMOVE_PERMISSION,
                    PermissionsEnum.REMOVE_MANAGER_APPOINTMENT, PermissionsEnum.RECEIVE_STORE_WORKER_INFO,
                    PermissionsEnum.RECEIVE_STORE_HISTORY, PermissionsEnum.RECEIVE_STORE_REVENUE, PermissionsEnum.REPLY_TO_BID
            );
        }
        ownedReadLock.unlock();
        managedReadLock.lock();
        if(this.storesManaged.containsKey(storeID))
        {
            permissions = storesManaged.get(storeID);
        }
        managedReadLock.unlock();

        if(permissions == null)
            return new Response<>(new LinkedList<>(), true, "user "+ name + " doesn't manage the given store");

        for(PermissionsEnum per: permissions)
            permissionsStr.add(per.name());

        return new Response<>(permissionsStr, false, "OK");
    }

    public Response<Boolean> addDiscountRule(int storeID, DiscountRule discountRule) {
        Store store;
        if(this.state.allowed(PermissionsEnum.ADD_DISCOUNT_RULE, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);
            if (store != null) {
                Response<Boolean> response = store.addDiscountRule(discountRule);
                if(!response.isFailure()){
                    DALService.getInstance().insertStore(store.toDTO());
                }
                return response;
            }
            else {
                return new Response<>(false, true, "The given store doesn't exists");
            }
        }
        else {
            return new Response<>(false, true, "The user doesn't have the right permissions");
        }
    }

    public Response<Boolean> addPurchaseRule(int storeID, PurchaseRule purchaseRule) {
        Store store;
        if(this.state.allowed(PermissionsEnum.ADD_PURCHASE_RULE, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);
            if (store != null) {
                Response<Boolean> response = store.addPurchaseRule(purchaseRule);
                if(!response.isFailure()){
                    DALService.getInstance().insertStore(store.toDTO());
                }
                return response;
            }
            else {
                return new Response<>(false, true, "The given store doesn't exists");
            }
        }
        else {
            return new Response<>(false, true, "The user doesn't have the right permissions");
        }
    }


    public Response<Boolean> removeDiscountRule(int storeID, int discountRuleID) {
        Store store;
        if(this.state.allowed(PermissionsEnum.REMOVE_DISCOUNT_RULE, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);
            if (store != null) {
                Response<Boolean> response = store.removeDiscountRule(discountRuleID);
                if(!response.isFailure()){
                    DALService.getInstance().insertStore(store.toDTO());
                }
                return response;
            }
            else {
                return new Response<>(false, true, "The given store doesn't exists");
            }
        }
        else {
            return new Response<>(false, true, "The user doesn't have the right permissions");
        }
    }

    public Response<Boolean> removePurchaseRule(int storeID, int purchaseRuleID) {
        Store store;
        if(this.state.allowed(PermissionsEnum.REMOVE_PURCHASE_RULE, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);
            if (store != null) {
                Response<Boolean> response = store.removePurchaseRule(purchaseRuleID);
                if(!response.isFailure()){
                    DALService.getInstance().insertStore(store.toDTO());
                }
                return response;
            }
            else {
                return new Response<>(false, true, "The given store doesn't exists");
            }
        }
        else {
            return new Response<>(false, true, "The user doesn't have the right permissions");
        }
    }

    public Response<PurchasePolicy> getPurchasePolicy(int storeID) {
        Store store;
        if(this.state.allowed(PermissionsEnum.VIEW_PURCHASE_POLICY, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);
            if (store != null) {
                PurchasePolicy policy = store.getPurchasePolicy();
                return new Response<>(policy, false, "Successfully retrieved purchase policy");
            }
            else {
                return new Response<>(null, true, "The given store doesn't exists");
            }
        }
        else {
            return new Response<>(null, true, "The user doesn't have the right permissions");
        }
    }

    public Response<DiscountPolicy> getDiscountPolicy(int storeID) {
        Store store;
        if(this.state.allowed(PermissionsEnum.VIEW_DISCOUNT_POLICY, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);
            if (store != null) {
                DiscountPolicy policy = store.getDiscountPolicy();
                return new Response<>(policy, false, "Successfully retrieved discount policy");
            }
            else {
                return new Response<>(null, true, "The given store doesn't exists");
            }
        }
        else {
            return new Response<>(null, true, "The user doesn't have the right permissions");
        }
    }

    public Response<Double> getTotalSystemRevenue() {
        if(this.state.allowed(PermissionsEnum.RECEIVE_GENERAL_REVENUE, this)) { //todo- check this permission
            return new Response<>(StoreController.getInstance().getTotalSystemRevenue(), false, "System manager received total revenue");
        }
        else {
            return new Response<>(-1.0, true, "The user doesn't have the right permissions");
        }
    }

    public Response<Double> getTotalStoreRevenue(int storeID) {
        Store store;
        if(this.state.allowed(PermissionsEnum.RECEIVE_STORE_REVENUE, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);
            if (store != null) {
                return new Response<>(store.getTotalRevenue(), false, "Total revenue in store " + storeID);
            }
            else {
                return new Response<>(-1.0, true, "The given store doesn't exists");
            }
        }
        else {
            return new Response<>(-1.0, true, "The user doesn't have the right permissions");
        }
    }

    public Response<Boolean> bidOffer(int productID, int storeID, double priceOffer) {
        Store store = StoreController.getInstance().getStoreById(storeID);
        if (store != null)
            return new Response<>(false, true, "The given store doesn't exists");

        Offer offer = new Offer(productID, storeID, priceOffer);

        this.offers.put(productID, offer);
        DALService.getInstance().insertUser(this.toDTO());

        Gson gson = new Gson();
        Publisher.getInstance().notify(storeID, new ReplyMessage("reactiveNotification", gson.toJson(new OfferData(this.name, productID, priceOffer)), "bidOffer"));
        return new Response<>(true, false, "Bid offer sent successfully to store " +storeID+ " owners");
    }

    public Response<Boolean> removeOffer(int productId){
        if(this.offers.containsKey(productId)){
            offers.remove(productId);
            DALService.getInstance().insertUser(this.toDTO());
            return new Response<>(true, false, "successfully removed offer");
        }
        else{
            return new Response<>(false, true, "offer doesn't exist");
        }
    }

    public Response<Boolean> changeOfferStatus(String offeringUsername, int productID, int storeID, double bidReply) {

        if (this.state.allowed(PermissionsEnum.REPLY_TO_BID, this, storeID)) {
            if (bidReply == -1) {

                Publisher.getInstance().notify(offeringUsername, new ReplyMessage("notification", "The offer was declined.", "changeOfferStatus"));
                this.offers.remove(productID);
                DALService.getInstance().insertUser(this.toDTO());
                return new Response<>(true, false, "The offer was declined.");

            } else if (bidReply == 0) {

                Publisher.getInstance().notify(offeringUsername, new ReplyMessage("notification", "The offer was accepted.", "changeOfferStatus"));
                this.offers.get(productID).setState(OfferState.APPROVED);
                this.offers.get(productID).setOfferReply(bidReply);
                DALService.getInstance().insertUser(this.toDTO());
                return new Response<>(true, false, "The offer was accepted.");

            } else {

                Publisher.getInstance().notify(offeringUsername, new ReplyMessage("notification", "The store presented a counter offer - " + bidReply, "changeOfferStatus"));
                this.offers.get(productID).setState(OfferState.APPROVED);
                this.offers.get(productID).setOfferReply(bidReply);
                DALService.getInstance().insertUser(this.toDTO());
                return new Response<>(true, false, "The store presented a counter offer");

            }
        }
        else{
            return new Response<>(false, true, "The user doesn't have the right permissions");
        }
    }

    public Response<Boolean> bidUserReply(PurchaseClientDTO purchase, int storeID) {
        List<PurchaseClientDTO> purchases = new LinkedList<>();
        purchases.add(purchase);

        if (this.purchaseHistory != null){
            purchaseHistory.addPurchase(purchases);
        }

        ProductClientDTO product = null;
        for(ProductClientDTO p: purchase.getBasket().getProductsDTO())
            product = p;

        if(product == null)
            return new Response<>(false, true, "The purchase failed");

        Publisher.getInstance().notify(storeID, new ReplyMessage("notification", "Product " + product.getName() + " purchased successfully", "bidUserReply"));

        Store store = StoreController.getInstance().getStoreById(storeID);
        DALService.getInstance().saveUserAndStore(this.toDTO(), store.toDTO());

        return new Response<>(true, false, "The purchase occurred successfully");
    }

    public Response<List<Integer>> getStoresOwnedAndManaged() {
        List<Integer> storeIDs = new LinkedList<>();

        storeIDs.addAll(this.storesOwned);
        storeIDs.addAll(this.storesManaged.keySet());

        return new Response<>(storeIDs, false, "Get store owned Successfully");
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }
}
