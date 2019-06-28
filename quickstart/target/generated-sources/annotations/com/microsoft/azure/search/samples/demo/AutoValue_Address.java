

package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Address extends Address {

  private final String streetAddress;

  private final String city;

  private final String stateProvince;

  private final String postalCode;

  AutoValue_Address(
      String streetAddress,
      String city,
      String stateProvince,
      String postalCode) {
    if (streetAddress == null) {
      throw new NullPointerException("Null streetAddress");
    }
    this.streetAddress = streetAddress;
    if (city == null) {
      throw new NullPointerException("Null city");
    }
    this.city = city;
    if (stateProvince == null) {
      throw new NullPointerException("Null stateProvince");
    }
    this.stateProvince = stateProvince;
    if (postalCode == null) {
      throw new NullPointerException("Null postalCode");
    }
    this.postalCode = postalCode;
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

  @JsonProperty(value = "StateProvince")
  @Override
  public String stateProvince() {
    return stateProvince;
  }

  @JsonProperty(value = "PostalCode")
  @Override
  public String postalCode() {
    return postalCode;
  }

  @Override
  public String toString() {
    return "Address{"
         + "streetAddress=" + streetAddress + ", "
         + "city=" + city + ", "
         + "stateProvince=" + stateProvince + ", "
         + "postalCode=" + postalCode
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
           && (this.stateProvince.equals(that.stateProvince()))
           && (this.postalCode.equals(that.postalCode()));
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
    h$ ^= stateProvince.hashCode();
    h$ *= 1000003;
    h$ ^= postalCode.hashCode();
    return h$;
  }

}
