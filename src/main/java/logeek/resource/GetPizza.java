package logeek.resource;

import com.google.common.base.Optional;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import io.dropwizard.jackson.Jackson;
import logeek.domain.Pizza;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.net.URI;

/**
 * Created by msokolov on 8/23/2016.
 */
public class GetPizza extends HystrixCommand<Optional<Pizza>> {
    private final HttpClient httpClient;
    private final URI uri;

    public GetPizza(HttpClient httpClient, URI uri) {
        super(HystrixCommandGroupKey.Factory.asKey("storage.pizza"));
        this.httpClient = httpClient;
        this.uri = uri;
    }

    @Override
    protected Optional<Pizza> run() throws Exception {
        return httpClient.execute(new HttpGet(uri), (ResponseHandler<Optional<Pizza>>) httpResponse -> {
            int status = httpResponse.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = httpResponse.getEntity();
                return (entity != null) ? newItem(entity) : Optional.absent();
            } else {
                return Optional.absent();
            }
        });
    }

    private Optional<Pizza> newItem(HttpEntity entity) throws IOException {
        if (Jackson.newObjectMapper().readTree(entity.getContent()).path("item").isMissingNode()) {
            return Optional.absent();
        } else {
            return Optional.of(new Pizza());
        }
    }

    @Override
    protected Optional<Pizza> getFallback() {
        return Optional.absent();
    }
}
