package Server.Domain.UserManager;

public abstract class UserState {

    public abstract boolean allowed(PermissionsEnum func, User user);

    public abstract boolean allowed(PermissionsEnum func, User user, int storeId);

    public abstract UserStateEnum getStateEnum();

}

