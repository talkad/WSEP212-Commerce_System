package Server.Domain.UserManager;

public abstract class UserState {

//    public abstract boolean register(String name, String password);

//    public abstract boolean login(String name, String password);

//    public abstract boolean loggedIn();

//    public abstract boolean createStore(String storeName);

    public boolean allowed(FunctionName func, String userName){
        return true;
    }

    }

