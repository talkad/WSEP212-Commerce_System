package Server.DAL;

import Server.DAL.PairDTOs.IntStringListPair;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

import java.util.List;
import java.util.Vector;

@Entity(value = "publishers")
public class PublisherDTO {

    @Id
    @Property(value = "id")
    private int id = 0;

    @Property(value = "storeSubscribers")
    private List<IntStringListPair> storeSubscribers;

    public PublisherDTO(){
        // For Morphia
    }

    public PublisherDTO(List<IntStringListPair> storeSubscribers) {
        this.storeSubscribers = storeSubscribers;
    }

    public List<IntStringListPair> getStoreSubscribers() {
        return storeSubscribers == null ? new Vector<>() : storeSubscribers;
    }

    public void setStoreSubscribers(List<IntStringListPair> storeSubscribers) {
        this.storeSubscribers = storeSubscribers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
