package logeek.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.params.IntParam;
import logeek.domain.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by msokolov on 9/29/2015.
 */
@Path("/order")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {
    private final OrderExecutor orderExecutor;

    public OrderResource(OrderExecutor orderExecutor) {
        this.orderExecutor = orderExecutor;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public OrderAck makeOrder(@Valid Order order) {
        return orderExecutor.proccess(order);
    }

    @Path("/total/{id}")
    @GET
    @Timed
    public TotalOrder totalOrderById(@PathParam("id")IntParam id) {
        return orderExecutor.totalOrderById(id.get());
    }

    @Path("/total")
    @GET
    @Timed
    public List<TotalOrder> totalOrder() {
        return orderExecutor.totalOrder();
    }

    @Path("/top")
    @GET
    @Timed
    public List<Consumer> top() {
        return orderExecutor.top();
    }
}
