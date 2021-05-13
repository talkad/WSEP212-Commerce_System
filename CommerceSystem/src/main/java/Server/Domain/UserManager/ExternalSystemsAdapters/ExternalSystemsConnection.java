package Server.Domain.UserManager.ExternalSystemsAdapters;

import Server.Domain.CommonClasses.Response;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ExternalSystemsConnection {

    private CloseableHttpClient client;
    private boolean isConnected;

    private ExternalSystemsConnection(){
        this.isConnected = false;
    }

    // Inner class to provide instance of class
    private static class CreateThreadSafeSingleton
    {
        private static final ExternalSystemsConnection INSTANCE = new ExternalSystemsConnection();
    }

    public static ExternalSystemsConnection getInstance()
    {
        return ExternalSystemsConnection.CreateThreadSafeSingleton.INSTANCE;
    }

    /**
     * initiate connection with the external server.
     * if the connection succeeds isConnected will be true, false otherwise.
     * @return positive response if the handshake succeeds.
     */
    public Response<Boolean> createHandshake() {
        Response<String> res;

        // copyright - https://stackoverflow.com/questions/34655031/javax-net-ssl-sslpeerunverifiedexception-host-name-does-not-match-the-certifica
        final SSLConnectionSocketFactory sslsf;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(),
                    NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build();

        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(100);
        this.client = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm)
                .build();

        List<NameValuePair> urlParameters = new LinkedList<>();
        urlParameters.add(new BasicNameValuePair("action_type", "handshake"));

        res = send(urlParameters);

        if(res.isFailure())
            return new Response<>(false, true, "Handshake failed (CRITICAL)");

        return new Response<>(true, false, "Connection initiated successfully");
    }

    public void closeConnection() {
        try {
            client.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Response<String> send(List<NameValuePair> request){
        try {

            HttpEntity postParams = new UrlEncodedFormEntity(request);
            HttpPost httpPost = new HttpPost("https://cs-bgu-wsep.herokuapp.com/");
            httpPost.setEntity(postParams);

            CloseableHttpResponse httpResponse = client.execute(httpPost);

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();

            this.isConnected = true;

            return new Response<>(response.toString(), false, "Request sent successfully. response: " + response.toString());

        }catch (IOException e) {
            closeConnection();
            this.isConnected = false;

            return new Response<>("", true, "Sending failure due to IO exception (CRITICAL)");
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
