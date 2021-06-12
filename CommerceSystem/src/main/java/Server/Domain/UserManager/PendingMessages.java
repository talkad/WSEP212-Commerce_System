package Server.Domain.UserManager;

import Server.DAL.DomainDTOs.PendingMessagesDTO;
import Server.DAL.DomainDTOs.ReplyMessageDTO;
import Server.Service.DataObjects.ReplyMessage;

import java.util.List;
import java.util.Vector;

public class PendingMessages {
    private List<ReplyMessage> pendingMessages;

    public PendingMessages() {
        this.pendingMessages = new Vector<>();
    }

    public PendingMessages(PendingMessagesDTO pendingMessagesDTO){
        this.pendingMessages = new Vector<>();

        List<ReplyMessageDTO> messages = pendingMessagesDTO.getPendingMessages();
        if(messages != null){
            for(ReplyMessageDTO replyMessageDTO : messages){
                this.pendingMessages.add(new ReplyMessage(replyMessageDTO));
            }
        }
    }

    public PendingMessagesDTO toDTO(){
        // TODO maybe make thread-safe
        List<ReplyMessageDTO> messages = new Vector<>();

        for(ReplyMessage replyMessage : pendingMessages){
            messages.add(replyMessage.toDTO());
        }

        return new PendingMessagesDTO(messages);
    }

    public List<ReplyMessage> getPendingMessages() {
        return pendingMessages;
    }

    public void addMessage(ReplyMessage msg){
        pendingMessages.add(msg);
    }

    public void clear(){
        pendingMessages.clear();
    }

}
