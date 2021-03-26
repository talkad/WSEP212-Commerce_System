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
        this.roles = null;
    }

    public User(String name){
        this.state = new Registered(name);
        // @TODO roles = loadfromdb
    }

    public void changeState(Role role){
        switch (role){
            case GUEST:
                state = new Guest();
                break;
//            case REGISTERED:
//                state = new Registered(); //@TODO Login info???
//                break;
        }
    }

    public UserState getState() {
        return state;
    }

    public boolean login(String name, String password) {
        return this.state.login(name, password);
    }
}
