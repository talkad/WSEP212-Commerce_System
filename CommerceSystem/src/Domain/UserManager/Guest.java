package Domain.UserManager;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Guest extends UserState {
    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    public Guest() {
        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    public boolean register(String name, String password) {
        boolean result = false;
        readLock.lock();
        if(isUniqueName(name)) {
            registerUser(name, password);
            result = true;
        }
        readLock.unlock();
        return result;
    }

    public boolean login(String name, String password){
        return RegisteredDAO.getInstance().validUser(name, password);
    }

    private boolean isUniqueName(String name){
        return RegisteredDAO.getInstance().isUniqueName(name); //@TODO database read function
    }

    private void registerUser(String name, String password){
        RegisteredDAO.getInstance().registerUser(name, password); //@TODO database write function
    }

    public boolean loggedIn(){
        return false;
    }

    public boolean createStore(String storeName) {
        return false;
    }
}
