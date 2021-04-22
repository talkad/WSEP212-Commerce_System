package Server.Domain.UserManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {

    private static class CreateSafeThreadSingleton {
        private static final Security INSTANCE = new Security();
    }

    public static Security getInstance() {
        return Security.CreateSafeThreadSingleton.INSTANCE;
    }


    // Copyright - https://techexpertise.medium.com/java-cryptographic-hash-functions-a7ae28f3fa42
    public String sha256(String plainText){
        StringBuilder hashText;

        try {
            //MessageDigest classes Static getInstance method is called with MD5 hashing
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");

            //digest() method is called to calculate message digest of the input
            //digest() return array of byte.
            byte[] inputDigest = msgDigest.digest(plainText.getBytes());

            // Convert byte array into signum representation
            // BigInteger class is used, to convert the resultant byte array into its signum representation
            BigInteger inputDigestBigInt = new BigInteger(1, inputDigest);

            // Convert the input digest into hex value
            hashText = new StringBuilder(inputDigestBigInt.toString(16));

            //Add preceding 0's to pad the hashtext to make it 32 bit
            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }
            return hashText.toString();
        }
        // Catch block to handle the scenarios when an unsupported message digest algorithm is provided.
        catch (NoSuchAlgorithmException e) {
            hashText = new StringBuilder(Integer.toString(plainText.hashCode()));
        }

        return hashText.toString();
    }
}