package Server.Domain.UserManager;


public class Guest extends UserState {
    //private FuncEnum allowed;
    public Guest(){
        //allowed = {LOGIN, REGISTER}
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
