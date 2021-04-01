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
    public boolean allowed(FunctionName func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(FunctionName func, User user, int storeId) {
        if (user.getStoresOwned().contains(storeId)) {
            return true;
        }
        else if(user.getStoresManaged().get(storeId).contains(func)) { // @TODO func or permission?
            return true;
        }
        return false;
    }

}
