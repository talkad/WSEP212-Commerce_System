package Server.Domain.UserManager;

import java.util.List;
import java.util.Vector;

public class Registered extends UserState {

    protected final List<Permissions> allowedFunctions;

    public Registered() {
        this.allowedFunctions = new Vector<>();
        this.allowedFunctions.add(Permissions.LOGOUT);
        this.allowedFunctions.add(Permissions.OPEN_STORE);
        this.allowedFunctions.add(Permissions.REVIEW_PRODUCT);
        this.allowedFunctions.add(Permissions.GET_PURCHASE_HISTORY);
    }

    @Override
    public boolean allowed(Permissions func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(Permissions permission, User user, int storeId) {
        if (user.getStoresOwned().contains(storeId)) {
            return true;
        }
        else if(user.getStoresManaged().containsKey(storeId)){
            return user.getStoresManaged().get(storeId).contains(permission);
        }
        else return false;
    }
}
