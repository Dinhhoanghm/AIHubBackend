package ongoing.backend.data.rapidApi;

import lombok.Data;

@Data
public class CategoryRequest {
  private PaginationInput paginationInput;
  private SearchApiOrderByInput searchApiOrderByInput;
  private SearchApiWhereInput searchApiWhereInput;
}
