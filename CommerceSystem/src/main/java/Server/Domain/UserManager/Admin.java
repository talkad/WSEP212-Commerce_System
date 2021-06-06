package Server.Domain.UserManager;

public class Admin extends Registered {
    public Admin() {
        super();
        this.allowedFunctions.add(PermissionsEnum.RECEIVE_GENERAL_HISTORY);
        this.allowedFunctions.add(PermissionsEnum.RECEIVE_GENERAL_REVENUE);
        this.allowedFunctions.add(PermissionsEnum.DAILY_VISITOR_STATISTICS);
    }

    @Override
    public UserStateEnum getStateEnum(){
        return UserStateEnum.ADMIN;
    }
}
