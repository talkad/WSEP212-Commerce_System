package Server.Domain.UserManager.ExternalSystemsAdapters;

import Server.Domain.CommonClasses.Response;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

/**
 * This class in the only class who communicates with the external delivery system,
 * and should get one as parameter when created
 */

public class ProductSupplyAdapter
{

    ExternalSystemsConnection conn;

    private ProductSupplyAdapter(){
        conn = ExternalSystemsConnection.getInstance();
    }

    // Inner class to provide instance of class
    private static class CreateThreadSafeSingleton
    {
        private static final ProductSupplyAdapter INSTANCE = new ProductSupplyAdapter();
    }

    public static ProductSupplyAdapter getInstance()
    {
        return CreateThreadSafeSingleton.INSTANCE;
    }

    public Response<Integer> supply(SupplyDetails supplyDetails){
        List<NameValuePair> urlParameters = new LinkedList<>();
        Response<Boolean> connRes;
        Response<String> externalRes;
        int transactionID;

        if(!conn.isConnected()){
            connRes = conn.createHandshake();

            if(connRes.isFailure())
                return new Response<>(-1, true, "supply transaction failed due to error in handshake (CRITICAL)");
        }

        urlParameters.add(new BasicNameValuePair("action_type", "supply"));
        urlParameters.add(new BasicNameValuePair("name", supplyDetails.getName()));
        urlParameters.add(new BasicNameValuePair("address", supplyDetails.getAddress()));
        urlParameters.add(new BasicNameValuePair("city", supplyDetails.getCity()));
        urlParameters.add(new BasicNameValuePair("country", supplyDetails.getCountry()));
        urlParameters.add(new BasicNameValuePair("zip", supplyDetails.getZip()));

        externalRes = conn.send(urlParameters);
        if(externalRes.isFailure()){
            return new Response<>(-1, true, "Supply failed due to sending error (CRITICAL)");
        }
        else{
            transactionID = Integer.parseInt(externalRes.getResult());

            if(transactionID < 0)
                return new Response<>(transactionID, true, "Supply failed error code " + transactionID);

            return new Response<>(transactionID, false, "Supply occurred successfully");
        }
    }

    public Response<Integer> cancelSupply(String transactionID){
        List<NameValuePair> urlParameters = new LinkedList<>();
        Response<Boolean> connRes;
        Response<String> externalRes;
        int result;

        if(!conn.isConnected()){
            connRes = conn.createHandshake();

            if(connRes.isFailure())
                return new Response<>(-1, true, "supply cancellation transaction failed due to error in handshake (CRITICAL)");
        }

        urlParameters.add(new BasicNameValuePair("action_type", "cancel_supply"));
        urlParameters.add(new BasicNameValuePair("transaction_id", transactionID));

        externalRes = conn.send(urlParameters);
        if(externalRes.isFailure()){
            return new Response<>(-1, true, "Supply cancellation failed due to sending error (CRITICAL)");
        }
        else{
            result = Integer.parseInt(externalRes.getResult());

            if(result < 0)
                return new Response<>(result, true, "Supply cancellation failed error code " + result);

            return new Response<>(result, false, "Supply cancellation occurred successfully");
        }
    }
}
