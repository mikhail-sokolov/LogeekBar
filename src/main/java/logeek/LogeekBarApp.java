package logeek;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import logeek.domain.*;
import logeek.resource.OrderResource;
import org.apache.http.client.HttpClient;
import org.zapodot.hystrix.bundle.HystrixBundle;

import java.util.concurrent.*;

/**
 * Created by msokolov on 9/29/2015.
 */
public class LogeekBarApp extends Application<LogeekBarConfig> {
    @Override
    public void initialize(Bootstrap<LogeekBarConfig> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
        bootstrap.addBundle(HystrixBundle.withDefaultSettings());
    }

    @Override
    public void run(LogeekBarConfig config, Environment environment) throws Exception {
        final HttpClient httpClient = new HttpClientBuilder(environment).using(config.getHttpClientConfiguration()).build("logeek");

        StorageFactory storageFactory = config.getStorageFactoryConfig().create(httpClient);
        Storage beerStorage = storageFactory.beerStorage();
        Storage pizzaStorage = storageFactory.pizzaStorage();
        OrderExecutor orderExecutor = new OrderExecutor(new ConcurrentLinkedQueue<>(), beerStorage, pizzaStorage);

        environment.jersey().register(new OrderResource(orderExecutor));
        environment.healthChecks().register("Storage check", storageFactory.createHealthCheck(httpClient));
    }

    public static void main(String[] args) throws Exception {
        new LogeekBarApp().run(args);
    }
}
