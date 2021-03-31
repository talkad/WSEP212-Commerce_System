package Domain.UserManager;

import Domain.ShoppingManager.StoreController;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User{

    private UserState state;
    private List<String> storesOwned;
    private List<String> storesManaged;
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
    }

    public User(String name){
        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();

        UserDTO userDTO = UserDAO.getInstance().getUser(name);
        this.storesOwned = userDTO.getStoresOwned();
        this.storesManaged = userDTO.getStoresManaged();
        // @TODO roles = loadfromdb
    }

    public void changeState(Role role){
        switch (role){
            case GUEST:
                state = new Guest();
                break;
//            case REGISTERED:
//                state = new Registered(); //@TODO Login info???
//                break;
        }
    }

    public boolean register(String name, String password) {
        boolean result = false;
        readLock.lock();
        if(UserDAO.getInstance().isUniqueName(name)) {
            UserDAO.getInstance().registerUser(name, password);
            result = true;
        }
        readLock.unlock();
        return result;
    }

    public boolean login(String name, String password){
        return UserDAO.getInstance().validUser(name, password);
    }

    public boolean createStore(String storeName) {
        boolean result;
        if (!this.state.allowed(CREATESTORE))
            return false;
        result = StoreController.createStore(storeName);
        if(result)
            this.storesOwned.add(storeName);
        return result;
    }
}
