package com.microsoft.azure.search.samples.demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class Room {
    public static final String DESCRIPTION = "Description";
    public static final String DESCRIPTION_FR = "Description_fr";
    public static final String TYPE = "Type";
    public static final String BASE_RATE = "BaseRate";
    public static final String BED_OPTIONS = "BedOptions";
    public static final String SLEEPS_COUNT = "SleepsCount";
    public static final String SMOKING_ALLOWED = "SmokingAllowed";
    public static final String TAGS = "Tags";

    @JsonProperty(DESCRIPTION)
    public abstract String description();

    @JsonProperty(DESCRIPTION_FR)
    public abstract String descriptionFr();

    @JsonProperty(TYPE)
    public abstract String type();

    @JsonProperty(BASE_RATE)
    public abstract double baseRate();

    @JsonProperty(BED_OPTIONS)
    public abstract String bedOptions();

    @JsonProperty(SLEEPS_COUNT)
    public abstract int sleepsCount();

    @JsonProperty(SMOKING_ALLOWED)
    public abstract boolean smokingAllowed();

    @JsonProperty(TAGS)
    public abstract List<String> tags();

    @JsonCreator
    public static Room create(@JsonProperty(DESCRIPTION) String description,
            @JsonProperty(DESCRIPTION_FR) String descriptionFr, @JsonProperty(TYPE) String type,
            @JsonProperty(BASE_RATE) double baseRate, @JsonProperty(BED_OPTIONS) String bedOptions,
            @JsonProperty(SLEEPS_COUNT) int sleepsCount, @JsonProperty(SMOKING_ALLOWED) boolean smokingAllowed,
            @JsonProperty(TAGS) List<String> tags) {
        return new com.microsoft.azure.search.samples.demo.AutoValue_Room(description, descriptionFr, type, baseRate,
                                                                          bedOptions, sleepsCount, smokingAllowed,
                                                                          tags);
    }
}
