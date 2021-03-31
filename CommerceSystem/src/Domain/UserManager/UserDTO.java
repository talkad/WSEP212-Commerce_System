package Domain.UserManager;

import java.util.List;

public class UserDTO {
    private String name;
    private String password;
    private List<String> storesManaged;
    private List<String> storesOwned;

    public UserDTO(String name, String password, List<String> storesManaged, List<String> storesOwned) {
        this.name = name;
        this.password = password;
        this.storesManaged = storesManaged;
        this.storesOwned = storesOwned;
    }

    public UserDTO(String name, List<String> storesManaged, List<String> storesOwned) {
        this.name = name;
        this.storesManaged = storesManaged;
        this.storesOwned = storesOwned;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getStoresManaged() {
        return storesManaged;
    }

    public List<String> getStoresOwned() {
        return storesOwned;
    }
}
