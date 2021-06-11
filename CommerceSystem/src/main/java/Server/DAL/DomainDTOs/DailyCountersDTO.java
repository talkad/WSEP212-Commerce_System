package Server.DAL.DomainDTOs;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

@Entity(value = "dailyCounters")
public class DailyCountersDTO {

    @Id
    @Property(value = "currentDate")
    private String currentDate;

    @Property(value = "guestCounter")
    private int guestCounter;

    @Property(value = "registeredCounter")
    private int registeredCounter;

    @Property(value = "managerCounter")
    private int managerCounter;

    @Property(value = "ownerCounter")
    private int ownerCounter;

    @Property(value = "adminCounter")
    private int adminCounter;

    public DailyCountersDTO(){
        // For Morphia
    }

    public DailyCountersDTO(String currentDate, int guestCounter, int registeredCounter, int managerCounter, int ownerCounter, int adminCounter) {
        this.currentDate = currentDate;
        this.guestCounter = guestCounter;
        this.registeredCounter = registeredCounter;
        this.managerCounter = managerCounter;
        this.ownerCounter = ownerCounter;
        this.adminCounter = adminCounter;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public int getGuestCounter() {
        return guestCounter;
    }

    public void setGuestCounter(int guestCounter) {
        this.guestCounter = guestCounter;
    }

    public int getRegisteredCounter() {
        return registeredCounter;
    }

    public void setRegisteredCounter(int registeredCounter) {
        this.registeredCounter = registeredCounter;
    }

    public int getManagerCounter() {
        return managerCounter;
    }

    public void setManagerCounter(int managerCounter) {
        this.managerCounter = managerCounter;
    }

    public int getOwnerCounter() {
        return ownerCounter;
    }

    public void setOwnerCounter(int ownerCounter) {
        this.ownerCounter = ownerCounter;
    }

    public int getAdminCounter() {
        return adminCounter;
    }

    public void setAdminCounter(int adminCounter) {
        this.adminCounter = adminCounter;
    }
}
