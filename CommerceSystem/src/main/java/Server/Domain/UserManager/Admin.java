package Server.Domain.UserManager;

public class Admin extends Registered {
    public Admin() {
        super();
        this.allowedFunctions.add(PermissionsEnum.RECEIVE_GENERAL_HISTORY);
        this.allowedFunctions.add(PermissionsEnum.RECEIVE_GENERAL_REVENUE);
    }

    @Override
    public UserStateEnum getStateEnum(){
        return UserStateEnum.ADMIN;
    }
}
