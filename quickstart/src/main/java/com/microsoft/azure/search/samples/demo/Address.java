package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {
    public static final String STREET_ADDRESS = "StreetAddress";
    public static final String CITY = "City";
    public static final String STATE_PROVINCE = "StateProvince";
    public static final String POSTAL_CODE = "PostalCode";

    @JsonProperty(STREET_ADDRESS)
    public abstract String streetAddress();

    @JsonProperty(CITY)
    public abstract String city();

    @JsonProperty(STATE_PROVINCE)
    public abstract String stateProvince();

    @JsonProperty(POSTAL_CODE)
    public abstract String postalCode();

    @JsonCreator
    public static Address create(@JsonProperty(STREET_ADDRESS) String streetAddress, @JsonProperty(CITY) String city,
            @JsonProperty(STATE_PROVINCE) String stateProvince, @JsonProperty(POSTAL_CODE) String postalCode) {
        return new com.microsoft.azure.search.samples.demo.AutoValue_Address(streetAddress, city, stateProvince, postalCode);
    }
}
