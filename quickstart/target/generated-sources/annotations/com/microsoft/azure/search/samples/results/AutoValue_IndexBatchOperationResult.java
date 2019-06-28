

package com.microsoft.azure.search.samples.results;

import javax.annotation.Generated;
import javax.annotation.Nullable;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_IndexBatchOperationResult extends IndexBatchOperationResult {

  private final String key;

  private final boolean status;

  private final String errorMessage;

  private final int statusCode;

  AutoValue_IndexBatchOperationResult(
      String key,
      boolean status,
      @Nullable String errorMessage,
      int statusCode) {
    if (key == null) {
      throw new NullPointerException("Null key");
    }
    this.key = key;
    this.status = status;
    this.errorMessage = errorMessage;
    this.statusCode = statusCode;
  }

  @Override
  public String key() {
    return key;
  }

  @Override
  public boolean status() {
    return status;
  }

  @Nullable
  @Override
  public String errorMessage() {
    return errorMessage;
  }

  @Override
  public int statusCode() {
    return statusCode;
  }

  @Override
  public String toString() {
    return "IndexBatchOperationResult{"
         + "key=" + key + ", "
         + "status=" + status + ", "
         + "errorMessage=" + errorMessage + ", "
         + "statusCode=" + statusCode
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof IndexBatchOperationResult) {
      IndexBatchOperationResult that = (IndexBatchOperationResult) o;
      return (this.key.equals(that.key()))
           && (this.status == that.status())
           && ((this.errorMessage == null) ? (that.errorMessage() == null) : this.errorMessage.equals(that.errorMessage()))
           && (this.statusCode == that.statusCode());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= key.hashCode();
    h$ *= 1000003;
    h$ ^= status ? 1231 : 1237;
    h$ *= 1000003;
    h$ ^= (errorMessage == null) ? 0 : errorMessage.hashCode();
    h$ *= 1000003;
    h$ ^= statusCode;
    return h$;
  }

}
