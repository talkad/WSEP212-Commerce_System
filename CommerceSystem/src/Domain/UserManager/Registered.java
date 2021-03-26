package Domain.UserManager;

public class Registered extends UserState {

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

    public void logout() {

    }

    @Override
    public boolean createStore(String storeName) {
        return CommerceSystem.createStore(storeName);        //TODO add implementation
    }
}
