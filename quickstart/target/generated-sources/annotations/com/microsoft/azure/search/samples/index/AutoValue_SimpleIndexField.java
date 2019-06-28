

package com.microsoft.azure.search.samples.index;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_SimpleIndexField extends SimpleIndexField {

  private final String name;

  private final String type;

  private final String analyzer;

  private final Boolean searchable;

  private final Boolean filterable;

  private final Boolean sortable;

  private final Boolean key;

  private AutoValue_SimpleIndexField(
      String name,
      String type,
      @Nullable String analyzer,
      @Nullable Boolean searchable,
      @Nullable Boolean filterable,
      @Nullable Boolean sortable,
      @Nullable Boolean key) {
    this.name = name;
    this.type = type;
    this.analyzer = analyzer;
    this.searchable = searchable;
    this.filterable = filterable;
    this.sortable = sortable;
    this.key = key;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String type() {
    return type;
  }

  @Nullable
  @Override
  public String analyzer() {
    return analyzer;
  }

  @Nullable
  @Override
  public Boolean searchable() {
    return searchable;
  }

  @Nullable
  @Override
  public Boolean filterable() {
    return filterable;
  }

  @Nullable
  @Override
  public Boolean sortable() {
    return sortable;
  }

  @Nullable
  @Override
  public Boolean key() {
    return key;
  }

  @Override
  public String toString() {
    return "SimpleIndexField{"
         + "name=" + name + ", "
         + "type=" + type + ", "
         + "analyzer=" + analyzer + ", "
         + "searchable=" + searchable + ", "
         + "filterable=" + filterable + ", "
         + "sortable=" + sortable + ", "
         + "key=" + key
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SimpleIndexField) {
      SimpleIndexField that = (SimpleIndexField) o;
      return (this.name.equals(that.name()))
           && (this.type.equals(that.type()))
           && ((this.analyzer == null) ? (that.analyzer() == null) : this.analyzer.equals(that.analyzer()))
           && ((this.searchable == null) ? (that.searchable() == null) : this.searchable.equals(that.searchable()))
           && ((this.filterable == null) ? (that.filterable() == null) : this.filterable.equals(that.filterable()))
           && ((this.sortable == null) ? (that.sortable() == null) : this.sortable.equals(that.sortable()))
           && ((this.key == null) ? (that.key() == null) : this.key.equals(that.key()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= name.hashCode();
    h$ *= 1000003;
    h$ ^= type.hashCode();
    h$ *= 1000003;
    h$ ^= (analyzer == null) ? 0 : analyzer.hashCode();
    h$ *= 1000003;
    h$ ^= (searchable == null) ? 0 : searchable.hashCode();
    h$ *= 1000003;
    h$ ^= (filterable == null) ? 0 : filterable.hashCode();
    h$ *= 1000003;
    h$ ^= (sortable == null) ? 0 : sortable.hashCode();
    h$ *= 1000003;
    h$ ^= (key == null) ? 0 : key.hashCode();
    return h$;
  }

  static final class Builder extends SimpleIndexField.Builder {
    private String name;
    private String type;
    private String analyzer;
    private Boolean searchable;
    private Boolean filterable;
    private Boolean sortable;
    private Boolean key;
    Builder() {
    }
    @Override
    public SimpleIndexField.Builder name(String name) {
      if (name == null) {
        throw new NullPointerException("Null name");
      }
      this.name = name;
      return this;
    }
    @Override
    public SimpleIndexField.Builder type(String type) {
      if (type == null) {
        throw new NullPointerException("Null type");
      }
      this.type = type;
      return this;
    }
    @Override
    public SimpleIndexField.Builder analyzer(String analyzer) {
      this.analyzer = analyzer;
      return this;
    }
    @Override
    public SimpleIndexField.Builder searchable(Boolean searchable) {
      this.searchable = searchable;
      return this;
    }
    @Override
    public SimpleIndexField.Builder filterable(Boolean filterable) {
      this.filterable = filterable;
      return this;
    }
    @Override
    public SimpleIndexField.Builder sortable(Boolean sortable) {
      this.sortable = sortable;
      return this;
    }
    @Override
    public SimpleIndexField.Builder key(Boolean key) {
      this.key = key;
      return this;
    }
    @Override
    public SimpleIndexField build() {
      String missing = "";
      if (this.name == null) {
        missing += " name";
      }
      if (this.type == null) {
        missing += " type";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_SimpleIndexField(
          this.name,
          this.type,
          this.analyzer,
          this.searchable,
          this.filterable,
          this.sortable,
          this.key);
    }
  }

}
