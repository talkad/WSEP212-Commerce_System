package Server.DAL.PairDTOs;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;
import java.util.Vector;

@Embedded
public class IntStringListPair {
    @Property(value = "first")
    private int first;
    @Property(value = "second")
    private List<String> second;

    public IntStringListPair(){
        // for Morphia
    }

    public IntStringListPair(int first, List<String> second){
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public List<String> getSecond() {
        return second == null ? new Vector<>() : second;
    }

    public void setSecond(List<String> second) {
        this.second = second;
    }
}