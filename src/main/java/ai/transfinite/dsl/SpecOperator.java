package ai.transfinite.dsl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum SpecOperator implements DslParameter {

  INTERVALL("INTERVALL");

  private String operator;

  SpecOperator(String operator) {
    this.operator = operator;
  }

  public static SpecOperator fromValue(String v) {
    try {
      return valueOf(v);
    } catch (IllegalArgumentException e) {
      log.error("Fant ikke operator {}", e.getMessage());
      return null;
    }
  }

  @Override
  public String value() {
    return operator;
  }
}
