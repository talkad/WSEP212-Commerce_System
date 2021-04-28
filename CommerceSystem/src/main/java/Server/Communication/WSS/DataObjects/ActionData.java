package Server.Communication.WSS.DataObjects;

import Server.Domain.CommonClasses.Response;

public class ActionData {
    String type;
    Response<?> response;

    public ActionData(String type, Response<?> response){
        this.type = type;
        this.response = response;
    }
}