package Server.DAL.PairDTOs;

import Server.DAL.PredicateDTOs.PredicateDTO;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
public class PredPair {
    @Property(value = "first")
    private PredicateDTO first;
    @Property(value = "second")
    private PredicateDTO second;

    public PredPair(){
        // for Morphia
    }

    public PredPair(PredicateDTO first, PredicateDTO second){
        this.first = first;
        this.second = second;
    }

    public PredicateDTO getFirst() {
        return first;
    }

    public void setFirst(PredicateDTO first) {
        this.first = first;
    }

    public PredicateDTO getSecond() {
        return second;
    }

    public void setSecond(PredicateDTO second) {
        this.second = second;
    }
}
