package Server.DAL;

import Server.Domain.CommonClasses.Pair;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;
import java.util.Vector;

@Embedded
public class AppointmentDTO {

    @Property(value = "storeAppointments")
    private List<Pair<Integer, List<String>>> storeAppointments;

    public AppointmentDTO(){
        // For Morphia
    }

    public AppointmentDTO(List<Pair<Integer, List<String>>> storeAppointments) {
        this.storeAppointments = storeAppointments;
    }

    public List<Pair<Integer, List<String>>> getStoreAppointments() {
        return storeAppointments == null ? new Vector<>() : storeAppointments;
    }

    public void setStoreAppointments(List<Pair<Integer, List<String>>> storeAppointments) {
        this.storeAppointments = storeAppointments;
    }
}
