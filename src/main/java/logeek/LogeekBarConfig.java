package logeek;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by msokolov on 9/29/2015.
 */
public class LogeekBarConfig extends Configuration {
    @Valid
    @NotNull
    private HttpClientConfiguration httpClient = new HttpClientConfiguration();
    @Valid
    @NotNull
    private StorageFactory.Config storageFactoryConfig = new StorageFactory.Config();

    @JsonProperty("httpClient")
    public HttpClientConfiguration getHttpClientConfiguration() {
        return httpClient;
    }

    @JsonProperty("httpClient")
    public void setHttpClientConfiguration(HttpClientConfiguration httpClient) {
        this.httpClient = httpClient;
    }

    @JsonProperty("storage")
    public StorageFactory.Config getStorageFactoryConfig() {
        return storageFactoryConfig;
    }

    @JsonProperty("storage")
    public void setStorageFactoryConfig(StorageFactory.Config storageFactoryConfig) {
        this.storageFactoryConfig = storageFactoryConfig;
    }
}
