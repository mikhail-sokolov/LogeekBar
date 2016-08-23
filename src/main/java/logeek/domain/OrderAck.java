package logeek.domain;

/**
 * Created by msokolov on 10/6/2015.
 */
public class OrderAck {
    private MenuItem menuItem;

    public OrderAck(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }
}
