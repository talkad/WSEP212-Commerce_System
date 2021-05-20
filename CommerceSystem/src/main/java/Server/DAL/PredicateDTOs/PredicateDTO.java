package Server.DAL.PredicateDTOs;

import Server.Domain.ShoppingManager.Predicates.Predicate;
import dev.morphia.annotations.Embedded;

@Embedded
public interface PredicateDTO {
    public Predicate toConcretePredicate();
}
