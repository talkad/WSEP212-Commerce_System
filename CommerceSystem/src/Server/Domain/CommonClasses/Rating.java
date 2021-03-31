package Server.Domain.CommonClasses;


public enum Rating {
    VERY_HIGH(5),
    HIGH(4),
    MEDIUM(3),
    BAD(2),
    VERY_BAD(1);

    public final int rate;

    private Rating(int rate) {
        this.rate = rate;
    }
}