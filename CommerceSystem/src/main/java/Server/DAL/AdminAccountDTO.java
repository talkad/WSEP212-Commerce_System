package Server.DAL;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

@Entity(value = "adminAccounts")
public class AdminAccountDTO {

    @Id
    @Property(value = "username")
    private String username;

    public AdminAccountDTO(){
        // For Morphia
    }

    public AdminAccountDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
