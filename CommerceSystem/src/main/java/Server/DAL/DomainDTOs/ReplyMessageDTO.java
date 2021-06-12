package Server.DAL.DomainDTOs;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("ReplyMessageDTO")

public class ReplyMessageDTO {

    @Property(value = "type")
    String type;

    @Property(value = "message")
    String message;

    @Property(value = "action")
    String action;

    public ReplyMessageDTO(){
        // For Morphia
    }

    public ReplyMessageDTO(String type, String message, String action) {
        this.type = type;
        this.message = message;
        this.action = action;
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

    public String getAction() {
        return this.action;
    }
}
