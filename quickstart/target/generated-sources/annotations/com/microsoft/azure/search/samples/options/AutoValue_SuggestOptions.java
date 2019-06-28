

package com.microsoft.azure.search.samples.options;

import java.util.Optional;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_SuggestOptions extends SuggestOptions {

  private final Optional<String> filter;

  private final Optional<String> orderby;

  private final Optional<String> select;

  private final Optional<String> searchFields;

  private final Optional<String> highlightPreTag;

  private final Optional<String> highlightPostTag;

  private final Optional<Integer> top;

  private final Optional<Double> minimumCoverage;

  private final Boolean fuzzy;

  private AutoValue_SuggestOptions(
      Optional<String> filter,
      Optional<String> orderby,
      Optional<String> select,
      Optional<String> searchFields,
      Optional<String> highlightPreTag,
      Optional<String> highlightPostTag,
      Optional<Integer> top,
      Optional<Double> minimumCoverage,
      Boolean fuzzy) {
    this.filter = filter;
    this.orderby = orderby;
    this.select = select;
    this.searchFields = searchFields;
    this.highlightPreTag = highlightPreTag;
    this.highlightPostTag = highlightPostTag;
    this.top = top;
    this.minimumCoverage = minimumCoverage;
    this.fuzzy = fuzzy;
  }

  @Override
  public Optional<String> filter() {
    return filter;
  }

  @Override
  public Optional<String> orderby() {
    return orderby;
  }

  @Override
  public Optional<String> select() {
    return select;
  }

  @Override
  public Optional<String> searchFields() {
    return searchFields;
  }

  @Override
  public Optional<String> highlightPreTag() {
    return highlightPreTag;
  }

  @Override
  public Optional<String> highlightPostTag() {
    return highlightPostTag;
  }

  @Override
  public Optional<Integer> top() {
    return top;
  }

  @Override
  public Optional<Double> minimumCoverage() {
    return minimumCoverage;
  }

  @Override
  public Boolean fuzzy() {
    return fuzzy;
  }

  @Override
  public String toString() {
    return "SuggestOptions{"
         + "filter=" + filter + ", "
         + "orderby=" + orderby + ", "
         + "select=" + select + ", "
         + "searchFields=" + searchFields + ", "
         + "highlightPreTag=" + highlightPreTag + ", "
         + "highlightPostTag=" + highlightPostTag + ", "
         + "top=" + top + ", "
         + "minimumCoverage=" + minimumCoverage + ", "
         + "fuzzy=" + fuzzy
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SuggestOptions) {
      SuggestOptions that = (SuggestOptions) o;
      return (this.filter.equals(that.filter()))
           && (this.orderby.equals(that.orderby()))
           && (this.select.equals(that.select()))
           && (this.searchFields.equals(that.searchFields()))
           && (this.highlightPreTag.equals(that.highlightPreTag()))
           && (this.highlightPostTag.equals(that.highlightPostTag()))
           && (this.top.equals(that.top()))
           && (this.minimumCoverage.equals(that.minimumCoverage()))
           && (this.fuzzy.equals(that.fuzzy()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= filter.hashCode();
    h$ *= 1000003;
    h$ ^= orderby.hashCode();
    h$ *= 1000003;
    h$ ^= select.hashCode();
    h$ *= 1000003;
    h$ ^= searchFields.hashCode();
    h$ *= 1000003;
    h$ ^= highlightPreTag.hashCode();
    h$ *= 1000003;
    h$ ^= highlightPostTag.hashCode();
    h$ *= 1000003;
    h$ ^= top.hashCode();
    h$ *= 1000003;
    h$ ^= minimumCoverage.hashCode();
    h$ *= 1000003;
    h$ ^= fuzzy.hashCode();
    return h$;
  }

  static final class Builder extends SuggestOptions.Builder {
    private Optional<String> filter = Optional.empty();
    private Optional<String> orderby = Optional.empty();
    private Optional<String> select = Optional.empty();
    private Optional<String> searchFields = Optional.empty();
    private Optional<String> highlightPreTag = Optional.empty();
    private Optional<String> highlightPostTag = Optional.empty();
    private Optional<Integer> top = Optional.empty();
    private Optional<Double> minimumCoverage = Optional.empty();
    private Boolean fuzzy;
    Builder() {
    }
    @Override
    public SuggestOptions.Builder filter(String filter) {
      this.filter = Optional.of(filter);
      return this;
    }
    @Override
    public SuggestOptions.Builder orderby(String orderby) {
      this.orderby = Optional.of(orderby);
      return this;
    }
    @Override
    public SuggestOptions.Builder select(String select) {
      this.select = Optional.of(select);
      return this;
    }
    @Override
    public SuggestOptions.Builder searchFields(String searchFields) {
      this.searchFields = Optional.of(searchFields);
      return this;
    }
    @Override
    public SuggestOptions.Builder highlightPreTag(String highlightPreTag) {
      this.highlightPreTag = Optional.of(highlightPreTag);
      return this;
    }
    @Override
    public SuggestOptions.Builder highlightPostTag(String highlightPostTag) {
      this.highlightPostTag = Optional.of(highlightPostTag);
      return this;
    }
    @Override
    public SuggestOptions.Builder top(Integer top) {
      this.top = Optional.of(top);
      return this;
    }
    @Override
    public SuggestOptions.Builder minimumCoverage(Double minimumCoverage) {
      this.minimumCoverage = Optional.of(minimumCoverage);
      return this;
    }
    @Override
    public SuggestOptions.Builder fuzzy(Boolean fuzzy) {
      if (fuzzy == null) {
        throw new NullPointerException("Null fuzzy");
      }
      this.fuzzy = fuzzy;
      return this;
    }
    @Override
    public SuggestOptions build() {
      String missing = "";
      if (this.fuzzy == null) {
        missing += " fuzzy";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_SuggestOptions(
          this.filter,
          this.orderby,
          this.select,
          this.searchFields,
          this.highlightPreTag,
          this.highlightPostTag,
          this.top,
          this.minimumCoverage,
          this.fuzzy);
    }
  }

}
