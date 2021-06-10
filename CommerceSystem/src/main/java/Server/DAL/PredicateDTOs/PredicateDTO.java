package Server.DAL.PredicateDTOs;

import Server.Domain.ShoppingManager.Predicates.Predicate;
import dev.morphia.annotations.Embedded;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("PredicateDTO")

public interface PredicateDTO {
    public Predicate toConcretePredicate();
}
