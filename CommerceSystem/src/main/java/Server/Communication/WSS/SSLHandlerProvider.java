package Server.Communication.WSS;

import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
public class SSLHandlerProvider {

    private static final String PROTOCOL = "TLS";
    private static final String ALGORITHM_SUN_X509="SunX509";
    private static final String ALGORITHM="ssl.KeyManagerFactory.algorithm";
    private static final String KEYSTORE= "ssl_certs/mysslstore.jks";
    private static final String KEYSTORE_TYPE="JKS";
    private static final String KEYSTORE_PASSWORD= "123456";
    private static final String CERT_PASSWORD="123456";
    private  static SSLContext serverSSLContext =null;

    public static SslHandler getSSLHandler(){
        SSLEngine sslEngine=null;
        if(serverSSLContext ==null){
            //System.out.println("Server SSL context is null");
            System.exit(-1);
        }else{
            sslEngine = serverSSLContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(false);

        }
        return new SslHandler(sslEngine);
    }

    public static void initSSLContext () {

        System.out.println("Initiating SSL context");
        String algorithm = Security.getProperty(ALGORITHM);
        if (algorithm == null) {
            algorithm = ALGORITHM_SUN_X509;
        }
        KeyStore ks = null;
        InputStream inputStream=null;
        try {
            inputStream = new FileInputStream(SSLHandlerProvider.class.getClassLoader().getResource(KEYSTORE).getFile());
            ks = KeyStore.getInstance(KEYSTORE_TYPE);
            ks.load(inputStream,KEYSTORE_PASSWORD.toCharArray());
        } catch (IOException e) {
            System.out.println("Cannot load the keystore file");
        } catch (CertificateException e) {
            System.out.println("Cannot get the certificate");
        }  catch (NoSuchAlgorithmException e) {
            System.out.println("Somthing wrong with the SSL algorithm");
        } catch (KeyStoreException e) {
            System.out.println("Cannot initialize keystore");
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                System.out.println("Cannot close keystore file stream ");
            }
        }
        try {

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks,CERT_PASSWORD.toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            // Setting trust store null since we don't need a CA certificate or Mutual Authentication
            TrustManager[] trustManagers = null;

            serverSSLContext = SSLContext.getInstance(PROTOCOL);
            serverSSLContext.init(keyManagers, trustManagers, null);

        } catch (Exception e) {
            System.out.println("Failed to initialize the server-side SSLContext");
        }


    }

}