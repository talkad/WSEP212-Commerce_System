package Server.Domain.UserManager;

public abstract class UserState {

    public abstract boolean allowed(FunctionName func, User user);

    public abstract boolean allowed(FunctionName func, User user, int storeId);

}

