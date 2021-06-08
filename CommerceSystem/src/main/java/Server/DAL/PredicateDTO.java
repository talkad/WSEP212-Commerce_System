package Server.DAL;

import Server.Domain.ShoppingManager.Predicates.Predicate;
import dev.morphia.annotations.Embedded;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("PredicateDTO")

public class PredicateDTO {
    public PredicateDTO(){
        //For Morphia
    }

    public Predicate toConcretePredicate(){return null;}
}
