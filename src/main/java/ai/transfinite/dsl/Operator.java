package ai.transfinite.dsl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Operator implements DslPart {
  FRA("FRA"),
  TIL("TIL"),
  LIK("LIK"),
  ULIK("ULIK");

  private String operator;

  Operator(String operator) {
    this.operator = operator;
  }

  public static Operator fromValue(String v) {
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
