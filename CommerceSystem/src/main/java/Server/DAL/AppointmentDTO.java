package Server.DAL;

import Server.DAL.PairDTOs.IntStringListPair;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;
import java.util.Vector;

@Embedded
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
