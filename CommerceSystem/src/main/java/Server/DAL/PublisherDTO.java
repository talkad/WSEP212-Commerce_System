package Server.DAL;

import Server.Domain.CommonClasses.Pair;
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
    private List<Pair<Integer, List<String>>> storeSubscribers;

    public PublisherDTO(){
        // For Morphia
    }

    public PublisherDTO(List<Pair<Integer, List<String>>> storeSubscribers) {
        this.storeSubscribers = storeSubscribers;
    }

    public List<Pair<Integer, List<String>>> getStoreSubscribers() {
        return storeSubscribers == null ? new Vector<>() : storeSubscribers;
    }

    public void setStoreSubscribers(List<Pair<Integer, List<String>>> storeSubscribers) {
        this.storeSubscribers = storeSubscribers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
