package ai.transfinite.dsl;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import lombok.ToString;

@ToString
@JsonDeserialize(
    builder = RegisterQuery.Builder.class
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterQuery {

  private static final String REGISTER_PATH = "document.personData.personIRegisteret.";

  public final String searchParameter;
  public final String value;
  public final String tilVerdi;
  public final DslParameter operator;
  public final Boolean isValid;

  public RegisterQuery(Builder builder) {
    this.searchParameter = REGISTER_PATH + builder.soekeparameter;
    this.value = builder.verdi;
    this.operator = builder.operator;
    this.tilVerdi = builder.tilVerdi;
    this.isValid = Boolean.valueOf(builder.erGjeldende);
  }

  public Optional<String> getTilVerdi() {
    return Optional.ofNullable(tilVerdi);
  }

  public Boolean getIsValid() {
    return Optional.ofNullable(isValid).orElse(true);
  }


  public String erGjeldendePath() {
    String[] split = searchParameter.split("\\.");
    return REGISTER_PATH + split[0] + ".erGjeldende";
  }

  public static class Builder {

    private String soekeparameter;
    private String verdi;
    private String tilVerdi;
    private Operator operator = Operator.LIK;
    private Clause clause = Clause.HAR;
    private String erGjeldende;


    public Builder withSearchParameter(String soekeparameter) {
      this.soekeparameter = soekeparameter;
      return this;
    }

    public Builder withValue(String verdi) {
      this.verdi = verdi;
      return this;
    }

    public Builder withIsValid(String verdi) {
      this.erGjeldende = verdi;
      return this;
    }

    public Builder nothing() {
      return this;
    }

    public Builder withToValue(String tilVerdi) {
      this.tilVerdi = tilVerdi;
      return this;
    }

    public Builder withOperator(String operator) {
      this.operator = Operator.valueOf(operator);
      return this;
    }

    public Builder withClause(String clause) {
      this.clause = Clause.valueOf(clause);
      return this;
    }


    public RegisterQuery build() {
      return new RegisterQuery(this);
    }
  }
}