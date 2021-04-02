package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.StoreController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User{

    private UserState state;
    private List<Integer> storesOwned;
    private Map<Integer, List<Permissions>> storesManaged;
    private String name;
    private ShoppingCart shoppingCart;
    private PurchaseHistory purchaseHistory;
    private Appointment appointments;

    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    public User(){
        this.state = new Guest();

        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();

        this.storesOwned = null;
        this.storesManaged = null;
        this.shoppingCart = new ShoppingCart();
        this.purchaseHistory = null;
        this.appointments = null;
    }

    public User(UserDTO userDTO){
        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();

        this.state = new Registered();
        this.storesOwned = userDTO.getStoresOwned();
        this.storesManaged = userDTO.getStoresManaged();
        this.name = userDTO.getName();
        this.shoppingCart = userDTO.getShoppingCart();
        this.purchaseHistory = userDTO.getPurchaseHistory();
        this.appointments = new Appointment();
        // @TODO roles = loadfromdb
    }

    public List<Integer> getStoresOwned() {
        return storesOwned;
    }

    public void addStoresOwned(int storeId) {
        this.storesOwned.add(storeId);
    }

    public Map<Integer, List<Permissions>> getStoresManaged() {
        return storesManaged;
    }

    public void addStoresManaged(int storeId, List<Permissions> permission) {
        this.storesManaged.put(storeId, permission);
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

    //    public void changeState(FunctionName role){
//        switch (role){
//            case GUEST:
//                state = new Guest();
//                break;
////            case REGISTERED:
////                state = new Registered(); //@TODO Login info???
////                break;
//        }
//    }

    public Response<Boolean> register() {
        if(state.allowed(Permissions.REGISTER, this)){
            return new Response<>(true, false, "User is allowed to register");
        }
        return new Response<>(false, true, "User is not allowed to register");
    }

//    public boolean login(String name, String password){
//        //@TODO what is the purpose of this function?
//    }

    public Response<Boolean> addToCart(Product product) {
        return this.shoppingCart.addProduct(product);
    }

    public Map<Integer ,Map<Product, Integer>> getShoppingCartContents() {
        return this.shoppingCart.getBaskets(); //@TODO SHOULD PROBABLY CHECK
    }

    public Response<Boolean> removeProduct(Product product) {
        return this.shoppingCart.removeProduct(product); //@TODO SHOULD PROBABLY CHECK
    }

    public void logout() {
        //@TODO final actions before logout
    }

    public Response<Integer> openStore(String storeName) {
        Response<Integer> result;
        if (!this.state.allowed(Permissions.OPEN_STORE, this)) {
            return new Response<>(-1, true, "Not allowed to open store");
        }

        result = StoreController.getInstance().openStore(storeName);
        if(!result.isFailure()) {
            this.storesOwned.add(result.getResult());
        }
        return result;
    }

    public List<Purchase> getPurchaseHistoryContents() {
        return this.purchaseHistory.getPurchases();
    }

    public Response<Boolean> updateProductQuantity(Product product, int amount) {
        return this.shoppingCart.updateProductQuantity(product, amount); // @TODO IMPLEMENT IN SHOPPINGCART TAL KADOSH
    }

    public Response<Boolean> addProductReview(int productID, String review) {
        //todo add allowed
        // @TODO purchaseHistory.getPurchases().contains(productID) then add product
        return new Response<>(false, true, review); // @TODO THIS IS BAD FIX IT GODAMNIT
    }

    public Response<Boolean> addProductsToStore(int storeID, Product product, int amount) {
        if(this.state.allowed(Permissions.ADD_PRODUCT_TO_STORE, this, storeID)){
            return StoreController.getInstance().addProductToStore(storeID, product, amount);
        }
        return new Response<>(false, true, "The user is not allowed to add products to the store");
    }

    public Response<Boolean> removeProductsFromStore(int storeID, Product product, int amount) {
        if(this.state.allowed(Permissions.REMOVE_PRODUCT_FROM_STORE, this, storeID)){
            return StoreController.getInstance().removeProductFromStore(storeID, product, amount);
        }
        return new Response<>(false, true, "The user is not allowed to remove products from the store");
    }

    public Response<Boolean> updateProductPrice(int storeID, int productID, int newPrice) {
        if(this.state.allowed(Permissions.UPDATE_PRODUCT_PRICE, this, storeID)){
            return StoreController.getInstance().updateProductPrice(storeID, productID, newPrice); //TODO ON SHOP SIDE
        }
        return new Response<>(false, true, "The user is not allowed to edit products information in the store");
    }

    public Response<Boolean> appointOwner(String newOwner, int storeId){
        if(this.state.allowed(Permissions.APPOINT_OWNER, this, storeId)){
            Response<Boolean> exists = UserDAO.getInstance().userExists(newOwner);
            if(!exists.isFailure()) {
                return UserDAO.getInstance().addStoreOwned(newOwner, storeId);
            }
        }
        return new Response<>(false, true, "User isn't allowed to appoint owner");
    }


    public Response<Boolean> appointManager(String newManager, int storeId) {
        if(this.state.allowed(Permissions.APPOINT_MANAGER, this, storeId)){
            Response<Boolean> exists = UserDAO.getInstance().userExists(newManager);
            if(!exists.isFailure()) {
                return UserDAO.getInstance().addStoreManaged(newManager, storeId);
            }
        }
        return new Response<>(false, true, "User isn't allowed to appoint manager");
    }

    public Response<String> removeOwnerAppointment(String appointeeName, int storeId) {
        return this.appointments.removeAppointment(storeId, appointeeName);
    }

    public Response<String> removeManagerAppointment(String appointeeName, int storeId) {
        return this.appointments.removeAppointment(storeId, appointeeName);
    }

    public boolean isOwner(int storeId){
        return this.storesOwned.contains(storeId);
    }

    public boolean isManager(int storeId){
        return this.storesManaged.containsKey(storeId);
    }
}
