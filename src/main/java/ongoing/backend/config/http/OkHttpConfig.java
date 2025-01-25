package ongoing.backend.config.http;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.SECONDS;

@Component
public class OkHttpConfig {
    private static ConnectionPool connectionPool = new ConnectionPool(1000, 15, SECONDS);

    @Bean
    public OkHttpClient getClient() {
        return new OkHttpClient.Builder().connectionPool(connectionPool).build();
    }
}
