package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.utils.Position;

public class StringLiteral extends UsingPrimitive {
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  private String value;
  private Boolean isWrappedDoubleQuotes;

  public Boolean getIsWrappedDoubleQuotes() {
    return isWrappedDoubleQuotes;
  }

  public StringLiteral(String value, Position position, Boolean isWrappedDoubleQuota) {
    super(position);
    this.value = value;
    this.isWrappedDoubleQuotes = isWrappedDoubleQuota;
  }

  public StringLiteral(
      String value, Position position, String scope, Boolean isWrappedDoubleQuota) {
    super(position, scope);
    this.value = value;
    this.isWrappedDoubleQuotes = isWrappedDoubleQuota;
  }

  public StringLiteral() {}

  @Override
  public String toString() {
    return "\"" + value + "\"";
  }
}
