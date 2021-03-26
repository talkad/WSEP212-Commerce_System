package Domain.UserManager;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserController {
    private AtomicInteger availableId;
    private Map<String, User> connectedUsers;
    private UserController(){
        this.availableId = new AtomicInteger(1);
        this.connectedUsers = new ConcurrentHashMap<>();
    }
    private static class CreateSafeThreadSingleton {
        private static final UserController INSTANCE = new UserController();
    }

    public static UserController getInstance() {
        return UserController.CreateSafeThreadSingleton.INSTANCE;
    }

    private String addGuest(){
        String guestName = "Guest" + availableId.getAndIncrement();
        connectedUsers.put(guestName, new User());
        return guestName;
    }

    private void login(String name, String password, String prevName){
        if(connectedUsers.get(name).login(name, password)){
            connectedUsers.remove(prevName);
            connectedUsers.put(name, new User(name)); //@TODO what is user?
        }
        else {
            //@TODO ERROR MSG FAILED TO LOGIN
        }
    }

    public String logout(String name){
        connectedUsers.remove(name);
        return addGuest();
    }
}

