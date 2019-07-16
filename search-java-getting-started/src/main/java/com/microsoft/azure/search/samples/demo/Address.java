package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {
    public static final String STREET_ADDRESS = "StreetAddress";
    public static final String CITY = "City";
    public static final String STATE = "State";
    public static final String ZIP_CODE = "ZipCode";

    @JsonProperty(STREET_ADDRESS)
    public abstract String streetAddress();

    @JsonProperty(CITY)
    public abstract String city();

    @JsonProperty(STATE)
    public abstract String state();

    @JsonProperty(ZIP_CODE)
    public abstract String zipCode();

    @JsonCreator
    public static Address create(@JsonProperty(STREET_ADDRESS) String streetAddress, @JsonProperty(CITY) String city,
            @JsonProperty(STATE) String state, @JsonProperty(ZIP_CODE) String zipCode) {
        return new com.microsoft.azure.search.samples.demo.AutoValue_Address(streetAddress, city, state, zipCode);
    }
}
