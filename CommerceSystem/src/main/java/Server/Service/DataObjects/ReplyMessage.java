package Server.Service.DataObjects;


public class ReplyMessage {
    String type;
    String message;

    public ReplyMessage(String type, String msg){
        this.type = type;
        this.message = msg;
    }
}
