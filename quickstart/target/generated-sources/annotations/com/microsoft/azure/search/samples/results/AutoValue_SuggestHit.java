

package com.microsoft.azure.search.samples.results;

import java.util.Map;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_SuggestHit extends SuggestHit {

  private final String text;

  private final Map<String, Object> document;

  AutoValue_SuggestHit(
      String text,
      Map<String, Object> document) {
    if (text == null) {
      throw new NullPointerException("Null text");
    }
    this.text = text;
    if (document == null) {
      throw new NullPointerException("Null document");
    }
    this.document = document;
  }

  @Override
  public String text() {
    return text;
  }

  @Override
  public Map<String, Object> document() {
    return document;
  }

  @Override
  public String toString() {
    return "SuggestHit{"
         + "text=" + text + ", "
         + "document=" + document
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SuggestHit) {
      SuggestHit that = (SuggestHit) o;
      return (this.text.equals(that.text()))
           && (this.document.equals(that.document()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= text.hashCode();
    h$ *= 1000003;
    h$ ^= document.hashCode();
    return h$;
  }

}
