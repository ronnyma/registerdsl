package ai.transfinite.dsl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Value implements DslPart {

  public Value(Boolean value) {
    this.value = String.valueOf(value);
  }

  private String value;

  public void setValue(String value) {
    this.value = value;
  }

  public void apple(String value) {
    setValue(value);
  }

  @Override
  public String value() {
    return value;
  }
}
