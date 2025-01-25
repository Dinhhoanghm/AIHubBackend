package ongoing.backend.data.rapidApi;

import lombok.Data;

@Data
public class RapidCategoryGraphQLRequest {
  private Object query = "query searchApis($searchApiWhereInput: SearchApiWhereInput!, $paginationInput: PaginationInput, $searchApiOrderByInput: SearchApiOrderByInput) {\n" +
    "  products: searchApis(\n" +
    "    where: $searchApiWhereInput\n" +
    "    pagination: $paginationInput\n" +
    "    orderBy: $searchApiOrderByInput\n" +
    "  ) {\n" +
    "    nodes {\n" +
    "      id\n" +
    "      thumbnail\n" +
    "      name\n" +
    "      description\n" +
    "      slugifiedName\n" +
    "      pricing\n" +
    "      updatedAt\n" +
    "      categoryName\n" +
    "      isSavedApi\n" +
    "      title\n" +
    "      visibility\n" +
    "      category: categoryName\n" +
    "      apiCategory {\n" +
    "        name\n" +
    "        color\n" +
    "      }\n" +
    "      score {\n" +
    "        popularityScore\n" +
    "        avgLatency\n" +
    "        avgServiceLevel\n" +
    "        avgSuccessRate\n" +
    "      }\n" +
    "      version {\n" +
    "        tags {\n" +
    "          id\n" +
    "          status\n" +
    "          tagdefinition\n" +
    "          type\n" +
    "          value\n" +
    "        }\n" +
    "      }\n" +
    "      user: User {\n" +
    "        id\n" +
    "        username\n" +
    "        slugifiedName: username\n" +
    "        name\n" +
    "        type\n" +
    "        parents {\n" +
    "          id\n" +
    "          name\n" +
    "          slugifiedName\n" +
    "          type\n" +
    "          thumbnail\n" +
    "        }\n" +
    "      }\n" +
    "    }\n" +
    "    facets {\n" +
    "      category {\n" +
    "        key\n" +
    "        count\n" +
    "      }\n" +
    "    }\n" +
    "    pageInfo {\n" +
    "      endCursor\n" +
    "      hasNextPage\n" +
    "      hasPreviousPage\n" +
    "      startCursor\n" +
    "    }\n" +
    "    total\n" +
    "    queryID\n" +
    "    replicaIndex\n" +
    "  }\n" +
    "}";
  private CategoryRequest variables;
}
