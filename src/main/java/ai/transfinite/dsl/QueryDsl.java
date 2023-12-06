package ai.transfinite.dsl;



import static ai.transfinite.dsl.Clause.ERGJELDENDE;
import static ai.transfinite.dsl.Clause.HAR;
import static ai.transfinite.dsl.Clause.HARIKKE;
import static ai.transfinite.dsl.Operator.FRA;
import static ai.transfinite.dsl.Operator.LIK;
import static ai.transfinite.dsl.Operator.TIL;
import static ai.transfinite.dsl.Operator.ULIK;
import static ai.transfinite.dsl.SpecOperator.INTERVALL;
import static java.util.Optional.empty;

import ai.transfinite.dsl.RegisterQuery.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class QueryDsl {

  private final List<RegisterQuery> registerQuery = new ArrayList<>();
  private boolean fraErSatt;

  private QueryDsl(Dsl builder) {
    List<Builder> registerQueryBuilder = new ArrayList<>();
    Optional<Builder> builder1 = Optional.of(new Builder()); //TODO: dette er en code-smell og må fikses
    for (DslPart s : builder.getQuery()) {
      Optional<Builder> returnBuilder = populate(s, builder1.get());
      if (returnBuilder.isPresent()) {
        registerQueryBuilder.add(returnBuilder.get());
        builder1 = returnBuilder;
      }
    }
    for (Builder b : registerQueryBuilder) {
      registerQuery.add(b.build());
    }
  }

  private Optional<Builder> populate(DslPart dslPart, Builder queryBuilder) {
    switch (dslPart) {
      case Terms t -> {
        Builder builder = new Builder();
        builder.withSoekeparameter(t.value());
        return Optional.of(builder);
      }
      case Clause c -> {
        queryBuilder.withClause(c.value());
        return empty();
      }
      case Operator o -> {
        queryBuilder.withOperator(o.value());
        return empty();
      }
      case SpecOperator ignored -> {
        fraErSatt = true;
        return empty();
      }
      case Value v -> {
        if (Set.of("true", "false").contains(v.value())) {
          queryBuilder.withGjeldende(v.value());
        } else {
          if (!fraErSatt) {
            queryBuilder.withVerdi(v.value());
          } else {
            fraErSatt = false;
            queryBuilder.withTilVerdi(v.value());
          }
        }
        return empty();
      }
      default -> {
        return empty();
      }
    }
  }

  public static class Dsl {

    private final List<DslPart> query = new ArrayList<>();

    private static boolean sanityCheck(List<DslPart> dslParts) {
      StringBuffer stringBuffer = new StringBuffer();
      dslParts.forEach(p -> {
        String firstLetter = p.getClass().getSimpleName().substring(0, 1);

        stringBuffer.append(firstLetter);
      });
      log.info("Signatur: {}", stringBuffer);

      return true;
    }

    public List<DslPart> getQuery() {
      sanityCheck(query);
      return query;
    }

    public Dsl har() {
      this.query.add(HAR);
      return this;
    }    public Dsl noekkel(Terms key) {
      this.query.add(key);
      this.query.add(HAR);
      return this;
    }

    public Dsl harIkke() {
      this.query.add(HARIKKE);
      return this;
    }

    public Dsl lik() {
      this.query.add(LIK);
      return this;
    }

    public Dsl verdi(String value) {
      this.query.add(new Value(value));
      return this;
    }

    public Dsl ulik() {
      this.query.add(ULIK);
      return this;
    }

    public Dsl interval() {
      this.query.add(INTERVALL);
      return this;
    }

    public Dsl fra() { //TODO: fra/til blir ikke håndtert
      this.query.add(INTERVALL);
      this.query.add(FRA);
      return this;
    }

    public Dsl til() {
      this.query.add(TIL);
      return this;
    }

    public Dsl gjeldende() {
      this.query.add(ERGJELDENDE);
      this.query.add(new Value(true));
      return this;
    }

    public QueryDsl generer() {
      return new QueryDsl(this);
    }
  }
}
