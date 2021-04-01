package Server.Domain.UserManager;


import java.util.LinkedList;
import java.util.List;

public class Registered extends UserState {

    private List<FunctionName> allowedFunctions;

    public Registered() {
        this.allowedFunctions = new LinkedList<>();
        this.allowedFunctions.add(FunctionName.OPEN_STORE);
    }

    @Override
    public boolean allowed(FunctionName func, String userName) {
        return this.allowedFunctions.contains(func);
    }

//    @Override
//    public boolean register(String name, String password) {
//        return false;
//    }

//    @Override
//    public boolean login(String name, String password) {
//        return false;
//    }

//    @Override
//    public boolean loggedIn() {
//        return true;
//    }

//    public void logout() { //@TODO
//
//    }

//    @Override
//    public boolean createStore(String storeName) {
//        return CommerceSystem.createStore(storeName);        //TODO add implementation
//    }
}
