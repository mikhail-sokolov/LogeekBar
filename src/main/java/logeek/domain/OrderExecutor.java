package logeek.domain;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by msokolov on 10/5/2015.
 */
public class OrderExecutor {
    private ConcurrentLinkedQueue<Order> ordersQueue;
    private Storage<Beer> beerStorage;
    private Storage<Pizza> pizzaStorage;

    public OrderExecutor(ConcurrentLinkedQueue<Order> ordersQueue, Storage<Beer> beerStorage, Storage<Pizza> pizzaStorage) {
        this.ordersQueue = ordersQueue;
        this.beerStorage = beerStorage;
        this.pizzaStorage = pizzaStorage;
    }

    public OrderAck proccess(Order order) {
        ordersQueue.add(order);
        switch (order.getMenuItem()) {
            case BEER:
                return new OrderAck(beerStorage.get().or(new Beer("Beer")), order.getMenuItem());
            case PIZZA:
                return new OrderAck(pizzaStorage.get().or(new Pizza("Pizza")), order.getMenuItem());
            default:
                throw new IllegalArgumentException("Unknown menu item");
        }
    }

    public TotalOrder totalOrderById(int id) {
        Stream<Order> ordersById = ordersQueue.stream().filter(o -> o.getId() == id);
        return createTotalOrder(id, ordersById);
    }

    private TotalOrder createTotalOrder(int id, Stream<Order> ordersById) {
        Map<MenuItem, List<Order>> groupByMenuItem = groupByMenuItem(ordersById);
        return new TotalOrder(
                id,
                amountOf(MenuItem.BEER, groupByMenuItem),
                amountOf(MenuItem.PIZZA, groupByMenuItem)
        );
    }

    private Map<MenuItem, List<Order>> groupByMenuItem(Stream<Order> orders) {
        return orders.collect(Collectors.groupingBy(Order::getMenuItem));
    }

    private int amountOf(MenuItem menuItem, Map<MenuItem, List<Order>> ordersByMenuItem) {
        return ordersByMenuItem.getOrDefault(menuItem, Collections.emptyList()).size();
    }

    public List<TotalOrder> totalOrder() {
        Map<Integer, List<Order>> ordersById = ordersQueue.stream().collect(Collectors.groupingBy(Order::getId));
        return ordersById.entrySet().stream().
                map(entry -> createTotalOrder(entry.getKey(), entry.getValue().stream())).
                collect(Collectors.toList());
    }

    public List<Consumer> top() {
        List<Consumer> topConsumers = new ArrayList<>(2);
        topConsumer(MenuItem.BEER).ifPresent(topConsumers::add);
        topConsumer(MenuItem.PIZZA).ifPresent(topConsumers::add);
        return topConsumers;
    }

    private Optional<Consumer> topConsumer(MenuItem item) {
        return ordersQueue.stream().
                filter(order -> order.getMenuItem().equals(item)).
                collect(Collectors.groupingBy(Order::getId)).
                entrySet().stream().
                map(entry -> new Consumer(entry.getKey(), item.name(), entry.getValue().size())).
                sorted((c1, c2) -> c2.getAmount() - c1.getAmount()).
                //peek(consumer -> System.out.println(consumer.getAmount())).
                findFirst();
    }
}
