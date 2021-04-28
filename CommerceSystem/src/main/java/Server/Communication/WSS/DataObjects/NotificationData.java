package Server.Communication.WSS.DataObjects;

public class NotificationData {
    String type;
    String message;

    public NotificationData(String message){
        this.type = "notification";
        this.message = message;
    }
}