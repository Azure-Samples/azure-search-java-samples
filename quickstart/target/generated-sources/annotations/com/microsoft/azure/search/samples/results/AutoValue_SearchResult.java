

package com.microsoft.azure.search.samples.results;

import java.util.List;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_SearchResult extends SearchResult {

  private final List<SearchResult.SearchHit> hits;

  private final long count;

  AutoValue_SearchResult(
      List<SearchResult.SearchHit> hits,
      long count) {
    if (hits == null) {
      throw new NullPointerException("Null hits");
    }
    this.hits = hits;
    this.count = count;
  }

  @Override
  public List<SearchResult.SearchHit> hits() {
    return hits;
  }

  @Override
  public long count() {
    return count;
  }

  @Override
  public String toString() {
    return "SearchResult{"
         + "hits=" + hits + ", "
         + "count=" + count
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SearchResult) {
      SearchResult that = (SearchResult) o;
      return (this.hits.equals(that.hits()))
           && (this.count == that.count());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= hits.hashCode();
    h$ *= 1000003;
    h$ ^= (int) ((count >>> 32) ^ count);
    return h$;
  }

}
