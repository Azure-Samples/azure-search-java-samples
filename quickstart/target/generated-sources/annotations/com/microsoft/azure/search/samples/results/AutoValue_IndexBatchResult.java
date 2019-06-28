

package com.microsoft.azure.search.samples.results;

import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_IndexBatchResult extends IndexBatchResult {

  private final List<IndexBatchOperationResult> value;

  private final Integer status;

  AutoValue_IndexBatchResult(
      List<IndexBatchOperationResult> value,
      @Nullable Integer status) {
    if (value == null) {
      throw new NullPointerException("Null value");
    }
    this.value = value;
    this.status = status;
  }

  @Override
  public List<IndexBatchOperationResult> value() {
    return value;
  }

  @Nullable
  @Override
  public Integer status() {
    return status;
  }

  @Override
  public String toString() {
    return "IndexBatchResult{"
         + "value=" + value + ", "
         + "status=" + status
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof IndexBatchResult) {
      IndexBatchResult that = (IndexBatchResult) o;
      return (this.value.equals(that.value()))
           && ((this.status == null) ? (that.status() == null) : this.status.equals(that.status()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= value.hashCode();
    h$ *= 1000003;
    h$ ^= (status == null) ? 0 : status.hashCode();
    return h$;
  }

}
