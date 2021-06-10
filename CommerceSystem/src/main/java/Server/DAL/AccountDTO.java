package Server.DAL;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Entity(value = "accounts")
@BsonDiscriminator("AccountDTO")
public class AccountDTO {

    @Id
    @Property(value = "username")
    private String username;

    @Property(value = "password")
    private String password;

    public AccountDTO(){
        // For Morphia
    }

    public AccountDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
