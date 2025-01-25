package ongoing.backend.utils.http;

import io.reactivex.rxjava3.core.Single;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import ongoing.backend.config.jackson.json.JsonObject;
import ongoing.backend.config.rxTemplate.RxTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class OkHttpService {
  private final OkHttpClient getClient;
  private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  public OkHttpService(OkHttpClient getClient) {
    this.getClient = getClient;
  }

  @SneakyThrows
  public <P> Single<P> postJson(String url, String body, Class<P> rsClass) {
    return RxTemplate.rxSchedulerNewThread(() -> {
      RequestBody requestBody = RequestBody.create(JSON, body);
      Request request = new Request.Builder()
        .url(url)
        .post(requestBody)
        .build();
      try (Response response = getClient.newCall(request).execute()) {
        return new JsonObject(response.body().string()).mapTo(rsClass);
      } catch (IOException e) {
        log.error("[POST REQUEST ERROR: ]", e);
        return null;
      }
    });
  }

  public <P> Single<P> postJson(String url, String body, JsonObject header, Class<P> rsClass) {
    return RxTemplate.rxSchedulerNewThread(() -> {
      RequestBody requestBody = RequestBody.create(JSON, body);
      Request.Builder builder = new Request.Builder()
        .url(url)
        .post(requestBody);
      header.getMap().forEach((s, o) -> builder.addHeader(s, o.toString()));
      Request request = builder.build();
      try (Response response = getClient.newCall(request).execute()) {
        return new JsonObject(response.body().string()).mapTo(rsClass);
      } catch (IOException e) {
        log.error("[POST REQUEST ERROR: ]", e);
        return null;
      }
    });
  }
}
