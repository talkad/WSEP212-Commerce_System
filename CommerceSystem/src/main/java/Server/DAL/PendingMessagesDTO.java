package Server.DAL;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;

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
        return pendingMessages;
    }

    public void setPendingMessages(List<ReplyMessageDTO> pendingMessages) {
        this.pendingMessages = pendingMessages;
    }
}
