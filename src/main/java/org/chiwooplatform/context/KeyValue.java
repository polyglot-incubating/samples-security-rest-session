package org.chiwooplatform.context;

public class KeyValue {

  private final String key;

  private final Object value;

  public KeyValue(String key, Object value) {
    super();
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }

}
