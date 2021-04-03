package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.StoreController;

import java.util.LinkedList;
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
        if(UserDAO.getInstance().isAdmin(userDTO.getName())){
            this.state = new Admin();
        }
        else {
            this.state = new Registered();
        }
        this.storesOwned = userDTO.getStoresOwned();
        this.storesManaged = userDTO.getStoresManaged();
        this.name = userDTO.getName();
        this.shoppingCart = userDTO.getShoppingCart();
        this.purchaseHistory = userDTO.getPurchaseHistory();
        this.appointments = new Appointment();
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

    public Response<Boolean> register() {
        if(state.allowed(Permissions.REGISTER, this)){
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

    public Map<Integer ,Map<ProductDTO, Integer>> getShoppingCartContents() {
        return this.shoppingCart.getBaskets(); //@TODO SHOULD PROBABLY CHECK
    }

    public Response<Boolean> removeProduct(int storeID, int productID) {
        return this.shoppingCart.removeProduct(storeID, productID); //@TODO SHOULD PROBABLY CHECK
    }

//    public void logout() {
//        //@TODO final actions before logout
//    }

    public Response<Integer> openStore(String storeName) {
        Response<Integer> result;
        if (!this.state.allowed(Permissions.OPEN_STORE, this)) {
            return new Response<>(-1, true, "Not allowed to open store");
        }

        result = StoreController.getInstance().openStore(storeName, this.name);
        if(!result.isFailure()) {
            this.storesOwned.add(result.getResult());
        }
        return result;
    }

    public List<Purchase> getPurchaseHistoryContents() {
        return this.purchaseHistory.getPurchases();
    }

    public Response<Boolean> updateProductQuantity(int storeID, int productID, int amount) {
        return this.shoppingCart.updateProductQuantity(storeID, productID, amount);
    }

    public Response<Boolean> addProductReview(int productID, String review) {
        if(this.state.allowed(Permissions.REVIEW_PRODUCT,this)){
            // @TODO purchaseHistory.getPurchases().contains(productID) then add product
            return new Response<>(false, true, review); // @TODO THIS IS BAD FIX IT GODAMNIT
        }
        else{
            return new Response<>(false, true, "User not allowed to review product");
        }
    }

    public Response<Boolean> addProductsToStore(int storeID, ProductDTO productDTO, int amount) {
        if(this.state.allowed(Permissions.ADD_PRODUCT_TO_STORE, this, storeID)){
            return StoreController.getInstance().addProductToStore(storeID, productDTO, amount);
        }
        return new Response<>(false, true, "The user is not allowed to add products to the store");
    }

    public Response<Boolean> removeProductsFromStore(int storeID, int productID, int amount) {
        if(this.state.allowed(Permissions.REMOVE_PRODUCT_FROM_STORE, this, storeID)){
            return StoreController.getInstance().removeProductFromStore(storeID, productID, amount);
        }
        return new Response<>(false, true, "The user is not allowed to remove products from the store");
    }

    public Response<Boolean> updateProductInfo(int storeID, int productID, double newPrice, String newName) {
        if(this.state.allowed(Permissions.UPDATE_PRODUCT_PRICE, this, storeID)){
            return StoreController.getInstance().updateProductInfo(storeID, productID, newPrice, newName);
        }
        return new Response<>(false, true, "The user is not allowed to edit products information in the store");
    }

    public Response<Boolean> appointOwner(String newOwner, int storeId){
        if(this.state.allowed(Permissions.APPOINT_OWNER, this, storeId)){
            Response<Boolean> exists = UserDAO.getInstance().userExists(newOwner);
            if(exists.getResult()) {
                this.appointments.addAppointment(storeId, newOwner);
                UserDAO.getInstance().addAppointment(this.name, storeId, newOwner);
                return UserDAO.getInstance().addStoreOwned(newOwner, storeId);
            }
        }
        return new Response<>(false, true, "User isn't allowed to appoint owner");
    }

    public Response<Boolean> appointManager(String newManager, int storeId) {
        if(this.state.allowed(Permissions.APPOINT_MANAGER, this, storeId)){
            Response<Boolean> exists = UserDAO.getInstance().userExists(newManager);
            if(exists.getResult()) {
                this.appointments.addAppointment(storeId, newManager);
                UserDAO.getInstance().addAppointment(this.name, storeId, newManager);
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

    public Response<String> removeAppointment(String appointeeName, int storeID) {
        if(this.storesOwned.contains(storeID)){
            return this.appointments.removeAppointment(storeID, appointeeName);
        }
        else if(this.storesManaged.containsKey(storeID)){
            return this.appointments.removeAppointment(storeID, appointeeName);
        }
        return new Response<>("problem", true, "user not appointed by this appointer");
    }

    public void removeRole(int storeID) {
        if(this.storesOwned.contains(storeID)){
            this.storesOwned.remove(storeID);
        }
        else if(this.storesManaged.containsKey(storeID)){
            this.storesManaged.remove(storeID);
        }
    }

    public boolean appointed(int storeId, String appointeeName) {
        return this.appointments.contains(storeId, appointeeName);
    }

    public Response<Boolean> appointedAndAllowed(int storeId, String appointeeName, Permissions permission) {
        if(this.state.allowed(permission, this, storeId)){
            if(this.appointments.contains(storeId, appointeeName)){
                return new Response<>(true, false, "success");
            }
            else {
                return new Response<>(false, true, "Attempted to remove a user that wasn't appointed by him");
            }
        }
        else {
            return new Response<>(false, true, "User not allowed to " + permission.name());
        }
    }

    public Response<Boolean> addPermission(int storeId, String permitted, Permissions permission) {
        if(this.state.allowed(Permissions.ADD_PERMISSION, this, storeId) && this.appointments.contains(storeId, permitted)){
            UserDAO.getInstance().addPermission(storeId, permitted, permission);
            return new Response<>(true, false, "Added permission");
        }
        else{
            return new Response<>(false, true, "User not allowed to add permissions to this user");
        }
    }

    public void addSelfPermission(int storeId, Permissions permission){
        this.storesManaged.get(storeId).add(permission);
    }

    public Response<List<Purchase>> getUserPurchaseHistory(String username) {
        if(this.state.allowed(Permissions.RECEIVE_GENERAL_HISTORY, this)){
            if(UserDAO.getInstance().userExists(username).getResult()) {
                return new Response<>(UserDAO.getInstance().getUser(username).getPurchaseHistory().getPurchases(), false, "no error");//todo combine dto pull
            }
            else{
                return new Response<>(new LinkedList<>(), true, "User does not exist");//todo empty list or null
            }
        }
        else{
            return new Response<>(new LinkedList<>(), true, "User not allowed to view user's purchase");//todo empty list or null
        }
    }
}
