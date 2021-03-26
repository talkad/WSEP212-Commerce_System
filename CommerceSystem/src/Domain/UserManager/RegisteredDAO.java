package Domain.UserManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisteredDAO {

    private Map<String, String> registeredUsers;

    private RegisteredDAO(){
        this.registeredUsers = new ConcurrentHashMap<>();
    }

    private static class CreateSafeThreadSingleton {
        private static final RegisteredDAO INSTANCE = new RegisteredDAO();
    }

    public static RegisteredDAO getInstance()
    {
        return RegisteredDAO.CreateSafeThreadSingleton.INSTANCE;
    }

    public void registerUser(String name, String password){
        registeredUsers.put(name, password);
    }

    public boolean isUniqueName(String name) {
        return !this.registeredUsers.containsKey(name);
    }

    public boolean validUser(String name, String password) {
        if(registeredUsers.get(name) != null)
            return registeredUsers.get(name).equals(password);
        return false;
    }
}
