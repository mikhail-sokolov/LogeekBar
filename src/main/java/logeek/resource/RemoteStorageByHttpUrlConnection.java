package logeek.resource;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import io.dropwizard.jackson.Jackson;
import logeek.domain.Storage;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;


/**
 * Created by msokolov on 8/22/2016.
 */
public class RemoteStorageByHttpUrlConnection<T> implements Storage<T> {
    private URL url;
    private Supplier<T> itemFactory;

    public RemoteStorageByHttpUrlConnection(URL url, Supplier<T> itemFactory) {
        this.url = url;
        this.itemFactory = itemFactory;
    }

    @Override
    public Optional<T> get() {
        try {
            URLConnection connection = url.openConnection();
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                String responseBody = scanner.useDelimiter("\\A").next();
                return newItem(responseBody);
            }
        } catch (IOException e) {
            return Optional.absent();
        }
    }

    private Optional<T> newItem(String responseBody) throws IOException {
        if (Jackson.newObjectMapper().readTree(responseBody).path("item").isMissingNode()) {
            return Optional.absent();
        } else {
            return Optional.of(itemFactory.get());
        }
    }
}
