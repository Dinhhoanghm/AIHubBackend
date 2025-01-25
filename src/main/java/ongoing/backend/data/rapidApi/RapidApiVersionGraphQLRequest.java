package ongoing.backend.data.rapidApi;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RapidApiVersionGraphQLRequest {
  private String query = "query getApiBySlugAndOwner($apiOwnerSlug: String, $apiSlug: String, $withEndpoints: Boolean! = false) {\n" +
    "  apiBySlugifiedNameAndOwnerName(\n" +
    "    slugifiedName: $apiSlug\n" +
    "    ownerName: $apiOwnerSlug\n" +
    "  ) {\n" +
    "    ...ApiInfo\n" +
    "    createdAt\n" +
    "    status\n" +
    "    longDescription\n" +
    "    apiType\n" +
    "    allowedContext\n" +
    "    isCtxSubscriber\n" +
    "    quality {\n" +
    "      score\n" +
    "    }\n" +
    "    owner {\n" +
    "      id\n" +
    "      name\n" +
    "      slugifiedName\n" +
    "      type\n" +
    "      thumbnail\n" +
    "      username\n" +
    "      parents {\n" +
    "        id\n" +
    "        name\n" +
    "        slugifiedName\n" +
    "        type\n" +
    "        thumbnail\n" +
    "      }\n" +
    "    }\n" +
    "    versions {\n" +
    "      id\n" +
    "      name\n" +
    "      current\n" +
    "      createdAt\n" +
    "      versionStatus\n" +
    "    }\n" +
    "    version {\n" +
    "      endpoints(pagingArgs: {limit: 3}) @include(if: $withEndpoints) {\n" +
    "        ...EndpointInfo\n" +
    "      }\n" +
    "    }\n" +
    "    billingPlans {\n" +
    "      ...BillingPlanInfo\n" +
    "    }\n" +
    "    billingItems {\n" +
    "      id\n" +
    "      name\n" +
    "      description\n" +
    "      type\n" +
    "      allEndpoints\n" +
    "    }\n" +
    "    billingFeatures {\n" +
    "      id\n" +
    "      name\n" +
    "      description\n" +
    "      type\n" +
    "    }\n" +
    "    rating {\n" +
    "      rating\n" +
    "      votes\n" +
    "      bestRating\n" +
    "    }\n" +
    "    activeUserRating {\n" +
    "      rating\n" +
    "    }\n" +
    "    documentation {\n" +
    "      readme {\n" +
    "        text\n" +
    "      }\n" +
    "    }\n" +
    "    termsOfService {\n" +
    "      id\n" +
    "      text\n" +
    "      name\n" +
    "    }\n" +
    "    subscriptionsCount\n" +
    "    websiteUrl\n" +
    "    spotlights {\n" +
    "      apiId\n" +
    "      thumbnailURL\n" +
    "      title\n" +
    "      type\n" +
    "      weight\n" +
    "      description\n" +
    "      id\n" +
    "      published\n" +
    "      spotlightURL\n" +
    "      status\n" +
    "      slugifiedName\n" +
    "    }\n" +
    "  }\n" +
    "}\n" +
    "\n" +
    "fragment ApiInfo on Api {\n" +
    "  __typename\n" +
    "  id\n" +
    "  name\n" +
    "  title\n" +
    "  description\n" +
    "  visibility\n" +
    "  slugifiedName\n" +
    "  pricing\n" +
    "  updatedAt\n" +
    "  category\n" +
    "  thumbnail\n" +
    "  isSavedApi\n" +
    "  categoryId\n" +
    "  apiCategory {\n" +
    "    name\n" +
    "    color\n" +
    "  }\n" +
    "  owner {\n" +
    "    __typename\n" +
    "    id\n" +
    "    name\n" +
    "    slugifiedName\n" +
    "    type\n" +
    "    thumbnail\n" +
    "    parents {\n" +
    "      id\n" +
    "      name\n" +
    "      slugifiedName\n" +
    "      type\n" +
    "      thumbnail\n" +
    "    }\n" +
    "  }\n" +
    "  score {\n" +
    "    avgServiceLevel\n" +
    "    avgLatency\n" +
    "    avgSuccessRate\n" +
    "    popularityScore\n" +
    "  }\n" +
    "  version {\n" +
    "    id\n" +
    "    apiSubType\n" +
    "    tags {\n" +
    "      id\n" +
    "      status\n" +
    "      tagdefinition\n" +
    "      type\n" +
    "      value\n" +
    "    }\n" +
    "  }\n" +
    "}\n" +
    "\n" +
    "fragment EndpointInfo on Endpoint {\n" +
    "  id\n" +
    "  isGraphQL\n" +
    "  route\n" +
    "  method\n" +
    "  name\n" +
    "  description\n" +
    "  apiversion\n" +
    "  group\n" +
    "  index\n" +
    "}\n" +
    "\n" +
    "fragment BillingPlanInfo on BillingPlan {\n" +
    "  id\n" +
    "  name\n" +
    "  recommended\n" +
    "  visibility\n" +
    "  allowedPlanDevelopers {\n" +
    "    userId\n" +
    "  }\n" +
    "  shouldRequestApproval\n" +
    "  requestApprovalQuestion\n" +
    "  hidden\n" +
    "  legalDocumentId\n" +
    "  legalAccountId\n" +
    "  version {\n" +
    "    ...BillingPlanVersionInfo\n" +
    "  }\n" +
    "}\n" +
    "\n" +
    "fragment BillingPlanVersionInfo on BillingPlanVersion {\n" +
    "  id\n" +
    "  name\n" +
    "  period\n" +
    "  option\n" +
    "  price\n" +
    "  current\n" +
    "  billingPlanId: billingplan\n" +
    "  pricing\n" +
    "  localePrice {\n" +
    "    price\n" +
    "    symbol\n" +
    "  }\n" +
    "  billinglimits {\n" +
    "    id\n" +
    "    period\n" +
    "    amount\n" +
    "    unlimited\n" +
    "    overageprice\n" +
    "    overageLocalePrice {\n" +
    "      price\n" +
    "      symbol\n" +
    "    }\n" +
    "    billingitem {\n" +
    "      id\n" +
    "      name\n" +
    "      title\n" +
    "      description\n" +
    "      type\n" +
    "      allEndpoints\n" +
    "    }\n" +
    "    item\n" +
    "    limitType\n" +
    "  }\n" +
    "  enablebillingfeatures {\n" +
    "    id\n" +
    "    billingfeature\n" +
    "    type\n" +
    "    status\n" +
    "    note\n" +
    "    billingFeatureObject {\n" +
    "      id\n" +
    "      name\n" +
    "      description\n" +
    "    }\n" +
    "  }\n" +
    "  rateLimit {\n" +
    "    enabled\n" +
    "    unit\n" +
    "    unitName\n" +
    "    amount\n" +
    "  }\n" +
    "}";
  public RapidCategoryData variables;
}
