package logeek;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import logeek.domain.Beer;
import logeek.domain.Pizza;
import logeek.domain.Storage;
import logeek.health.HasHealthCheck;
import logeek.health.StorageHealthCheck;
import logeek.resource.*;
import org.apache.http.client.HttpClient;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Supplier;

/**
 * Created by msokolov on 8/21/2016.
 */
public abstract class StorageFactory implements HasHealthCheck {
    private Config config;

    public StorageFactory(Config config) {
        this.config = config;
    }

    public abstract Storage<Beer> beerStorage();
    public abstract Storage<Pizza> pizzaStorage();

    @Override
    public HealthCheck createHealthCheck(HttpClient httpClient) throws MalformedURLException {
        return new StorageHealthCheck(
            httpClient,
            new URL("http://" + config.host + ":" + config.healthCheckPort + config.healthCheckPath)
        );
    }

    public enum Type {
        BASIC,
        HTTP_CLIENT,
        HYSTRIX
    }

    public static class Config {
        @NotEmpty
        @JsonProperty
        private String host;

        @Min(8080)
        @Max(65535)
        @JsonProperty
        private int port = 8081;


        @Min(8082)
        @Max(65535)
        @JsonProperty
        private int healthCheckPort = 8082;

        @NotEmpty
        @JsonProperty
        private String healthCheckPath;

        @NotNull
        @JsonProperty
        private StorageFactory.Type type;

        private Supplier<Beer> logeekBeer() {
            return () -> new Beer("LOGEEK Beer");
        }

        private Supplier<Pizza> logeekPizza() {
            return () -> new Pizza("LOGEEK Pizza");
        }


        public StorageFactory create(HttpClient httpClient) throws MalformedURLException, URISyntaxException {
            URL beerUrl = new URL("http://" + host + ":" + port + "/api/beer");
            URI beerUri = beerUrl.toURI();
            URL pizzaUrl = new URL("http://" + host + ":" + port + "/api/pizza");
            URI pizzaUri = pizzaUrl.toURI();
            switch (type) {
                case BASIC:
                    return new StorageFactory(this) {
                        @Override
                        public Storage<Beer> beerStorage() {
                            return new RemoteStorageByHttpUrlConnection<>(beerUrl, logeekBeer());
                        }

                        @Override
                        public Storage<Pizza> pizzaStorage() {
                            return new RemoteStorageByHttpUrlConnection<>(pizzaUrl, logeekPizza());
                        }
                    };
                case HTTP_CLIENT:
                    return new StorageFactory(this) {
                        @Override
                        public Storage<Beer> beerStorage() {
                            return new RemoteStorageByHttpClient<Beer>(httpClient, beerUri, logeekBeer());
                        }

                        @Override
                        public Storage<Pizza> pizzaStorage() {
                            return new RemoteStorageByHttpClient<>(httpClient, pizzaUri, logeekPizza());
                        }
                    };
                case HYSTRIX:
                    return new StorageFactory(this) {
                        @Override
                        public Storage<Beer> beerStorage() {
                            return new RemoteStorageByHystrix<>(httpClient, beerUri, GetBeer::new);
                        }

                        @Override
                        public Storage<Pizza> pizzaStorage() {
                            return new RemoteStorageByHystrix<>(httpClient, pizzaUri, GetPizza::new);
                        }
                    };
            }
            throw new RuntimeException("Unknown remote storage type");
        }
    }
}
