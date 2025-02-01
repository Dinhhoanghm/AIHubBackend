package ongoing.backend.data.rapidApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RapidCategoryData {
  private String name;
  private String apiOwnerSlug;
  private String apiSlug;
  private String apiVersionId;
  private List<RapidEndpointData> endPoints;
}
