

package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Hotel extends Hotel {

  private final String hotelId;

  private final String hotelName;

  private final String description;

  private final String descriptionFr;

  private final String category;

  private final List<String> tags;

  private final boolean parkingIncluded;

  private final boolean smokingAllowed;

  private final String lastRenovationDate;

  private final double rating;

  private final Address address;

  private final List<Room> rooms;

  AutoValue_Hotel(
      String hotelId,
      String hotelName,
      String description,
      String descriptionFr,
      String category,
      List<String> tags,
      boolean parkingIncluded,
      boolean smokingAllowed,
      String lastRenovationDate,
      double rating,
      Address address,
      List<Room> rooms) {
    if (hotelId == null) {
      throw new NullPointerException("Null hotelId");
    }
    this.hotelId = hotelId;
    if (hotelName == null) {
      throw new NullPointerException("Null hotelName");
    }
    this.hotelName = hotelName;
    if (description == null) {
      throw new NullPointerException("Null description");
    }
    this.description = description;
    if (descriptionFr == null) {
      throw new NullPointerException("Null descriptionFr");
    }
    this.descriptionFr = descriptionFr;
    if (category == null) {
      throw new NullPointerException("Null category");
    }
    this.category = category;
    if (tags == null) {
      throw new NullPointerException("Null tags");
    }
    this.tags = tags;
    this.parkingIncluded = parkingIncluded;
    this.smokingAllowed = smokingAllowed;
    if (lastRenovationDate == null) {
      throw new NullPointerException("Null lastRenovationDate");
    }
    this.lastRenovationDate = lastRenovationDate;
    this.rating = rating;
    if (address == null) {
      throw new NullPointerException("Null address");
    }
    this.address = address;
    if (rooms == null) {
      throw new NullPointerException("Null rooms");
    }
    this.rooms = rooms;
  }

  @JsonProperty(value = "HotelId")
  @Override
  public String hotelId() {
    return hotelId;
  }

  @JsonProperty(value = "HotelName")
  @Override
  public String hotelName() {
    return hotelName;
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

  @JsonProperty(value = "Category")
  @Override
  public String category() {
    return category;
  }

  @JsonProperty(value = "Tags")
  @Override
  public List<String> tags() {
    return tags;
  }

  @JsonProperty(value = "ParkingIncluded")
  @Override
  public boolean parkingIncluded() {
    return parkingIncluded;
  }

  @JsonProperty(value = "SmokingAllowed")
  @Override
  public boolean smokingAllowed() {
    return smokingAllowed;
  }

  @JsonProperty(value = "LastRenovationDate")
  @Override
  public String lastRenovationDate() {
    return lastRenovationDate;
  }

  @JsonProperty(value = "Rating")
  @Override
  public double rating() {
    return rating;
  }

  @JsonProperty(value = "Address")
  @Override
  public Address address() {
    return address;
  }

  @JsonProperty(value = "Rooms")
  @Override
  public List<Room> rooms() {
    return rooms;
  }

  @Override
  public String toString() {
    return "Hotel{"
         + "hotelId=" + hotelId + ", "
         + "hotelName=" + hotelName + ", "
         + "description=" + description + ", "
         + "descriptionFr=" + descriptionFr + ", "
         + "category=" + category + ", "
         + "tags=" + tags + ", "
         + "parkingIncluded=" + parkingIncluded + ", "
         + "smokingAllowed=" + smokingAllowed + ", "
         + "lastRenovationDate=" + lastRenovationDate + ", "
         + "rating=" + rating + ", "
         + "address=" + address + ", "
         + "rooms=" + rooms
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Hotel) {
      Hotel that = (Hotel) o;
      return (this.hotelId.equals(that.hotelId()))
           && (this.hotelName.equals(that.hotelName()))
           && (this.description.equals(that.description()))
           && (this.descriptionFr.equals(that.descriptionFr()))
           && (this.category.equals(that.category()))
           && (this.tags.equals(that.tags()))
           && (this.parkingIncluded == that.parkingIncluded())
           && (this.smokingAllowed == that.smokingAllowed())
           && (this.lastRenovationDate.equals(that.lastRenovationDate()))
           && (Double.doubleToLongBits(this.rating) == Double.doubleToLongBits(that.rating()))
           && (this.address.equals(that.address()))
           && (this.rooms.equals(that.rooms()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= hotelId.hashCode();
    h$ *= 1000003;
    h$ ^= hotelName.hashCode();
    h$ *= 1000003;
    h$ ^= description.hashCode();
    h$ *= 1000003;
    h$ ^= descriptionFr.hashCode();
    h$ *= 1000003;
    h$ ^= category.hashCode();
    h$ *= 1000003;
    h$ ^= tags.hashCode();
    h$ *= 1000003;
    h$ ^= parkingIncluded ? 1231 : 1237;
    h$ *= 1000003;
    h$ ^= smokingAllowed ? 1231 : 1237;
    h$ *= 1000003;
    h$ ^= lastRenovationDate.hashCode();
    h$ *= 1000003;
    h$ ^= (int) ((Double.doubleToLongBits(rating) >>> 32) ^ Double.doubleToLongBits(rating));
    h$ *= 1000003;
    h$ ^= address.hashCode();
    h$ *= 1000003;
    h$ ^= rooms.hashCode();
    return h$;
  }

}
