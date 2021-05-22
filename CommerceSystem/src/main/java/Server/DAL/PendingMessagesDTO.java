package Server.DAL;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;
import java.util.Vector;

@Embedded
public class PendingMessagesDTO {

    @Property(value = "pendingMessages")
    private List<ReplyMessageDTO> pendingMessages;

    public PendingMessagesDTO(){
        // For Morphia
    }

    public PendingMessagesDTO(List<ReplyMessageDTO> pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    public List<ReplyMessageDTO> getPendingMessages() {
        return pendingMessages == null ? new Vector<>() : pendingMessages;
    }

    public void setPendingMessages(List<ReplyMessageDTO> pendingMessages) {
        this.pendingMessages = pendingMessages;
    }
}
