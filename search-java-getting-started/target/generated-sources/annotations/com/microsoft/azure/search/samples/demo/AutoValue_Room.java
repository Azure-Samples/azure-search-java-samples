

package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Room extends Room {

  private final String description;

  private final String descriptionFr;

  private final String type;

  private final double baseRate;

  private final String bedOptions;

  private final int sleepsCount;

  private final boolean smokingAllowed;

  private final List<String> tags;

  AutoValue_Room(
      String description,
      String descriptionFr,
      String type,
      double baseRate,
      String bedOptions,
      int sleepsCount,
      boolean smokingAllowed,
      List<String> tags) {
    if (description == null) {
      throw new NullPointerException("Null description");
    }
    this.description = description;
    if (descriptionFr == null) {
      throw new NullPointerException("Null descriptionFr");
    }
    this.descriptionFr = descriptionFr;
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    this.baseRate = baseRate;
    if (bedOptions == null) {
      throw new NullPointerException("Null bedOptions");
    }
    this.bedOptions = bedOptions;
    this.sleepsCount = sleepsCount;
    this.smokingAllowed = smokingAllowed;
    if (tags == null) {
      throw new NullPointerException("Null tags");
    }
    this.tags = tags;
  }

  @JsonProperty(value = "Description")
  @Override
  public String description() {
    return description;
  }

  @JsonProperty(value = "Description_fr")
  @Override
  public String descriptionFr() {
    return descriptionFr;
  }

  @JsonProperty(value = "Type")
  @Override
  public String type() {
    return type;
  }

  @JsonProperty(value = "BaseRate")
  @Override
  public double baseRate() {
    return baseRate;
  }

  @JsonProperty(value = "BedOptions")
  @Override
  public String bedOptions() {
    return bedOptions;
  }

  @JsonProperty(value = "SleepsCount")
  @Override
  public int sleepsCount() {
    return sleepsCount;
  }

  @JsonProperty(value = "SmokingAllowed")
  @Override
  public boolean smokingAllowed() {
    return smokingAllowed;
  }

  @JsonProperty(value = "Tags")
  @Override
  public List<String> tags() {
    return tags;
  }

  @Override
  public String toString() {
    return "Room{"
         + "description=" + description + ", "
         + "descriptionFr=" + descriptionFr + ", "
         + "type=" + type + ", "
         + "baseRate=" + baseRate + ", "
         + "bedOptions=" + bedOptions + ", "
         + "sleepsCount=" + sleepsCount + ", "
         + "smokingAllowed=" + smokingAllowed + ", "
         + "tags=" + tags
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Room) {
      Room that = (Room) o;
      return (this.description.equals(that.description()))
           && (this.descriptionFr.equals(that.descriptionFr()))
           && (this.type.equals(that.type()))
           && (Double.doubleToLongBits(this.baseRate) == Double.doubleToLongBits(that.baseRate()))
           && (this.bedOptions.equals(that.bedOptions()))
           && (this.sleepsCount == that.sleepsCount())
           && (this.smokingAllowed == that.smokingAllowed())
           && (this.tags.equals(that.tags()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= description.hashCode();
    h$ *= 1000003;
    h$ ^= descriptionFr.hashCode();
    h$ *= 1000003;
    h$ ^= type.hashCode();
    h$ *= 1000003;
    h$ ^= (int) ((Double.doubleToLongBits(baseRate) >>> 32) ^ Double.doubleToLongBits(baseRate));
    h$ *= 1000003;
    h$ ^= bedOptions.hashCode();
    h$ *= 1000003;
    h$ ^= sleepsCount;
    h$ *= 1000003;
    h$ ^= smokingAllowed ? 1231 : 1237;
    h$ *= 1000003;
    h$ ^= tags.hashCode();
    return h$;
  }

}
