package Domain.UserManager;

import java.util.Map;

public class User{

    private UserState state;
    private Map<String, Role> roles;

    public User(){
        this.state = new Guest();
        this.roles = null;
    }

    public User(String name){
        this.state = new Registered(name);
        //this.roles = state.roles
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

    public boolean login(String name, String password) {
        return this.state.login(name, password);
    }
    public boolean createStore(String storeName) {
        boolean result;
        result = this.state.createStore(storeName);
        if(result)
            this.roles.put(storeName, Role.STORE_OWNER);
        return result;
    }
}
