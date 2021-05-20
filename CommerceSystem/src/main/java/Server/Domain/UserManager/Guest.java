package Server.Domain.UserManager;


import java.util.List;
import java.util.Vector;

public class Guest extends UserState {
    private final List<PermissionsEnum> allowedFunctions;
    public Guest(){
        this.allowedFunctions = new Vector<>();
        this.allowedFunctions.add(PermissionsEnum.REGISTER);
    }

    @Override
    public boolean allowed(PermissionsEnum func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(PermissionsEnum func, User user, int storeId) {
        return false;
    }

    @Override
    public UserStateEnum getStateEnum(){
        return UserStateEnum.GUEST;
    }
}
