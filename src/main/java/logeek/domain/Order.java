package logeek.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

/**
 * Created by msokolov on 10/5/2015.
 */
public class Order {
    @Min(value = 1)
    private final int id;
    private final MenuItem menuItem;

    @JsonCreator
    public Order(@JsonProperty(value = "id") int id, @JsonProperty(value = "item") MenuItem menuItem) {
        this.id = id;
        this.menuItem = menuItem;
    }

    public int getId() {
        return id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }
}
