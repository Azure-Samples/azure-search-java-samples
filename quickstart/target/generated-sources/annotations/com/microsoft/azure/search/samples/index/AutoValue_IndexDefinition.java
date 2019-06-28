

package com.microsoft.azure.search.samples.index;

import java.util.List;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_IndexDefinition extends IndexDefinition {

  private final String name;

  private final List<IndexField> fields;

  AutoValue_IndexDefinition(
      String name,
      List<IndexField> fields) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (fields == null) {
      throw new NullPointerException("Null fields");
    }
    this.fields = fields;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public List<IndexField> fields() {
    return fields;
  }

  @Override
  public String toString() {
    return "IndexDefinition{"
         + "name=" + name + ", "
         + "fields=" + fields
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof IndexDefinition) {
      IndexDefinition that = (IndexDefinition) o;
      return (this.name.equals(that.name()))
           && (this.fields.equals(that.fields()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= name.hashCode();
    h$ *= 1000003;
    h$ ^= fields.hashCode();
    return h$;
  }

}
