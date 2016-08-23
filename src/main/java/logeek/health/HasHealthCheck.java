package logeek.health;

import com.codahale.metrics.health.HealthCheck;
import org.apache.http.client.HttpClient;

import java.net.MalformedURLException;

/**
 * Created by msokolov on 8/22/2016.
 */
public interface HasHealthCheck {
    HealthCheck createHealthCheck(HttpClient httpClient) throws MalformedURLException;
}
