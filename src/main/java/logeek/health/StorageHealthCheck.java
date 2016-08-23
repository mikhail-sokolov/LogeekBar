package logeek.health;

import com.codahale.metrics.health.HealthCheck;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.net.URL;

/**
 * Created by msokolov on 9/29/2015.
 */
public class StorageHealthCheck extends HealthCheck {
    public static final String EMPTY_STRING = "";
    private static final String PONG = "pong";
    private HttpClient httpClient;
    private URL url;

    public StorageHealthCheck(HttpClient httpClient, URL url) {
        this.httpClient = httpClient;
        this.url = url;
    }

    @Override
    protected Result check() throws Exception {
        String result = httpClient.execute(new HttpGet(url.toURI()), httpResponse -> {
            int status = httpResponse.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = httpResponse.getEntity();
                return (entity != null) ? EntityUtils.toString(entity) : EMPTY_STRING;
            } else {
                return EMPTY_STRING;
            }
        });

        if (PONG.equals(result)) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Unsuccessful ping");
        }
    }
}
