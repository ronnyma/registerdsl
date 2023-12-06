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

  public final String soekeparameter;
  public final String verdi;
  public final String tilVerdi;
  public final Operator operator;
  public final String erGjeldende;

  public RegisterQuery(Builder builder) {
    this.soekeparameter = REGISTER_PATH + builder.soekeparameter;
    this.verdi = builder.verdi;
    this.operator = builder.operator;
    this.tilVerdi = builder.tilVerdi;
    this.erGjeldende = builder.erGjeldende;
  }

  public Optional<String> getTilVerdi() {
    return Optional.ofNullable(tilVerdi);
  }

  public String getErGjeldende() {
    return Optional.ofNullable(erGjeldende).orElse("true");
  }


  public String erGjeldendePath() {
    String[] split = soekeparameter.split("\\.");
    return REGISTER_PATH + split[0] + ".erGjeldende";
  }

  public static class Builder {

    private String soekeparameter;
    private String verdi;
    private String tilVerdi;
    private Operator operator = Operator.LIK;
    private Clause clause = Clause.HAR;
    private String erGjeldende;

    public Builder() {
    }

    public Builder withSoekeparameter(String soekeparameter) {
      this.soekeparameter = soekeparameter;
      return this;
    }

    public Builder withVerdi(String verdi) {
      this.verdi = verdi;
      return this;
    }

    public Builder withGjeldende(String verdi) {
      this.erGjeldende = verdi;
      return this;
    }

    public Builder nothing() {
      return this;
    }

    public Builder withTilVerdi(String tilVerdi) {
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

    public Builder withErGjeldende(String erGjeldende) {
      this.erGjeldende = erGjeldende;
      return this;
    }

    public RegisterQuery build() {
      return new RegisterQuery(this);
    }
  }
}