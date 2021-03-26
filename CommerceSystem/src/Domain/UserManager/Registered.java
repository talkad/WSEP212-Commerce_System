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


}
