package Server.Domain.UserManager;

import Server.Service.DataObjects.ReplyMessage;

import java.util.List;
import java.util.Vector;

public class PendingMessages {
    private List<ReplyMessage> pendingMessages;

    public PendingMessages() {
        this.pendingMessages = new Vector<>();
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
