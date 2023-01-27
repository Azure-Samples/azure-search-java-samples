// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package azure.search.sample;

import com.azure.search.documents.indexes.SearchableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Model class representing an address.
 */
@JsonInclude(Include.NON_NULL)
public class Address {
    /**
     * Street address
     */
    @JsonProperty("StreetAddress")
    @SearchableField
    public String streetAddress;

    /**
     * City
     */
    @JsonProperty("City")
    @SearchableField(isFilterable = true, isSortable = true, isFacetable = true)
    public String city;

    /**
     * State or province
     */
    @JsonProperty("StateProvince")
    @SearchableField(isFilterable = true, isSortable = true, isFacetable = true)
    public String stateProvince;

    /**
     * Postal code
     */
    @JsonProperty("PostalCode")
    @SearchableField(isFilterable = true, isSortable = true, isFacetable = true)
    public String postalCode;

    /**
     * Country
     */
    @JsonProperty("Country")
    @SearchableField(isFilterable = true, isSortable = true, isFacetable = true)
    public String country;
}