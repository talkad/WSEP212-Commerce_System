package Domain.UserManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserDAO {

    private Map<String, String> registeredUsers;
    private Map<String, List<String>> testManagers;
    private Map<String, List<String>> testOwners;

    // private Map<String, Map<String, Role>> userRoles;


    private UserDAO(){
        this.registeredUsers = new ConcurrentHashMap<>();
    }

    public UserDTO getUser(String name){
        List<String> storesOwned = testOwners.get(name);
        if (storesOwned == null)
            storesOwned = new LinkedList<>();
        List<String> storesManaged = testManagers.get(name);
        if (storesManaged == null)
            storesManaged = new LinkedList<>();
        return new UserDTO(name, storesOwned, storesManaged);
    }

    private static class CreateSafeThreadSingleton {
        private static final UserDAO INSTANCE = new UserDAO();
    }

    public static UserDAO getInstance()
    {
        return UserDAO.CreateSafeThreadSingleton.INSTANCE;
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

//    public Map<String, Role> getRegisteredRoles(String name){
//        if(registeredUsers.containsKey(name)){
//            return userRoles.get(name);
//        }
//        //@TODO else(loadFromDB)
//        return new ConcurrentHashMap<>();
//    }
}
