package Server.DAL;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("AppointmentDTO")
public class AppointmentDTO {

    @Property(value = "storeAppointments")
    private List<IntStringListPair> storeAppointments;

    public AppointmentDTO(){
        // For Morphia
    }

    public AppointmentDTO(List<IntStringListPair> storeAppointments) {
        this.storeAppointments = storeAppointments;
    }

    public List<IntStringListPair> getStoreAppointments() {
        return storeAppointments == null ? new Vector<>() : storeAppointments;
    }

    public void setStoreAppointments(List<IntStringListPair> storeAppointments) {
        this.storeAppointments = storeAppointments;
    }
}
