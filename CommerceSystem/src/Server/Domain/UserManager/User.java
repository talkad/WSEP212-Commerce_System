package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Review;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {

    private UserState state;
    private List<Integer> storesOwned;
    private Map<Integer, List<Permissions>> storesManaged;
    private String name;
    private ShoppingCart shoppingCart;
    private PurchaseHistory purchaseHistory;
    private Appointment appointments;

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

        this.storesOwned = null;
        this.storesManaged = null;
        this.shoppingCart = new ShoppingCart();
        this.purchaseHistory = null;
        this.appointments = null;
    }

    public User(UserDTO userDTO) {
        ownedLock = new ReentrantReadWriteLock();
        ownedWriteLock = ownedLock.writeLock();
        ownedReadLock = ownedLock.readLock();

        managedLock = new ReentrantReadWriteLock();
        managedWriteLock = managedLock.writeLock();
        managedReadLock = managedLock.readLock();

        if (UserDAO.getInstance().isAdmin(userDTO.getName())) {
            this.state = new Admin();
        } else {
            this.state = new Registered();
        }
        this.storesOwned = userDTO.getStoresOwned();
        this.storesManaged = userDTO.getStoresManaged();
        this.name = userDTO.getName();
        this.shoppingCart = userDTO.getShoppingCart();
        this.purchaseHistory = userDTO.getPurchaseHistory();
        this.appointments = userDTO.getAppointments();
    }

    public List<Integer> getStoresOwned() {
        return storesOwned;
    }

    public void addStoresOwned(int storeId) {
        ownedWriteLock.lock();
        this.storesOwned.add(storeId);
        ownedWriteLock.unlock();
    }

    public Map<Integer, List<Permissions>> getStoresManaged() {
        return storesManaged;
    }

    public void addStoresManaged(int storeId, List<Permissions> permission) {
        managedWriteLock.lock();
        this.storesManaged.put(storeId, permission);
        managedWriteLock.unlock();
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

    public Response<Boolean> register() {
        if (state.allowed(Permissions.REGISTER, this)) {
            return new Response<>(true, false, "User is allowed to register");
        }
        return new Response<>(false, true, "User is not allowed to register");
    }

//    public boolean login(String name, String password){
//        //@TODO what is the purpose of this function?
//    }

    public Response<Boolean> addToCart(int storeID, int productID) {
        return this.shoppingCart.addProduct(storeID, productID);
    }

    public Response<Map<Integer, Map<ProductDTO, Integer>>> getShoppingCartContents() {
        return new Response<>(this.shoppingCart.getBaskets(), false, null);
    }

    public Response<Boolean> removeProduct(int storeID, int productID) {
        return this.shoppingCart.removeProduct(storeID, productID);
    }

    public Response<Boolean> logout() {
        return new Response<>(true, !this.state.allowed(Permissions.LOGOUT, this), "Cannot logout without being logged in");
    }

    public Response<Integer> openStore(String storeName) {
        Response<Integer> result;
        if (!this.state.allowed(Permissions.OPEN_STORE, this)) {
            return new Response<>(-1, true, "Not allowed to open store");
        }

        result = StoreController.getInstance().openStore(storeName, this.name);
        if (!result.isFailure()) {
            ownedWriteLock.lock();
            this.storesOwned.add(result.getResult());
            ownedWriteLock.unlock();
        }
        return result;
    }

    public Response<List<PurchaseDTO>> getPurchaseHistoryContents() {
        if (this.state.allowed(Permissions.GET_PURCHASE_HISTORY, this)) {
            return new Response<>(this.purchaseHistory.getPurchases(), false, null);
        }
        return new Response<>(null, true, "User not allowed to view purchase history");
    }

    public Response<Boolean> updateProductQuantity(int storeID, int productID, int amount) {
        return this.shoppingCart.updateProductQuantity(storeID, productID, amount);
    }

    public Response<Boolean> addProductReview(int storeID, int productID, String reviewStr) {
        Store store;
        Response<Review> reviewRes = Review.createReview(name, reviewStr);

        if(reviewRes.isFailure())
            return new Response<>(false, true, reviewRes.getErrMsg());

        if (this.state.allowed(Permissions.REVIEW_PRODUCT, this)) {

            if (purchaseHistory.isPurchased(productID)) {
                store = StoreController.getInstance().getStoreById(storeID);

                if(store == null){
                    return new Response<>(false, true, "This store doesn't exists");
                }

                return store.addProductReview(productID, reviewRes.getResult());
            }

            return new Response<>(false, true, "The given product wasn't purchased before");
        } else {
            return new Response<>(false, true, "User not allowed to review product");
        }
    }

    public Response<Boolean> addProductsToStore(ProductDTO productDTO, int amount) {
        Store store;

        if (this.state.allowed(Permissions.ADD_PRODUCT_TO_STORE, this, productDTO.getStoreID())) {
            store = StoreController.getInstance().getStoreById(productDTO.getStoreID());

            if(store == null){
                return new Response<>(false, true, "This store doesn't exists");
            }

            return store.addProduct(productDTO, amount);
        }
        return new Response<>(false, true, "The user is not allowed to add products to the store");
    }

    public Response<Boolean> removeProductsFromStore(int storeID, int productID, int amount) {
        Store store;

        if (this.state.allowed(Permissions.REMOVE_PRODUCT_FROM_STORE, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);

            if(store == null){
                return new Response<>(false, true, "This store doesn't exists");
            }

            return store.removeProduct(productID, amount);
        }
        return new Response<>(false, true, "The user is not allowed to remove products from the store");
    }

    public Response<Boolean> updateProductInfo(int storeID, int productID, double newPrice, String newName) {
        if (this.state.allowed(Permissions.UPDATE_PRODUCT_PRICE, this, storeID)) {
            return StoreController.getInstance().updateProductInfo(storeID, productID, newPrice, newName);
        }
        return new Response<>(false, true, "The user is not allowed to edit products information in the store");
    }

    public Response<Boolean> appointOwner(String newOwner, int storeId) {
        if (this.state.allowed(Permissions.APPOINT_OWNER, this, storeId)) {
            Response<Boolean> exists = UserDAO.getInstance().userExists(newOwner);
            if (exists.getResult()) {
                if (!UserDAO.getInstance().ownedOrManaged(storeId, newOwner)) {
                    this.appointments.addAppointment(storeId, newOwner);
                    UserDAO.getInstance().addAppointment(this.name, storeId, newOwner);
                    return UserDAO.getInstance().addStoreOwned(newOwner, storeId);
                }
                return new Response<>(false, true, "User was already appointed in this store");
            } else {
                return new Response<>(false, true, "User does not exist in the system");
            }
        }
        return new Response<>(false, true, "User isn't allowed to appoint owner");
    }

    public Response<Boolean> appointManager(String newManager, int storeId) {
        if (this.state.allowed(Permissions.APPOINT_MANAGER, this, storeId)) {
            Response<Boolean> exists = UserDAO.getInstance().userExists(newManager);
            if (exists.getResult()) {
                if (!UserDAO.getInstance().ownedOrManaged(storeId, newManager)) {
                    this.appointments.addAppointment(storeId, newManager);
                    UserDAO.getInstance().addAppointment(this.name, storeId, newManager);
                    return UserDAO.getInstance().addStoreManaged(newManager, storeId);
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
            return this.appointments.removeAppointment(storeID, appointeeName);
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

    public Response<Boolean> appointedAndAllowed(int storeId, String appointeeName, Permissions permission) {
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

    public Response<Boolean> addPermission(int storeId, String permitted, Permissions permission) {     // req 4.6
        if (this.state.allowed(Permissions.EDIT_PERMISSION, this, storeId) && this.appointments.contains(storeId, permitted)) {
            if(!UserDAO.getInstance().getUser(permitted).getStoresManaged().get(storeId).contains(permission)) {
                UserDAO.getInstance().addPermission(storeId, permitted, permission);
                return new Response<>(true, false, "Added permission");
            }
            else return new Response<>(false, true, "The user already has the permission");
        } else {
            return new Response<>(false, true, "User not allowed to add permissions to this user");
        }
    }

    public Response<Boolean> removePermission(int storeId, String permitted, Permissions permission) {     // req 4.6
        if (this.state.allowed(Permissions.EDIT_PERMISSION, this, storeId) && this.appointments.contains(storeId, permitted)) {
            UserDAO.getInstance().removePermission(storeId, permitted, permission);
            return new Response<>(true, false, "Removed permission");
        } else {
            return new Response<>(false, true, "User not allowed to remove permissions from this user");
        }
    }

    public void addSelfPermission(int storeId, Permissions permission) {
        managedWriteLock.lock();
        this.storesManaged.get(storeId).add(permission);
        managedWriteLock.unlock();
    }

    public void removeSelfPermission(int storeId, Permissions permission) {
        managedWriteLock.lock();
        this.storesManaged.get(storeId).remove(permission);
        managedWriteLock.unlock();
    }

    public Response<List<PurchaseDTO>> getUserPurchaseHistory(String username) {       // req 6.4
        if (this.state.allowed(Permissions.RECEIVE_GENERAL_HISTORY, this)) {
            if (UserDAO.getInstance().userExists(username).getResult()) {
                return new Response<>(UserDAO.getInstance().getUser(username).getPurchaseHistory().getPurchases(), false, "no error");//todo combine dto pull
            } else {
                return new Response<>(new Vector<>(), true, "User does not exist");//todo empty list or null
            }
        } else {
            return new Response<>(new Vector<>(), true, "User not allowed to view user's purchase");//todo empty list or null
        }
    }

    public Response<Collection<PurchaseDTO>> getStorePurchaseHistory(int storeID) {
        Store store;

        if (this.state.allowed(Permissions.RECEIVE_GENERAL_HISTORY, this)) {
            store = StoreController.getInstance().getStoreById(storeID);

            if(store == null){
                return new Response<>(null, true, "This store doesn't exists");
            }

            return store.getPurchaseHistory();
        }
        return new Response<>(new LinkedList<>(), true, "User not allowed to view user's purchase");//todo empty list or null
    }

    public Response<Boolean> getStoreWorkersDetails(int storeID) {       // req 4.9
        return new Response<>(true, !this.state.allowed(Permissions.RECEIVE_STORE_WORKER_INFO, this, storeID), "User not allowed to receive store workers information");
    }

    public Response<Collection<PurchaseDTO>> getPurchaseDetails(int storeID) {     // req 4.11
        Store store;

        if (this.state.allowed(Permissions.RECEIVE_STORE_HISTORY, this, storeID)) {
            store = StoreController.getInstance().getStoreById(storeID);

            if(store == null){
                return new Response<>(null, true, "This store doesn't exists");
            }

            return store.getPurchaseHistory();
        } else {
            return new Response<>(null, true, "User not allowed to receive store history");
        }
    }

    public void addToPurchaseHistory(List<PurchaseDTO> result) {
        if (this.purchaseHistory != null){
            purchaseHistory.addPurchase(result);
        }
    }

    public void emptyCart(){
        this.shoppingCart = new ShoppingCart();
    }
}
