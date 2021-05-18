package Server.Service.DataObjects;


import Server.DAL.ReplyMessageDTO;

public class ReplyMessage {
    String type;
    String message;

    public ReplyMessage(String type, String msg){
        this.type = type;
        this.message = msg;
    }

    public ReplyMessage(ReplyMessageDTO replyMessageDTO){
        this.type = replyMessageDTO.getType();
        this.message = replyMessageDTO.getMessage();
    }

    public ReplyMessageDTO toDTO(){
        return new ReplyMessageDTO(this.type, this.message);
    }
}
