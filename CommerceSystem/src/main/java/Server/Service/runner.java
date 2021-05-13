package Server.Service;

import Server.Domain.UserManager.ExternalSystemsAdapters.ExternalSystemsConnection;

public class runner {


    public static void main(String[] args){
        ExternalSystemsConnection conn = ExternalSystemsConnection.getInstance();

        conn.createHandshake();
    }
}
