package ai.transfinite.elastic;


import static co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode.Multiply;
import static java.util.Collections.singletonList;

import ai.transfinite.dsl.Operator;
import ai.transfinite.dsl.RegisterQuery;
import ai.transfinite.dsl.SpecOperator;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class QueryGenerator {

  public static final String DELIMITER = ".";
  private static final String EMPTY = "";
  private static final String ER_GJELDENDE = ".erGjeldende";

  private static Map<String, Long> TERMS_TO_CUT_THE_SUFFIX;


  private static List<QueryVariant> nestedMatchQueryBuilder(String field, String value, Boolean erGjeldende) {
    List<QueryVariant> queryVariants = new ArrayList<>();

    switch (value) {
      case String v -> queryVariants.add(matchQueryBuilder(field, v).build());
      case null -> queryVariants.add(existsQueryBuilder(field).build());
    }

    if (erGjeldende != null) {
      queryVariants.add(matchQueryBuilder(addValidSuffix(field), erGjeldende.toString()).build());
    }
    List<Query> list = queryVariants.stream().map(Query::new).toList();

    return singletonList(new NestedQuery.Builder().path(getPath(field))
        .query(toQuery(new BoolQuery.Builder().must(list).build())).build());
  }

  private static List<QueryVariant> nestedRangedQueryBuilder(String field, String fromValue, String toValue, Boolean erGjeldende) {
    List<QueryVariant> build = List.of(rangeQueryBuilder(field, fromValue, toValue).build(),
        new TermQuery.Builder().field(addValidSuffix(field)).value(erGjeldende).build());
    List<Query> list = build.stream().map(Query::new).toList();

    return singletonList(new NestedQuery.Builder().path(getPath(field))
        .query(toQuery(new BoolQuery.Builder().must(list).build())).build());
  }

  private static ExistsQuery.Builder existsQueryBuilder(String fieldname) {
    return new ExistsQuery.Builder().field(fieldname);
  }

  private static MatchQuery.Builder matchQueryBuilder(String field, String value) {
    return new MatchQuery.Builder().field(field).query(value);
  }

  private static RangeQuery.Builder rangeQueryBuilder(String field, String fromValue, String toValue) {
    return new RangeQuery.Builder().field(field).from(fromValue).to(toValue);
  }

  @SafeVarargs
  public static List<Query> buildMatchQuery(List<QueryVariant>... builder) {
    return Stream.of(builder)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(QueryGenerator::toQuery)
        .toList();
  }

  public static <T extends QueryVariant> Query toQuery(T specQuery) {
    return new Query(specQuery);
  }

  private static String getPath(String fieldName) {
    return createSearchTerm(fieldName);
  }

  private static String addValidSuffix(String fieldName) {
    return createSearchTerm(fieldName).concat(ER_GJELDENDE);
  }

  private static String createSearchTerm(String fieldName) {
    String fieldWithoutKeywordSuffix = removeKeywordSuffix(fieldName);
    String[] items = fieldWithoutKeywordSuffix.split("\\.");

    return Arrays.stream(items)
        .limit(items.length - calculateCutoff(fieldName))
        .collect(Collectors.joining(DELIMITER));
  }

  private static String removeKeywordSuffix(String fieldName) {
    String[] tokens = fieldName.split("[.]");
    int maxSize = tokens.length - 1;

    return "keyword".equals(tokens[maxSize]) ?
        Stream.of(tokens).limit(maxSize)
            .collect(Collectors.joining(DELIMITER))
        : fieldName;
  }

  public static SearchRequest.Builder generate(List<RegisterQuery> query) {
    List<Query> withList = query.stream()
        .filter(q -> q.operator == Operator.LIK)
        .map(q -> nestedMatchQueryBuilder(q.searchParameter, q.value, q.isValid))
        .map(QueryGenerator::buildMatchQuery)
        .flatMap(Collection::stream)
        .toList();

    List<Query> withoutList = query.stream()
        .filter(q -> q.operator == Operator.ULIK)
        .map(q -> nestedMatchQueryBuilder(q.searchParameter, q.value, q.isValid))
        .map(QueryGenerator::buildMatchQuery)
        .flatMap(Collection::stream)
        .toList();

    List<Query> rangeList =
        query.stream()
            .filter(q -> q.operator == SpecOperator.INTERVALL)
            .map(q -> nestedRangedQueryBuilder(q.searchParameter, q.value, q.tilVerdi, q.isValid))
            .map(QueryGenerator::buildMatchQuery)
            .flatMap(Collection::stream)
            .toList();

    Query personQuery = new Query.Builder()
        .bool(q -> q
            .must(withList)
            .must(rangeList)
            .mustNot(withoutList))
        .build();

    //Workaround for ElasticSearch 7 and `must_not` which runs in filter context. A ranom hit must be selected
    int maxHits = 10;
    if (!withoutList.isEmpty() && withList.isEmpty()) {
      maxHits = 1000;
    }

    return new SearchRequest.Builder()
        .query(q -> q.functionScore(f -> f.boostMode(Multiply)
            .functions(fs -> fs.randomScore(builder -> builder))
            .boost(5.0F).query(personQuery))).size(maxHits);
  }

  private static Long calculateCutoff(String term) {
    List<String> stopList = Arrays.asList("document.personData.personIRegisteret".split("[.]"));
    String[] tokens = term.split("[.]");
    return Arrays.stream(tokens).filter(t -> !stopList.contains(t)).count() - 1;
  }
}
