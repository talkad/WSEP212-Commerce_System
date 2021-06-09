package Server.DAL.PairDTOs;

import Server.DAL.ProductDTO;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("ProductIntPair")
public class ProductIntPair {
    @Property(value = "first")
    private ProductDTO first;
    @Property(value = "second")
    private int second;

    public ProductIntPair(){
        // for Morphia
    }

    public ProductIntPair(ProductDTO first, int second){
        this.first = first;
        this.second = second;
    }

    public ProductDTO getFirst() {
        return first;
    }

    public void setFirst(ProductDTO first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
