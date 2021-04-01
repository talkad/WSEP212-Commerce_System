package Server.Domain.UserManager;


import java.util.LinkedList;
import java.util.List;

public class Guest extends UserState {
    private final List<Permissions> allowedFunctions;
    public Guest(){
        this.allowedFunctions = new LinkedList<>();
        this.allowedFunctions.add(Permissions.REGISTER);
    }

    @Override
    public boolean allowed(Permissions func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(Permissions func, User user, int storeId) {
        return false;
    }
}
