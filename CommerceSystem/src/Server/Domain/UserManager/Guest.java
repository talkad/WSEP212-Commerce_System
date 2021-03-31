package Server.Domain.UserManager;


import java.util.LinkedList;
import java.util.List;

public class Guest extends UserState {
    private List<FunctionName> allowedFunctions;
    public Guest(){
        this.allowedFunctions = new LinkedList<>();
        this.allowedFunctions.add(FunctionName.REGISTER);
    }

    @Override
    public boolean allowed(FunctionName func, String userName) {
        return this.allowedFunctions.contains(func);
    }

    //    private ReadWriteLock lock;
//    private Lock writeLock;
//    private Lock readLock;

//    public Guest() {
//        lock = new ReentrantReadWriteLock();
//        writeLock = lock.writeLock();
//        readLock = lock.readLock();
//    }

//    public boolean register(String name, String password) {
//        boolean result = false;
//        readLock.lock();
//        if(isUniqueName(name)) {
//            registerUser(name, password);
//            result = true;
//        }
//        readLock.unlock();
//        return result;
//    }

//    public boolean login(String name, String password){
//        return UserDAO.getInstance().validUser(name, password);
//    }

//    private boolean isUniqueName(String name){
//        return UserDAO.getInstance().isUniqueName(name); //@TODO database read function
//    }

    private void registerUser(String name, String password){
        UserDAO.getInstance().registerUser(name, password); //@TODO database write function
    }

    public boolean loggedIn(){
        return false;
    }

//    public boolean createStore(String storeName) {
//        return false;
//    }
}
