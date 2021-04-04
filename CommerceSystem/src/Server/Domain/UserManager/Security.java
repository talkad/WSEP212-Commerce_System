package Server.Domain.UserManager;

public class Security {
    public String getHashCode(String str){
        return Integer.toString(str.hashCode());
    }
}