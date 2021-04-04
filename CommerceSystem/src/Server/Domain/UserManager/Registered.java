package Server.Domain.UserManager;


import Server.Domain.CommonClasses.Response;

import java.util.LinkedList;
import java.util.List;

public class Registered extends UserState {

    protected final List<Permissions> allowedFunctions;

    public Registered() {
        this.allowedFunctions = new LinkedList<>();
        this.allowedFunctions.add(Permissions.LOGOUT);
        this.allowedFunctions.add(Permissions.OPEN_STORE);
        this.allowedFunctions.add(Permissions.REVIEW_PRODUCT);
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
        else return user.getStoresManaged().get(storeId).contains(permission);
    }
}
