package Server.Domain.CommonClasses;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
public class Pair<T, U> {
    @Property(value = "first")
    private T first;
    @Property(value = "second")
    private U second;

    public Pair(){
        // for Morphia
    }

    public Pair(T first, U second){
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public U getSecond() {
        return second;
    }

    public void setSecond(U second) {
        this.second = second;
    }
}

