package ongoing.backend.service.rapidApi;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import okhttp3.*;
import ongoing.backend.config.exception.ApiException;
import ongoing.backend.config.jackson.json.JsonArray;
import ongoing.backend.config.jackson.json.JsonObject;
import ongoing.backend.data.dto.file.JsonNestedRequest;
import ongoing.backend.data.dto.file.JsonOutput;
import ongoing.backend.data.dto.file.NestParamData;
import ongoing.backend.data.rapidApi.*;
import ongoing.backend.service.readFile.ConvertJsonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ongoing.backend.config.jackson.json.Json.encode;
import static ongoing.backend.config.jackson.json.Json.encodeCamelCase;

@Service
public class RapidApiService {
  private final ConvertJsonService convertJsonService;
  private final Gson gson;
  @Value("${spring.rapidApi.secret-key}")
  private String rapidApiKey;

  public RapidApiService(ConvertJsonService convertJsonService, Gson gson) {
    this.convertJsonService = convertJsonService;
    this.gson = gson;
  }

  public Map<String, Object> getRapidDataResponse(String endpoint, String slugName, String params, String nestParams) throws IOException, ApiException {
    Type listType = new TypeToken<List<String>>() {
    }.getType();
    List<String> nestParamLst = gson.fromJson(nestParams, listType);
    OkHttpClient client = new OkHttpClient().newBuilder()
      .build();
    String url = String.format("https://%s.p.rapidapi.com/%s?%s", slugName, endpoint, params);
    Request request = new Request.Builder()
      .url(url)
      .addHeader("x-rapidapi-key", rapidApiKey)
      .addHeader("Cookie", "__cf_bm=DrYHGA5qeVCMuf9Hp9W_ujQ6B6xNn3s.EC1UMhyqL4A-1737785120-1.0.1.1-aC.JBL7QOTlNF1TF6MjjpK75HRBMcOkK.V.8Aw7inJWw0MdvaBSQScA5ixAxiUPCrU0d415lorkxuhmzpdc7qg")
      .build();
    Response response = client.newCall(request).execute();
    String content = response.body().string();
    List<NestParamData> nestParamsList = nestParamLst
      .stream().map(s -> new NestParamData().setNestString(s))
      .collect(Collectors.toList());
    JsonNestedRequest nestedRequest = new JsonNestedRequest();
    nestedRequest.setNestParams(nestParamsList);
    nestedRequest.setJson(content);
    Map<String, Object> data = new HashMap<>();
    JsonOutput output = convertJsonService.convertJsonToList(nestedRequest);
    data.put("data", output.getData());
    return data;
  }

  public Map<String, Object> getRapidMetaDataResponse(String endpoint, String slugName, String params, String nestParams) throws IOException, ApiException {
    Type listType = new TypeToken<List<String>>() {
    }.getType();
    List<String> nestParamLst = gson.fromJson(nestParams, listType);
    OkHttpClient client = new OkHttpClient().newBuilder()
      .build();
    String url = String.format("https://%s.p.rapidapi.com/%s?%s", slugName, endpoint, params);
    Request request = new Request.Builder()
      .url(url)
      .addHeader("x-rapidapi-key", rapidApiKey)
      .addHeader("Cookie", "__cf_bm=DrYHGA5qeVCMuf9Hp9W_ujQ6B6xNn3s.EC1UMhyqL4A-1737785120-1.0.1.1-aC.JBL7QOTlNF1TF6MjjpK75HRBMcOkK.V.8Aw7inJWw0MdvaBSQScA5ixAxiUPCrU0d415lorkxuhmzpdc7qg")
      .build();
    Response response = client.newCall(request).execute();
    String content = response.body().string();
    List<NestParamData> nestParamsList = nestParamLst
      .stream().map(s -> new NestParamData().setNestString(s))
      .collect(Collectors.toList());
    JsonNestedRequest nestedRequest = new JsonNestedRequest();
    nestedRequest.setNestParams(nestParamsList);
    nestedRequest.setJson(content);
    return convertJsonService.convertNestParamsToColum(nestedRequest);
  }

  public List<RapidCategoryData> getRapidCategories(CategoryRequest categoryRequest) throws IOException {
    OkHttpClient client = new OkHttpClient().newBuilder()
      .build();
    MediaType mediaType = MediaType.parse("application/json");
    RapidCategoryGraphQLRequest rapidGraphQLRequest = new RapidCategoryGraphQLRequest();
    rapidGraphQLRequest.setVariables(categoryRequest);
    String test = encodeCamelCase(rapidGraphQLRequest);
    RequestBody body = RequestBody.create(mediaType, encodeCamelCase(rapidGraphQLRequest));
    Request request = new Request.Builder()
      .url("https://rapidapi.com/gateway/graphql")
      .method("POST", body)
      .addHeader("accept", "*/*")
      .addHeader("accept-language", "en-US,en;q=0.9")
      .addHeader("content-type", "application/json")
      .addHeader("cookie", "ajs_anonymous_id=719895AD-3B42-4B30-A633-71560842F8B3; _csrf=hDHQl5_QOOsCBEVuFmPrKlAz; __variation__FFNewModalExperiment=0.72; __variation__FFPostSignupModalMarketplace=0.95; __variation__FFNewHero=0.8; __variation__FFNewOrgCreatePage=0.23; __variation__FFAskCompanyInfo=0.35; __variation__FFEmbedTeamsVideo=0.99; __variation__FFTeamsLandingPageOrgButtonText=0.87; __variation__FFOrgCreationWithUsersInvitaions=0.4; __variation__FFApiPlaygroundABTest=0.03; __variation__FF_SearchInput_PlaceHolder=0.44; __variation__FFSubscribeModalDirectNavToPricing=0.54; __variation__FFNewPricingPage=0.86; __variation__FFPricingPaymentsAdminsInvite=0.29; __variation__FFPricingPersonalNoOrg=0.64; __variation__FFTryItFreeBottomMPTeamsPage=0.39; __variation__FFFastSubscribeToFreePlanOnMarketplace=0.25; __variation__FFNewPaymentPage=0.87; __variation__FFNewMarketplaceHomepageContent=0.13; __variation__FFAPILimitModalExperiment=0.85; jwt_auth=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTAxMDIyNjcsIm1hc2hhcGVJZCI6IjY3OGZhNjQ5ZTQ1MTBhMDAxODE1N2JiYyIsIm9yaWdpbl9zaXRlIjoiZGVmYXVsdCIsImlzTmV3VXNlciI6dHJ1ZSwiaXNBdXRoZW50aWNhdGVkQnlTU08iOnRydWUsImVtYWlsIjoiZG9kaW5oaG9hbmdobUBnbWFpbC5jb20iLCJ3YXNTZXNzaW9uSXNzdWVkIjp0cnVlLCJpYXQiOjE3Mzc0Njc0NjUsImV4cCI6MTc0NTI0MzQ2NX0.dQaNXuxMdoPbFiShGT53dnpRflHZh19k44F5sqWu-SA; connect.sid=s%3AOgC7qrqgT6Bp3-xq1kf_Jl04gdtiYRsl.VMWsNNZQqqhCgPyYuqjd9zxFdppfvfI0ZnrkrPbAxwE; rapidapi-context={%22entityId%22:%2210102267%22%2C%22type%22:%22User%22}; __stripe_mid=1f5e62e7-3db3-4277-9061-7a06377620f9ad374a; __cflb=02DiuHPSNb326nZUQB6NoY4qqsJaLefLR1K6PCePHrR7a; __stripe_sid=d8c399cd-28da-4ee3-9e32-1bb01bfeadf2df6e3b; theme=light; cf_clearance=bwi.OmClOuQ6Ax3gB9HOPk17cyqMA7HVFZjjY_Eb6qY-1737767080-1.2.1.1-Xx8_wGjosH.ZosfOZkNW6_0Qis4heMpn74Mbl2J.9UdkP_8pGyXGEsHb3RLPhpx41t5BW6Imf3ZWSrZXrOjCjDZuw.pV0ngAhX_AXY1ncVle93bozDv7RTW.d.hsT2zRr0W8K_lr2naZYoARkDbdS7i3xP262m5GRBJsuIztrRo9kF9n2B8QE5Ui4B3x09RO1hb6L4vJZDSPC87nvr8zL0PD9WAGj6uX8jX_3zMqcTAXpXit4K7lK_viPJlND6yAVJFfLPDOx8URBBQ7CzDM8WBERaJ_phX_Nh2P.yIychM; __cf_bm=oJfj3mAzKCwBuPPWQwbiTdtbzjz3cOWb.mjJynFUCIM-1737767144-1.0.1.1-bFeUpfewbC81QS2Zh4YqXyqnvKu.LEP.XklgDb_ddtvMNlFw56ilqIgluDKlO90O06ESOv99BTNte45VXLbFHA; __cf_bm=21qDgNBRQhQ1floTYwlm3nKSW1QcA8ehICwIM.MyuAc-1737773934-1.0.1.1-7rCotRkewOT6Ezd1dlAbqEO1pZgkjeLflahEVbwz7mHM5Szp_X4szxlf0CbdvjZo0QJjAPouoQB9h.AhPUppIw; __cflb=02DiuHPSNb326nZUQB6NoY4qqsJaLefLQ1oCkaze5vuwJ; ajs_anonymous_id=B6195137-7F4A-47F1-BDF6-3F960A50EE08; connect.sid=s%3AOgC7qrqgT6Bp3-xq1kf_Jl04gdtiYRsl.VMWsNNZQqqhCgPyYuqjd9zxFdppfvfI0ZnrkrPbAxwE")
      .addHeader("csrf-token", "0KKu20xO-sZxGYOQhj1tZJ-htBPKts2FuRac")
      .addHeader("rapid-client", "hub-service")
      .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
      .addHeader("sec-ch-ua-mobile", "?0")
      .addHeader("sec-ch-ua-platform", "\"Linux\"")
      .addHeader("sec-fetch-dest", "empty")
      .addHeader("sec-fetch-mode", "cors")
      .addHeader("sec-fetch-site", "same-origin")
      .addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
      .build();
    Response response = client.newCall(request).execute();
    ResponseBody json = response.body();
    String content = json.string();
    JsonArray jsonArray = new JsonObject(content).getJsonObject("data")
      .getJsonObject("products")
      .getJsonArray("nodes");
    List<RapidCategoryData> rapidCategoryDataList = new ArrayList<>();
    List<RapidCategoryData> finalRapidCategoryDataList = rapidCategoryDataList;
    jsonArray.forEach(e -> {
      JsonObject jsonObject = new JsonObject(encode(e)).getJsonObject("map");
      RapidCategoryData rapidCategoryData = new RapidCategoryData();
      rapidCategoryData.setName(jsonObject.getString("name"));
      rapidCategoryData.setApiSlug(jsonObject.getString("slugifiedName"));
      JsonObject user = jsonObject.getJsonObject("user");
      rapidCategoryData.setApiOwnerSlug(user.getString("slugifiedName"));
      RapidApiVersionGraphQLRequest rapidApiVersionGraphQLRequest = new RapidApiVersionGraphQLRequest();
      rapidApiVersionGraphQLRequest.setVariables(rapidCategoryData);
      String apiVersionKey = null;
      try {
        apiVersionKey = getApiVersionId(rapidApiVersionGraphQLRequest);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      rapidCategoryData.setApiVersionId(apiVersionKey);
      List<String> endPoints = new ArrayList<>();
      try {
        endPoints = getEndpoints(apiVersionKey);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      rapidCategoryData.setEndPoints(endPoints);
      finalRapidCategoryDataList.add(rapidCategoryData);
    });
    return finalRapidCategoryDataList;
  }

  private String getApiVersionId(RapidApiVersionGraphQLRequest rapidApiVersionGraphQLRequest) throws IOException {
    OkHttpClient client = new OkHttpClient().newBuilder()
      .build();
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(mediaType, encodeCamelCase(rapidApiVersionGraphQLRequest));
    Request request = new Request.Builder()
      .url("https://rapidapi.com/gateway/graphql")
      .method("POST", body)
      .addHeader("accept", "*/*")
      .addHeader("accept-language", "en-US,en;q=0.9")
      .addHeader("content-type", "application/json")
      .addHeader("cookie", "ajs_anonymous_id=719895AD-3B42-4B30-A633-71560842F8B3; _csrf=hDHQl5_QOOsCBEVuFmPrKlAz; __variation__FFNewModalExperiment=0.72; __variation__FFPostSignupModalMarketplace=0.95; __variation__FFNewHero=0.8; __variation__FFNewOrgCreatePage=0.23; __variation__FFAskCompanyInfo=0.35; __variation__FFEmbedTeamsVideo=0.99; __variation__FFTeamsLandingPageOrgButtonText=0.87; __variation__FFOrgCreationWithUsersInvitaions=0.4; __variation__FFApiPlaygroundABTest=0.03; __variation__FF_SearchInput_PlaceHolder=0.44; __variation__FFSubscribeModalDirectNavToPricing=0.54; __variation__FFNewPricingPage=0.86; __variation__FFPricingPaymentsAdminsInvite=0.29; __variation__FFPricingPersonalNoOrg=0.64; __variation__FFTryItFreeBottomMPTeamsPage=0.39; __variation__FFFastSubscribeToFreePlanOnMarketplace=0.25; __variation__FFNewPaymentPage=0.87; __variation__FFNewMarketplaceHomepageContent=0.13; __variation__FFAPILimitModalExperiment=0.85; jwt_auth=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTAxMDIyNjcsIm1hc2hhcGVJZCI6IjY3OGZhNjQ5ZTQ1MTBhMDAxODE1N2JiYyIsIm9yaWdpbl9zaXRlIjoiZGVmYXVsdCIsImlzTmV3VXNlciI6dHJ1ZSwiaXNBdXRoZW50aWNhdGVkQnlTU08iOnRydWUsImVtYWlsIjoiZG9kaW5oaG9hbmdobUBnbWFpbC5jb20iLCJ3YXNTZXNzaW9uSXNzdWVkIjp0cnVlLCJpYXQiOjE3Mzc0Njc0NjUsImV4cCI6MTc0NTI0MzQ2NX0.dQaNXuxMdoPbFiShGT53dnpRflHZh19k44F5sqWu-SA; connect.sid=s%3AOgC7qrqgT6Bp3-xq1kf_Jl04gdtiYRsl.VMWsNNZQqqhCgPyYuqjd9zxFdppfvfI0ZnrkrPbAxwE; rapidapi-context={%22entityId%22:%2210102267%22%2C%22type%22:%22User%22}; __stripe_mid=1f5e62e7-3db3-4277-9061-7a06377620f9ad374a; __cflb=02DiuHPSNb326nZUQB6NoY4qqsJaLefLR1K6PCePHrR7a; __stripe_sid=d8c399cd-28da-4ee3-9e32-1bb01bfeadf2df6e3b; theme=light; cf_clearance=bwi.OmClOuQ6Ax3gB9HOPk17cyqMA7HVFZjjY_Eb6qY-1737767080-1.2.1.1-Xx8_wGjosH.ZosfOZkNW6_0Qis4heMpn74Mbl2J.9UdkP_8pGyXGEsHb3RLPhpx41t5BW6Imf3ZWSrZXrOjCjDZuw.pV0ngAhX_AXY1ncVle93bozDv7RTW.d.hsT2zRr0W8K_lr2naZYoARkDbdS7i3xP262m5GRBJsuIztrRo9kF9n2B8QE5Ui4B3x09RO1hb6L4vJZDSPC87nvr8zL0PD9WAGj6uX8jX_3zMqcTAXpXit4K7lK_viPJlND6yAVJFfLPDOx8URBBQ7CzDM8WBERaJ_phX_Nh2P.yIychM; __cf_bm=YF.XttNgdFlhGAnzohj1m_MY9_Bbmcc2Nn8VardCgng-1737769010-1.0.1.1-7WSKDPyhhz.Lwl1XaUDYAaOLzjB3x5p0kz85WWk07Ta8PjqTrbfshwIvxV.vtsS7nHCC64GyvR5P5J3L4xh0zw; __cf_bm=iIlia4pFPMzEop3olJUVq4JcgfqM0eGfUqZ23F7D4TY-1737769176-1.0.1.1-snsvvZclb.ykv6r26DyrftRI2Z8sEuhgSVzEp20crVZw1TmoehbncWXE6Fm.VjOq6H7a0p3ItW.u.U5ZG111zQ; connect.sid=s%3AOgC7qrqgT6Bp3-xq1kf_Jl04gdtiYRsl.VMWsNNZQqqhCgPyYuqjd9zxFdppfvfI0ZnrkrPbAxwE")
      .addHeader("csrf-token", "0KKu20xO-sZxGYOQhj1tZJ-htBPKts2FuRac")
      .addHeader("origin", "https://rapidapi.com")
      .addHeader("priority", "u=1, i")
      .addHeader("rapid-client", "hub-service")
      .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
      .addHeader("sec-ch-ua-mobile", "?0")
      .addHeader("sec-ch-ua-platform", "\"Linux\"")
      .addHeader("sec-fetch-dest", "empty")
      .addHeader("sec-fetch-mode", "cors")
      .addHeader("sec-fetch-site", "same-origin")
      .addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
      .build();
    Response response = client.newCall(request).execute();
    ResponseBody json = response.body();
    String content = json.string();
    String apiVersionId = new JsonObject(content).getJsonObject("data")
      .getJsonObject("apiBySlugifiedNameAndOwnerName")
      .getJsonObject("version")
      .getString("id");
    return apiVersionId;
  }

  private List<String> getEndpoints(String apiVersionId) throws IOException {
    OkHttpClient client = new OkHttpClient().newBuilder()
      .build();
    MediaType mediaType = MediaType.parse("application/json");
    Map<String, String> variables = new HashMap<>();
    variables.put("apiVersionId", apiVersionId);
    RapidEndpointGraphQLRequest rapidEndpointGraphQLRequest = new RapidEndpointGraphQLRequest();
    rapidEndpointGraphQLRequest.setVariables(variables);
    RequestBody body = RequestBody.create(mediaType, encodeCamelCase(rapidEndpointGraphQLRequest));
    Request request = new Request.Builder()
      .url("https://rapidapi.com/gateway/graphql")
      .method("POST", body)
      .addHeader("accept", "*/*")
      .addHeader("accept-language", "en-US,en;q=0.9")
      .addHeader("content-type", "application/json")
      .addHeader("cookie", "ajs_anonymous_id=719895AD-3B42-4B30-A633-71560842F8B3; _csrf=hDHQl5_QOOsCBEVuFmPrKlAz; __variation__FFNewModalExperiment=0.72; __variation__FFPostSignupModalMarketplace=0.95; __variation__FFNewHero=0.8; __variation__FFNewOrgCreatePage=0.23; __variation__FFAskCompanyInfo=0.35; __variation__FFEmbedTeamsVideo=0.99; __variation__FFTeamsLandingPageOrgButtonText=0.87; __variation__FFOrgCreationWithUsersInvitaions=0.4; __variation__FFApiPlaygroundABTest=0.03; __variation__FF_SearchInput_PlaceHolder=0.44; __variation__FFSubscribeModalDirectNavToPricing=0.54; __variation__FFNewPricingPage=0.86; __variation__FFPricingPaymentsAdminsInvite=0.29; __variation__FFPricingPersonalNoOrg=0.64; __variation__FFTryItFreeBottomMPTeamsPage=0.39; __variation__FFFastSubscribeToFreePlanOnMarketplace=0.25; __variation__FFNewPaymentPage=0.87; __variation__FFNewMarketplaceHomepageContent=0.13; __variation__FFAPILimitModalExperiment=0.85; jwt_auth=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTAxMDIyNjcsIm1hc2hhcGVJZCI6IjY3OGZhNjQ5ZTQ1MTBhMDAxODE1N2JiYyIsIm9yaWdpbl9zaXRlIjoiZGVmYXVsdCIsImlzTmV3VXNlciI6dHJ1ZSwiaXNBdXRoZW50aWNhdGVkQnlTU08iOnRydWUsImVtYWlsIjoiZG9kaW5oaG9hbmdobUBnbWFpbC5jb20iLCJ3YXNTZXNzaW9uSXNzdWVkIjp0cnVlLCJpYXQiOjE3Mzc0Njc0NjUsImV4cCI6MTc0NTI0MzQ2NX0.dQaNXuxMdoPbFiShGT53dnpRflHZh19k44F5sqWu-SA; connect.sid=s%3AOgC7qrqgT6Bp3-xq1kf_Jl04gdtiYRsl.VMWsNNZQqqhCgPyYuqjd9zxFdppfvfI0ZnrkrPbAxwE; rapidapi-context={%22entityId%22:%2210102267%22%2C%22type%22:%22User%22}; __stripe_mid=1f5e62e7-3db3-4277-9061-7a06377620f9ad374a; __cflb=02DiuHPSNb326nZUQB6NoY4qqsJaLefLR1K6PCePHrR7a; __stripe_sid=d8c399cd-28da-4ee3-9e32-1bb01bfeadf2df6e3b; theme=light; cf_clearance=bwi.OmClOuQ6Ax3gB9HOPk17cyqMA7HVFZjjY_Eb6qY-1737767080-1.2.1.1-Xx8_wGjosH.ZosfOZkNW6_0Qis4heMpn74Mbl2J.9UdkP_8pGyXGEsHb3RLPhpx41t5BW6Imf3ZWSrZXrOjCjDZuw.pV0ngAhX_AXY1ncVle93bozDv7RTW.d.hsT2zRr0W8K_lr2naZYoARkDbdS7i3xP262m5GRBJsuIztrRo9kF9n2B8QE5Ui4B3x09RO1hb6L4vJZDSPC87nvr8zL0PD9WAGj6uX8jX_3zMqcTAXpXit4K7lK_viPJlND6yAVJFfLPDOx8URBBQ7CzDM8WBERaJ_phX_Nh2P.yIychM; __cf_bm=ZiEQD1hhDWlNX9_R_bI350.QwSoeuOqYP4FIOdCm9sE-1737768047-1.0.1.1-IbbvZskZ6d7VdRck1hGfM8rzAmu.2au7S.0sRBUZz5bPXrIQUIDMl7Ngl6KDYQTbNoqhrDDD85ZIE2QJzu4RIQ; __cf_bm=kMIHZ6rG8Z4VO5i6IWnkfUaJeQ3sqQ4uUOiqi8sg3KA-1737782137-1.0.1.1-29HsdIyxOnSvJGlZxGIpjkWPpqgCctGAQ61Lz2kv7b7_zb.qVy9pIW0NYKJIjj0Ko.KVFZvCkRr7QCIvWYJN.g; __cflb=02DiuHPSNb326nZUQB6NoY4qqsJaLefLQ1oCkaze5vuwJ; ajs_anonymous_id=B6195137-7F4A-47F1-BDF6-3F960A50EE08; connect.sid=s%3AOgC7qrqgT6Bp3-xq1kf_Jl04gdtiYRsl.VMWsNNZQqqhCgPyYuqjd9zxFdppfvfI0ZnrkrPbAxwE")
      .addHeader("csrf-token", "0KKu20xO-sZxGYOQhj1tZJ-htBPKts2FuRac")
      .addHeader("origin", "https://rapidapi.com")
      .addHeader("priority", "u=1, i")
      .addHeader("rapid-client", "hub-service")
      .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
      .addHeader("sec-ch-ua-mobile", "?0")
      .addHeader("sec-ch-ua-platform", "\"Linux\"")
      .addHeader("sec-fetch-dest", "empty")
      .addHeader("sec-fetch-mode", "cors")
      .addHeader("sec-fetch-site", "same-origin")
      .addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
      .build();
    Response response = client.newCall(request).execute();
    ResponseBody json = response.body();
    String content = json.string();
    JsonArray jsonArray = new JsonObject(content).getJsonObject("data")
      .getJsonObject("apiVersion")
      .getJsonArray("endpoints");
    List<String> endpoints = new ArrayList<>();
    jsonArray.forEach(e -> {
      String endpoint = new JsonObject(encodeCamelCase(e)).getJsonObject("map")
        .getString("route");
      String method = new JsonObject(encodeCamelCase(e)).getJsonObject("map")
        .getString("method");
      if (method.equalsIgnoreCase("GET")) {
        endpoints.add(endpoint);
      }
    });
    return endpoints;
  }
}
