

package com.microsoft.azure.search.samples.options;

import java.util.Optional;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_SearchOptions extends SearchOptions {

  private final Optional<Boolean> includeCount;

  private final Optional<String> filter;

  private final Optional<String> orderBy;

  private final Optional<String> select;

  private final Optional<Integer> top;

  private AutoValue_SearchOptions(
      Optional<Boolean> includeCount,
      Optional<String> filter,
      Optional<String> orderBy,
      Optional<String> select,
      Optional<Integer> top) {
    this.includeCount = includeCount;
    this.filter = filter;
    this.orderBy = orderBy;
    this.select = select;
    this.top = top;
  }

  @Override
  public Optional<Boolean> includeCount() {
    return includeCount;
  }

  @Override
  public Optional<String> filter() {
    return filter;
  }

  @Override
  public Optional<String> orderBy() {
    return orderBy;
  }

  @Override
  public Optional<String> select() {
    return select;
  }

  @Override
  public Optional<Integer> top() {
    return top;
  }

  @Override
  public String toString() {
    return "SearchOptions{"
         + "includeCount=" + includeCount + ", "
         + "filter=" + filter + ", "
         + "orderBy=" + orderBy + ", "
         + "select=" + select + ", "
         + "top=" + top
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SearchOptions) {
      SearchOptions that = (SearchOptions) o;
      return (this.includeCount.equals(that.includeCount()))
           && (this.filter.equals(that.filter()))
           && (this.orderBy.equals(that.orderBy()))
           && (this.select.equals(that.select()))
           && (this.top.equals(that.top()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= includeCount.hashCode();
    h$ *= 1000003;
    h$ ^= filter.hashCode();
    h$ *= 1000003;
    h$ ^= orderBy.hashCode();
    h$ *= 1000003;
    h$ ^= select.hashCode();
    h$ *= 1000003;
    h$ ^= top.hashCode();
    return h$;
  }

  static final class Builder extends SearchOptions.Builder {
    private Optional<Boolean> includeCount = Optional.empty();
    private Optional<String> filter = Optional.empty();
    private Optional<String> orderBy = Optional.empty();
    private Optional<String> select = Optional.empty();
    private Optional<Integer> top = Optional.empty();
    Builder() {
    }
    @Override
    public SearchOptions.Builder includeCount(boolean includeCount) {
      this.includeCount = Optional.of(includeCount);
      return this;
    }
    @Override
    public SearchOptions.Builder filter(String filter) {
      this.filter = Optional.of(filter);
      return this;
    }
    @Override
    public SearchOptions.Builder orderBy(String orderBy) {
      this.orderBy = Optional.of(orderBy);
      return this;
    }
    @Override
    public SearchOptions.Builder select(String select) {
      this.select = Optional.of(select);
      return this;
    }
    @Override
    public SearchOptions.Builder top(Integer top) {
      this.top = Optional.of(top);
      return this;
    }
    @Override
    public SearchOptions build() {
      return new AutoValue_SearchOptions(
          this.includeCount,
          this.filter,
          this.orderBy,
          this.select,
          this.top);
    }
  }

}
