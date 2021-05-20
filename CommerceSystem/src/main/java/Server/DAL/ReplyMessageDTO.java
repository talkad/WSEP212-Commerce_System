package Server.DAL;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
public class ReplyMessageDTO {

    @Property(value = "type")
    String type;

    @Property(value = "message")
    String message;

    public ReplyMessageDTO(){
        // For Morphia
    }

    public ReplyMessageDTO(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
