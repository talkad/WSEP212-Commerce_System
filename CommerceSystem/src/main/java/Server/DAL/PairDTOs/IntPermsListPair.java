package Server.DAL.PairDTOs;

import Server.Domain.UserManager.PermissionsEnum;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("IntPermsListPair")

public class IntPermsListPair {
    @Property(value = "first")
    private int first;
    @Property(value = "second")
    private List<PermissionsEnum> second;

    public IntPermsListPair(){
        // for Morphia
    }

    public IntPermsListPair(int first, List<PermissionsEnum> second){
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public List<PermissionsEnum> getSecond() {
        return second == null ? new Vector<>() : second;
    }

    public void setSecond(List<PermissionsEnum> second) {
        this.second = second;
    }
}
