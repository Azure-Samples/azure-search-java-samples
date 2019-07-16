package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class Hotel {
    public static final String HOTEL_ID = "HotelId";
    public static final String HOTEL_NAME = "HotelName";
    public static final String DESCRIPTION = "Description";
    public static final String DESCRIPTION_FR = "Description_fr";
    public static final String CATEGORY = "Category";
    public static final String TAGS = "Tags";
    public static final String PARKING_INCLUDED = "ParkingIncluded";
    public static final String SMOKING_ALLOWED = "SmokingAllowed";
    public static final String LAST_RENOVATION_DATE = "LastRenovationDate";
    public static final String RATING = "Rating";
    public static final String ADDRESS = "Address";
    public static final String ROOMS = "Rooms";

    @JsonProperty(HOTEL_ID)
    public abstract String hotelId();

    @JsonProperty(HOTEL_NAME)
    public abstract String hotelName();

    @JsonProperty(DESCRIPTION)
    public abstract String description();

    @JsonProperty(DESCRIPTION_FR)
    public abstract String descriptionFr();

    @JsonProperty(CATEGORY)
    public abstract String category();

    @JsonProperty(TAGS)
    public abstract List<String> tags();

    @JsonProperty(PARKING_INCLUDED)
    public abstract boolean parkingIncluded();

    @JsonProperty(SMOKING_ALLOWED)
    public abstract boolean smokingAllowed();

    @JsonProperty(LAST_RENOVATION_DATE)
    public abstract String lastRenovationDate();

    @JsonProperty(RATING)
    public abstract double rating();

    @JsonProperty(ADDRESS)
    public abstract Address address();

    @JsonProperty(ROOMS)
    public abstract List<Room> rooms();

    @JsonCreator
    public static Hotel create(@JsonProperty(HOTEL_ID) String hotelId, @JsonProperty(HOTEL_NAME) String hotelName,
            @JsonProperty(DESCRIPTION) String description, @JsonProperty(DESCRIPTION_FR) String descriptionFr,
            @JsonProperty(CATEGORY) String category, @JsonProperty(TAGS) List<String> tags,
            @JsonProperty(PARKING_INCLUDED) boolean parkingIncluded,
            @JsonProperty(SMOKING_ALLOWED) boolean smokingAllowed,
            @JsonProperty(LAST_RENOVATION_DATE) String lastRenovationDate, @JsonProperty(RATING) double rating,
            @JsonProperty(ADDRESS) Address address, @JsonProperty(ROOMS) List<Room> rooms) {
        return new com.microsoft.azure.search.samples.demo.AutoValue_Hotel(hotelId, hotelName, description,
                                                                           descriptionFr, category, tags,
                                                                           parkingIncluded, smokingAllowed,
                                                                           lastRenovationDate, rating, address, rooms);
    }
}
