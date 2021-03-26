package Domain.UserManager;

public class Registered extends UserState {

    public Registered(String name) {
        super();
    }

    @Override
    public boolean register(String name, String password) {
        return false;
    }

    @Override
    public boolean login(String name, String password) {
        return false;
    }

    @Override
    public boolean loggedIn() {
        return true;
    }

    public void logout() { //@TODO

    }
}
