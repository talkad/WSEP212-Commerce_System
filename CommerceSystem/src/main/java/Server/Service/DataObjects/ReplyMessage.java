package Server.Service.DataObjects;


public class ReplyMessage {
    String type;
    String message;
    String action;

    public ReplyMessage(String type, String message, String action){
        this.type = type;
        this.message = message;
        this.action = action;
    }

    public String getMessage(){
        return message;
    }
}
