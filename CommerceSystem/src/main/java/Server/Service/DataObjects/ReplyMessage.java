package Server.Service.DataObjects;


public class ReplyMessage {
    String type;
    String message;
    String action;

    public ReplyMessage(String type, String msg, String action){
        this.type = type;
        this.message = msg;
        this.action = action;
    }
}
