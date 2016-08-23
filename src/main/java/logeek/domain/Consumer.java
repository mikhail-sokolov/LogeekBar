package logeek.domain;

/**
 * Created by msokolov on 10/8/2015.
 */
public class Consumer {
    private final int id;
    private final String itemName;
    private final int amount;

    public Consumer(int id, String itemName, int amount) {
        this.id = id;
        this.itemName = itemName;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public int getAmount() {
        return amount;
    }
}
