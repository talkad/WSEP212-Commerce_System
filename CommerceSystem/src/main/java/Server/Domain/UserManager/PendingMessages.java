package Server.Domain.UserManager;

import java.util.List;
import java.util.Vector;

public class PendingMessages {
    private List<String> pendingMessages;

    public PendingMessages() {
        this.pendingMessages = new Vector<>();
    }

    public List<String> getPendingMessages() {
        return pendingMessages;
    }

    public void addMessage(String msg){
        pendingMessages.add(msg);
    }

    public void clear(){
        pendingMessages.clear();
    }

}
