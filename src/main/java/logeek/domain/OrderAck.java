package logeek.domain;

/**
 * Created by msokolov on 10/6/2015.
 */
public class OrderAck {
    private Item item;
    private MenuItem menuItem;

    public OrderAck(Item item, MenuItem menuItem) {
        this.item = item;
        this.menuItem = menuItem;
    }

    public Item getItem() {
        return item;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }
}
