package ai.transfinite.dsl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Clause implements DslPart {

  ERGJELDENDE("ERGJELDENDE"),
  HAR("HAR"),
  HARIKKE("HARIKKE");

  private String clause;

  Clause(String clause) {
    this.clause = clause;
  }

  public static Clause fromValue(String v) {
    try {
      return valueOf(v);
    } catch (IllegalArgumentException e) {
      log.error("Fant ikke operator {}", e.getMessage());
      return null;
    }
  }

  @Override
  public String value() {
    return clause;
  }
}
