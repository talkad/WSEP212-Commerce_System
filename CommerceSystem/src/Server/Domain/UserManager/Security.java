package Server.Domain.UserManager;

public class Security {

    private static class CreateSafeThreadSingleton {
        private static final Security INSTANCE = new Security();
    }

    public static Security getInstance() {
        return Security.CreateSafeThreadSingleton.INSTANCE;
    }


    public String getHashCode(String plainText){
        byte[] bytes = plainText.getBytes();


        return Integer.toString(plainText.hashCode());
    }
}