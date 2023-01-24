

package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_AzureSearchConfig extends AzureSearchConfig {

  private final String serviceName;

  private final String apiKey;

  private final String indexName;

  private final String indexerName;

  private final String apiVersion;

  private final String datasourceName;

  private final String endPoint;

  AutoValue_AzureSearchConfig(
      String serviceName,
      String apiKey,
      String indexName,
      String indexerName,
      String apiVersion,
      String datasourceName,
      String endPoint) {
    if (serviceName == null) {
      throw new NullPointerException("Null serviceName");
    }
    this.serviceName = serviceName;
    if (apiKey == null) {
      throw new NullPointerException("Null apiKey");
    }
    this.apiKey = apiKey;
    if (indexName == null) {
      throw new NullPointerException("Null indexName");
    }
    this.indexName = indexName;
    if (indexerName == null) {
      throw new NullPointerException("Null indexerName");
    }
    this.indexerName = indexerName;
    if (apiVersion == null) {
      throw new NullPointerException("Null apiVersion");
    }
    this.apiVersion = apiVersion;
    if (datasourceName == null) {
      throw new NullPointerException("Null datasourceName");
    }
    this.datasourceName = datasourceName;
    if (endPoint == null) {
      throw new NullPointerException("Null endPoint");
    }
    this.endPoint = endPoint;
  }

  @JsonProperty(value = "ServiceName")
  @Override
  public String serviceName() {
    return serviceName;
  }

  @JsonProperty(value = "ApiKey")
  @Override
  public String apiKey() {
    return apiKey;
  }

  @JsonProperty(value = "IndexName")
  @Override
  public String indexName() {
    return indexName;
  }

  @JsonProperty(value = "IndexerName")
  @Override
  public String indexerName() {
    return indexerName;
  }

  @JsonProperty(value = "ApiVersion")
  @Override
  public String apiVersion() {
    return apiVersion;
  }

  @JsonProperty(value = "DatasourceName")
  @Override
  public String datasourceName() {
    return datasourceName;
  }

  @JsonProperty(value = "EndPoint")
  @Override
  public String endPoint() {
    return endPoint;
  }

  @Override
  public String toString() {
    return "AzureSearchConfig{"
         + "serviceName=" + serviceName + ", "
         + "apiKey=" + apiKey + ", "
         + "indexName=" + indexName + ", "
         + "indexerName=" + indexerName + ", "
         + "apiVersion=" + apiVersion + ", "
         + "datasourceName=" + datasourceName + ", "
         + "endPoint=" + endPoint
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof AzureSearchConfig) {
      AzureSearchConfig that = (AzureSearchConfig) o;
      return (this.serviceName.equals(that.serviceName()))
           && (this.apiKey.equals(that.apiKey()))
           && (this.indexName.equals(that.indexName()))
           && (this.indexerName.equals(that.indexerName()))
           && (this.apiVersion.equals(that.apiVersion()))
           && (this.datasourceName.equals(that.datasourceName()))
           && (this.endPoint.equals(that.endPoint()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= serviceName.hashCode();
    h$ *= 1000003;
    h$ ^= apiKey.hashCode();
    h$ *= 1000003;
    h$ ^= indexName.hashCode();
    h$ *= 1000003;
    h$ ^= indexerName.hashCode();
    h$ *= 1000003;
    h$ ^= apiVersion.hashCode();
    h$ *= 1000003;
    h$ ^= datasourceName.hashCode();
    h$ *= 1000003;
    h$ ^= endPoint.hashCode();
    return h$;
  }

}
