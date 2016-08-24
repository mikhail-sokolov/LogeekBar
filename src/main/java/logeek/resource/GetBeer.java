package logeek.resource;

import com.google.common.base.Optional;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import io.dropwizard.jackson.Jackson;
import logeek.domain.Beer;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.net.URI;

/**
 * Created by msokolov on 8/23/2016.
 */
public class GetBeer extends HystrixCommand<Optional<Beer>> {
    private final HttpClient httpClient;
    private final URI uri;

    public GetBeer(HttpClient httpClient, URI uri) {
        super(HystrixCommandGroupKey.Factory.asKey("storage.beer"));
        this.httpClient = httpClient;
        this.uri = uri;
    }

    @Override
    protected Optional<Beer> run() throws Exception {
        return httpClient.execute(new HttpGet(uri), (ResponseHandler<Optional<Beer>>) httpResponse -> {
            int status = httpResponse.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = httpResponse.getEntity();
                return (entity != null) ? newItem(entity) : Optional.absent();
            } else {
                return Optional.absent();
            }
        });
    }

    private Optional<Beer> newItem(HttpEntity entity) throws IOException {
        if (Jackson.newObjectMapper().readTree(entity.getContent()).path("item").isMissingNode()) {
            return Optional.absent();
        } else {
            return Optional.of(new Beer("LOGEEK Beer"));
        }
    }

    @Override
    protected Optional<Beer> getFallback() {
        return Optional.absent();
    }
}
