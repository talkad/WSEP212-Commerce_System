package Domain.UserManager;

import java.util.Map;

public class User{
    enum Role{
        GUEST,
        REGISTERED,
        STORE_MANAGER,
        STORE_OWNER,
        ADMIN
    }
    private UserState state;
    private Map<String, Role> roles;

    public User(){
        this.state = new Guest();
    }

    public void changeState(Role role){
        switch (role){
            case GUEST:
                state = new Guest();
                break;

        }
    }

    public boolean createStore(String storeName) {
        boolean result;
        result = this.state.createStore(storeName);
        if(result)
            this.roles.put(storeName, Role.STORE_OWNER);
        return result;
    }
}
