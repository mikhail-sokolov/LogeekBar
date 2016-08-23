package logeek.resource;

import com.google.common.base.Optional;
import io.dropwizard.jackson.Jackson;
import logeek.domain.Storage;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Supplier;

/**
 * Created by msokolov on 8/21/2016.
 */
public class RemoteStorageByHttpClient<T> implements Storage<T> {
    private HttpClient client;
    private URI uri;
    private Supplier<T> itemFactory;

    public RemoteStorageByHttpClient(HttpClient client, URI uri, Supplier<T> itemFactory) {
        this.client = client;
        this.uri = uri;
        this.itemFactory = itemFactory;
    }

    @Override
    public Optional<T> get() {
        try {
            return client.execute(new HttpGet(uri), (ResponseHandler<Optional<T>>) httpResponse -> {
                int status = httpResponse.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = httpResponse.getEntity();
                    return (entity != null) ? newItem(entity) : Optional.absent();
                } else {
                    return Optional.absent();
                }
            });
        } catch (IOException e) {
            return Optional.absent();
        }
    }

    private Optional<T> newItem(HttpEntity entity) throws IOException {
        if (Jackson.newObjectMapper().readTree(entity.getContent()).path("item").isMissingNode()) {
            return Optional.absent();
        } else {
            return Optional.of(itemFactory.get());
        }
    }
}
