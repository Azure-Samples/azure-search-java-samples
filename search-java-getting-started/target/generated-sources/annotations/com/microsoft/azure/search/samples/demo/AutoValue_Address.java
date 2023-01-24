

package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Address extends Address {

  private final String streetAddress;

  private final String city;

  private final String state;

  private final String zipCode;

  AutoValue_Address(
      String streetAddress,
      String city,
      String state,
      String zipCode) {
    if (streetAddress == null) {
      throw new NullPointerException("Null streetAddress");
    }
    this.streetAddress = streetAddress;
    if (city == null) {
      throw new NullPointerException("Null city");
    }
    this.city = city;
    if (state == null) {
      throw new NullPointerException("Null state");
    }
    this.state = state;
    if (zipCode == null) {
      throw new NullPointerException("Null zipCode");
    }
    this.zipCode = zipCode;
  }

  @JsonProperty(value = "StreetAddress")
  @Override
  public String streetAddress() {
    return streetAddress;
  }

  @JsonProperty(value = "City")
  @Override
  public String city() {
    return city;
  }

  @JsonProperty(value = "State")
  @Override
  public String state() {
    return state;
  }

  @JsonProperty(value = "ZipCode")
  @Override
  public String zipCode() {
    return zipCode;
  }

  @Override
  public String toString() {
    return "Address{"
         + "streetAddress=" + streetAddress + ", "
         + "city=" + city + ", "
         + "state=" + state + ", "
         + "zipCode=" + zipCode
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Address) {
      Address that = (Address) o;
      return (this.streetAddress.equals(that.streetAddress()))
           && (this.city.equals(that.city()))
           && (this.state.equals(that.state()))
           && (this.zipCode.equals(that.zipCode()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= streetAddress.hashCode();
    h$ *= 1000003;
    h$ ^= city.hashCode();
    h$ *= 1000003;
    h$ ^= state.hashCode();
    h$ *= 1000003;
    h$ ^= zipCode.hashCode();
    return h$;
  }

}
