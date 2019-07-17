package com.microsoft.azure.search.samples.options;

import com.google.auto.value.AutoValue;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@AutoValue
public abstract class SuggestOptions {
    public static Builder builder() {
        return new com.microsoft.azure.search.samples.options.AutoValue_SuggestOptions.Builder().fuzzy(false);
    }

    public abstract Optional<String> filter();

    public abstract Optional<String> orderby();

    public abstract Optional<String> select();

    public abstract Optional<String> searchFields();

    public abstract Optional<String> highlightPreTag();

    public abstract Optional<String> highlightPostTag();

    public abstract Optional<Integer> top();

    public abstract Optional<Double> minimumCoverage();

    public abstract Boolean fuzzy();

    private String optionalQueryParam(String queryKeyName, Optional accessor) {
        if (accessor.isPresent()) {
            var s = String.format("&%s=%s", queryKeyName, accessor.get().toString());
            return URLEncoder.encode(s, StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }

    public String toQueryParameters() {
        var sb = new StringBuilder();

        var optionalQueryParams = new String[]{
                optionalQueryParam("$filter", filter()),
                optionalQueryParam("$orderBy", orderby()),
                optionalQueryParam("$select", select()),
                optionalQueryParam("searchFields", searchFields()),
                optionalQueryParam("highlightPreTag", highlightPreTag()),
                optionalQueryParam("highlightPostTag", highlightPostTag()),
                optionalQueryParam("$top", top()),
                optionalQueryParam("minimumCoverage", minimumCoverage())
        };

        Arrays.stream(optionalQueryParams).forEach(sb::append);
        if (fuzzy()) {
            sb.append("&fuzzy=true");
        }

        return sb.toString();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder filter(String filter);

        public abstract Builder orderby(String orderBy);

        public abstract Builder select(String select);

        public abstract Builder searchFields(String searchFields);

        public abstract Builder highlightPreTag(String highlightPreTag);

        public abstract Builder highlightPostTag(String highlightPostTag);

        public abstract Builder top(Integer top);

        public abstract Builder minimumCoverage(Double minimumCoverage);

        public abstract Builder fuzzy(Boolean fuzzy);

        public abstract SuggestOptions build();
    }
}
