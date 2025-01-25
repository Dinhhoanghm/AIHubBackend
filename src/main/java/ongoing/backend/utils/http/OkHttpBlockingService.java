package ongoing.backend.utils.http;

import okhttp3.*;
import ongoing.backend.config.jackson.json.JsonObject;
import ongoing.backend.utils.StringUtil;
import org.springframework.stereotype.Component;

@Component
public class OkHttpBlockingService {
    private final OkHttpClient client;

    public OkHttpBlockingService(/*@Qualifier(OKHTTP_SEARCH_BEAN_NAME) */ OkHttpClient client) {
        this.client = client;
    }

    public String post(String url, String jsonBody) throws Exception {
        return post(url, jsonBody, null);
    }

    public <T> T postReturnObject(String url, String jsonBody, Class<T> clazz) {
        String resp = null;
        try {
            resp = post(url, jsonBody, null);
        } catch (Exception e) {
            return null;
        }
        if (resp == null) return null;
        String res = StringUtil.decodeUnicode(resp);
        return new JsonObject(res).mapTo(clazz);
    }

    public String post(String url, String jsonBody, String accessToken) throws Exception {
        MediaType json = MediaType.parse("application/json;charset=utf-8");
        Request request;
        RequestBody body = jsonBody == null ? RequestBody.create(json, "") : RequestBody.create(json, jsonBody);

        if (accessToken == null) {
            request = new Request.Builder().url(url).post(body).build();
        } else {
            request = new Request.Builder().url(url).addHeader("Authorization", "Bearer " + accessToken)
                    .post(body).build();
        }
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            response.body().close();
            return result;
        } catch (Exception exception) {
            return null;
        }
    }

    public String post(String url, String jsonBody, String accessToken, String csrfToken) throws Exception {
        MediaType json = MediaType.parse("application/json;charset=utf-8");
        Request request;
        RequestBody body = jsonBody == null ? RequestBody.create(json, "") : RequestBody.create(json, jsonBody);

        if (accessToken == null) {
            request = new Request.Builder().url(url).post(body).build();
        } else {
            request = new Request.Builder().url(url).addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Csrf-Token", csrfToken)
                    .post(body).build();
        }

        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            response.body().close();
            return result;
        }
    }

    public String put(String url, String accessToken) throws Exception {
        return put(url, null, accessToken);
    }

    public String put(String url, String jsonBody, String accessToken) throws Exception {
        MediaType json = MediaType.parse("application/json;charset=utf-8");
        Request request;
        RequestBody body = jsonBody == null ? RequestBody.create(json, "") : RequestBody.create(json, jsonBody);

        if (accessToken == null) {
            request = new Request.Builder().url(url).put(body).build();
        } else {
            request = new Request.Builder().url(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .put(body).build();
        }

        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            response.body().close();
            return result;
        }
    }

    public String get(String url, String accessToken) throws Exception {

        Request request = new Request.Builder().url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get().build();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            response.body().close();
            return result;
        }
    }

    public String get(String url, JsonObject headers) throws Exception {
        Request.Builder builder = new Request.Builder().url(url);
        headers.getMap().forEach((s, o) -> builder.addHeader(s, o.toString()));
        Request request = builder.get().build();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            response.body().close();
            return result;
        }
    }

    public <T> T getWithToken(String url, String token, Class<T> aClass) throws Exception {

        Request request = new Request.Builder().url(url)
                .addHeader("Token", token)
                .get().build();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            response.body().close();
            return new JsonObject(result).mapTo(aClass);
        }
    }

    public String delete(String url, String accessToken) throws Exception {

        Request request = new Request.Builder().url(url).addHeader("Authorization", "Bearer " + accessToken)
                .delete().build();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            response.body().close();
            return result;
        }
    }


}
