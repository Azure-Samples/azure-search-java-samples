

package com.microsoft.azure.search.samples.index;

import java.util.List;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_ComplexIndexField extends ComplexIndexField {

  private final String name;

  private final List<IndexField> fields;

  private final String type;

  AutoValue_ComplexIndexField(
      String name,
      List<IndexField> fields,
      String type) {
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (fields == null) {
      throw new NullPointerException("Null fields");
    }
    this.fields = fields;
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
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
  public String type() {
    return type;
  }

  @Override
  public String toString() {
    return "ComplexIndexField{"
         + "name=" + name + ", "
         + "fields=" + fields + ", "
         + "type=" + type
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ComplexIndexField) {
      ComplexIndexField that = (ComplexIndexField) o;
      return (this.name.equals(that.name()))
           && (this.fields.equals(that.fields()))
           && (this.type.equals(that.type()));
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
    h$ *= 1000003;
    h$ ^= type.hashCode();
    return h$;
  }

}
