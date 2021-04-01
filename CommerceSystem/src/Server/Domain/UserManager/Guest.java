package Server.Domain.UserManager;


import java.util.LinkedList;
import java.util.List;

public class Guest extends UserState {
    private final List<FunctionName> allowedFunctions;
    public Guest(){
        this.allowedFunctions = new LinkedList<>();
        this.allowedFunctions.add(FunctionName.REGISTER);
    }

    @Override
    public boolean allowed(FunctionName func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(FunctionName func, User user, int storeId) {
        return false;
    }
}
