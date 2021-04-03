package Server.Domain.UserManager;

public abstract class UserState {

    public abstract boolean allowed(Permissions func, User user);

    public abstract boolean allowed(Permissions func, User user, int storeId);

}

