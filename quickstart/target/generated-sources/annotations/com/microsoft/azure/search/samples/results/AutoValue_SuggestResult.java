

package com.microsoft.azure.search.samples.results;

import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_SuggestResult extends SuggestResult {

  private final List<SuggestHit> hits;

  private final Double coverage;

  AutoValue_SuggestResult(
      List<SuggestHit> hits,
      @Nullable Double coverage) {
    if (hits == null) {
      throw new NullPointerException("Null hits");
    }
    this.hits = hits;
    this.coverage = coverage;
  }

  @Override
  public List<SuggestHit> hits() {
    return hits;
  }

  @Nullable
  @Override
  public Double coverage() {
    return coverage;
  }

  @Override
  public String toString() {
    return "SuggestResult{"
         + "hits=" + hits + ", "
         + "coverage=" + coverage
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SuggestResult) {
      SuggestResult that = (SuggestResult) o;
      return (this.hits.equals(that.hits()))
           && ((this.coverage == null) ? (that.coverage() == null) : this.coverage.equals(that.coverage()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= hits.hashCode();
    h$ *= 1000003;
    h$ ^= (coverage == null) ? 0 : coverage.hashCode();
    return h$;
  }

}
