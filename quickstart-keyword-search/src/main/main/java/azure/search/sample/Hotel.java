// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package azure.search.sample;

import com.azure.search.documents.indexes.SearchableField;
import com.azure.search.documents.indexes.SimpleField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.time.OffsetDateTime;

/**
 * Model class representing a hotel.
 */
@JsonInclude(Include.NON_NULL)
public class Hotel {
    /**
     * Hotel ID
     */
    @JsonProperty("HotelId")
    @SimpleField(isKey = true)
    public String hotelId;

    /**
     * Hotel name
     */
    @JsonProperty("HotelName")
    @SearchableField(isSortable = true)
    public String hotelName;

    /**
     * Description
     */
    @JsonProperty("Description")
    @SearchableField(analyzerName = "en.microsoft")
    public String description;

    /**
     * Category
     */
    @JsonProperty("Category")
    @SearchableField(isFilterable = true, isSortable = true, isFacetable = true)
    public String category;

    /**
     * Tags
     */
    @JsonProperty("Tags")
    @SearchableField(isFilterable = true, isFacetable = true)
    public String[] tags;

    /**
     * Whether parking is included
     */
    @JsonProperty("ParkingIncluded")
    @SimpleField(isFilterable = true, isSortable = true, isFacetable = true)
    public Boolean parkingIncluded;

    /**
     * Last renovation time
     */
    @JsonProperty("LastRenovationDate")
    @SimpleField(isFilterable = true, isSortable = true, isFacetable = true)
    public OffsetDateTime lastRenovationDate;

    /**
     * Rating
     */
    @JsonProperty("Rating")
    @SimpleField(isFilterable = true, isSortable = true, isFacetable = true)
    public Double rating;

    /**
     * Address
     */
    @JsonProperty("Address")
    public Address address;

    @Override
    public String toString()
    {
        try
        {
            return new ObjectMapper().writeValueAsString(this);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
            return "";
        }
    }
}