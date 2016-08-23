package logeek.resource;

import com.google.common.base.Optional;
import com.netflix.hystrix.HystrixCommand;
import logeek.domain.Storage;
import org.apache.http.client.HttpClient;

import java.net.URI;
import java.util.function.BiFunction;

/**
 * Created by msokolov on 8/22/2016.
 */
public class RemoteStorageByHystrix<T> implements Storage<T> {
    private final HttpClient httpClient;
    private final URI uri;
    private final BiFunction<HttpClient, URI, HystrixCommand<Optional<T>>> aNewCommand;

    public RemoteStorageByHystrix(
        HttpClient httpClient,
        URI uri,
        BiFunction<HttpClient, URI, HystrixCommand<Optional<T>>> aNew
    ) {
        this.httpClient = httpClient;
        this.uri = uri;
        this.aNewCommand = aNew;
    }

    @Override
    public Optional<T> get() {
        return aNewCommand.apply(httpClient, uri).execute();
    }
}
