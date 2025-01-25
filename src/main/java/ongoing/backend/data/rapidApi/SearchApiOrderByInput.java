package ongoing.backend.data.rapidApi;

import lombok.Data;

import java.util.List;

@Data
public class SearchApiOrderByInput {
  private List<SortFields> sortingFields;
}
