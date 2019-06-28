

package com.microsoft.azure.search.samples.results;

import java.util.Map;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_SearchResult_SearchHit extends SearchResult.SearchHit {

  private final Map<String, Object> document;

  AutoValue_SearchResult_SearchHit(
      Map<String, Object> document) {
    if (document == null) {
      throw new NullPointerException("Null document");
    }
    this.document = document;
  }

  @Override
  public Map<String, Object> document() {
    return document;
  }

  @Override
  public String toString() {
    return "SearchHit{"
         + "document=" + document
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SearchResult.SearchHit) {
      SearchResult.SearchHit that = (SearchResult.SearchHit) o;
      return (this.document.equals(that.document()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= document.hashCode();
    return h$;
  }

}
