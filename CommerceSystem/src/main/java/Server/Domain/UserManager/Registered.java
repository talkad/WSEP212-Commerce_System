package Server.Domain.UserManager;

import java.util.List;
import java.util.Vector;

public class Registered extends UserState {

    protected final List<PermissionsEnum> allowedFunctions;

    public Registered() {
        this.allowedFunctions = new Vector<>();
        this.allowedFunctions.add(PermissionsEnum.LOGOUT);
        this.allowedFunctions.add(PermissionsEnum.OPEN_STORE);
        this.allowedFunctions.add(PermissionsEnum.REVIEW_PRODUCT);
        this.allowedFunctions.add(PermissionsEnum.GET_PURCHASE_HISTORY);
    }

    @Override
    public boolean allowed(PermissionsEnum func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(PermissionsEnum permission, User user, int storeId) {
        if (user.getStoresOwned().contains(storeId)) {
            return true;
        }
        else if(user.getStoresManaged().containsKey(storeId)){
            return user.getStoresManaged().get(storeId).contains(permission);
        }
        else return false;
    }

    @Override
    public UserStateEnum getStateEnum(){
        return UserStateEnum.REGISTERED;
    }
}
