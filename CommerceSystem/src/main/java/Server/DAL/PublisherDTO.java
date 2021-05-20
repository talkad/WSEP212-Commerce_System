package Server.DAL;

import Server.Domain.CommonClasses.Pair;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

import java.util.List;

@Entity(value = "publishers")
public class PublisherDTO {

    @Id
    @Property(value = "storeSubscribers")
    private List<Pair<Integer, List<String>>> storeSubscribers;

    public PublisherDTO(){
        // For Morphia
    }

    public PublisherDTO(List<Pair<Integer, List<String>>> storeSubscribers) {
        this.storeSubscribers = storeSubscribers;
    }

    public List<Pair<Integer, List<String>>> getStoreSubscribers() {
        return storeSubscribers;
    }

    public void setStoreSubscribers(List<Pair<Integer, List<String>>> storeSubscribers) {
        this.storeSubscribers = storeSubscribers;
    }
}
