package ongoing.backend.data.rapidApi;

import lombok.Data;

import java.util.List;

@Data
public class SearchApiWhereInput {
  private String term;
  private List<String> categoryNames;
  private List<String> tags;
}
