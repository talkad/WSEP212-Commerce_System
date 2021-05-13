package Server.Domain.UserManager.ExternalSystemsAdapters;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class in the only class who communicates with the external payment system,
 * and should get one as parameter when created
 */

public class PaymentSystemAdapter
{

    ExternalSystemsConnection conn;

    private PaymentSystemAdapter()
    {
        conn = ExternalSystemsConnection.getInstance();
    }

    // Inner class to provide instance of class
    private static class CreateThreadSafeSingleton
    {
        private static final PaymentSystemAdapter INSTANCE = new PaymentSystemAdapter();
    }

    public static PaymentSystemAdapter getInstance()
    {
        return CreateThreadSafeSingleton.INSTANCE;
    }

    // todo - remove
    public void pay(double a, String b){
//        externalSupplier.deliver(location, details);
    }

    public boolean canPay(double a, String b){
//        return externalSupplier.canDeliver(location, details);
        return false;
    }

    public Response<Integer> pay(String card_number, String month, String year, String holder, String ccv, String id){
        List<NameValuePair> urlParameters = new LinkedList<>();
        Response<Boolean> connRes;
        Response<String> externalRes;
        int transactionID;

        if(!conn.isConnected()){
            connRes = conn.createHandshake();

            if(connRes.isFailure())
                return new Response<>(-1, true, "pay transaction failed due to error in handshake");
        }

        urlParameters.add(new BasicNameValuePair("action_type", "pay"));
        urlParameters.add(new BasicNameValuePair("card_number", card_number));
        urlParameters.add(new BasicNameValuePair("month", month));
        urlParameters.add(new BasicNameValuePair("year", year));
        urlParameters.add(new BasicNameValuePair("holder", holder));
        urlParameters.add(new BasicNameValuePair("ccv", ccv));
        urlParameters.add(new BasicNameValuePair("id", id));

        externalRes = conn.send(urlParameters);
        if(externalRes.isFailure()){
            return new Response<>(-1, true, "payment failed due to sending error");
        }
        else{
            transactionID = Integer.parseInt(externalRes.getResult());

            if(transactionID < 0)
                return new Response<>(transactionID, true, "Payment failed error code " + transactionID);

            return new Response<>(transactionID, false, "payment occurred successfully");
        }
    }

    public Response<Integer> cancelPay(String transactionID){
        List<NameValuePair> urlParameters = new LinkedList<>();
        Response<Boolean> connRes;
        Response<String> externalRes;
        int result;

        if(!conn.isConnected()){
            connRes = conn.createHandshake();

            if(connRes.isFailure())
                return new Response<>(-1, true, "pay cancellation transaction failed due to error in handshake");
        }

        urlParameters.add(new BasicNameValuePair("action_type", "cancel_pay"));
        urlParameters.add(new BasicNameValuePair("transaction_id", transactionID));

        externalRes = conn.send(urlParameters);
        if(externalRes.isFailure()){
            return new Response<>(-1, true, "payment cancellation failed due to sending error");
        }
        else{
            result = Integer.parseInt(externalRes.getResult());

            if(result < 0)
                return new Response<>(result, true, "Payment cancellation failed error code " + result);

            return new Response<>(result, false, "payment cancellation occurred successfully");
        }
    }

}
