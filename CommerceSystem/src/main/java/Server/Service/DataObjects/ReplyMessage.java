package Server.Service.DataObjects;


import Server.DAL.DomainDTOs.ReplyMessageDTO;

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

    public String getType() {
        return type;
    }

    public String getAction() {
        return action;
    }

    public ReplyMessage(ReplyMessageDTO replyMessageDTO){
        this.type = replyMessageDTO.getType();
        this.message = replyMessageDTO.getMessage();
        this.action = replyMessageDTO.getAction();
    }

    public ReplyMessageDTO toDTO(){
        return new ReplyMessageDTO(this.type, this.message, this.action);
    }
}
