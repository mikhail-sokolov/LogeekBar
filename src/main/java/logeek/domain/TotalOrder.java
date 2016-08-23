package logeek.domain;

/**
 * Created by msokolov on 10/5/2015.
 */
public class TotalOrder {
    private final int id;
    private final long beer;
    private final long pizza;

    public TotalOrder(int id, long beer, long pizza) {
        this.id = id;
        this.beer = beer;
        this.pizza = pizza;
    }

    public int getId() {
        return id;
    }

    public long getBeer() {
        return beer;
    }

    public long getPizza() {
        return pizza;
    }
}
