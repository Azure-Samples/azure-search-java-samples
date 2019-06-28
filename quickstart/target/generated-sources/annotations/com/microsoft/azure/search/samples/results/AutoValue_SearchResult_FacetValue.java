

package com.microsoft.azure.search.samples.results;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_SearchResult_FacetValue extends SearchResult.FacetValue {

  private final Object value;

  private final Object from;

  private final Object to;

  private final Integer count;

  AutoValue_SearchResult_FacetValue(
      @Nullable Object value,
      @Nullable Object from,
      @Nullable Object to,
      Integer count) {
    this.value = value;
    this.from = from;
    this.to = to;
    if (count == null) {
      throw new NullPointerException("Null count");
    }
    this.count = count;
  }

  @Nullable
  @Override
  public Object value() {
    return value;
  }

  @Nullable
  @Override
  public Object from() {
    return from;
  }

  @Nullable
  @Override
  public Object to() {
    return to;
  }

  @Override
  public Integer count() {
    return count;
  }

  @Override
  public String toString() {
    return "FacetValue{"
         + "value=" + value + ", "
         + "from=" + from + ", "
         + "to=" + to + ", "
         + "count=" + count
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SearchResult.FacetValue) {
      SearchResult.FacetValue that = (SearchResult.FacetValue) o;
      return ((this.value == null) ? (that.value() == null) : this.value.equals(that.value()))
           && ((this.from == null) ? (that.from() == null) : this.from.equals(that.from()))
           && ((this.to == null) ? (that.to() == null) : this.to.equals(that.to()))
           && (this.count.equals(that.count()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= (value == null) ? 0 : value.hashCode();
    h$ *= 1000003;
    h$ ^= (from == null) ? 0 : from.hashCode();
    h$ *= 1000003;
    h$ ^= (to == null) ? 0 : to.hashCode();
    h$ *= 1000003;
    h$ ^= count.hashCode();
    return h$;
  }

}
