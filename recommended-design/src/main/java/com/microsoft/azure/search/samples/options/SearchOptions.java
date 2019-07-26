package com.microsoft.azure.search.samples.options;

import com.google.auto.value.AutoValue;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AutoValue
public abstract class SearchOptions {
    public static Builder builder() {
        return new com.microsoft.azure.search.samples.options.AutoValue_SearchOptions.Builder()
                .facets(new ArrayList<>())
                .scoringParameters(new ArrayList<>())
                .requireAllTerms(false);
    }

    public abstract Optional<Boolean> includeCount();

    public abstract Optional<String> filter();

    public abstract Optional<String> orderBy();

    public abstract Optional<String> select();

    public abstract Optional<String> searchFields();

    public abstract List<String> facets();

    public abstract Optional<String> highlight();

    public abstract Optional<String> highlightPreTag();

    public abstract Optional<String> highlightPostTag();

    public abstract Optional<String> scoringProfile();

    public abstract List<String> scoringParameters();

    public abstract Optional<Integer> top();

    public abstract Optional<Integer> skip();

    public abstract Boolean requireAllTerms();

    public abstract Optional<Double> minimumCoverage();

    private String optionalQueryParam(String queryKeyName, Optional accessor) {
        if (accessor.isPresent()) {
            var encodedParam = URLEncoder.encode(accessor.get().toString(), StandardCharsets.UTF_8);
            return String.format("&%s=%s", queryKeyName, encodedParam);
        } else {
            return "";
        }
    }

    public String toQueryParameters() {
        var sb = new StringBuilder();

        var optionalQueryParams = new String[]{
                optionalQueryParam("$filter", filter()),
                optionalQueryParam("$orderBy", orderBy()),
                optionalQueryParam("$select", select()),
                optionalQueryParam("searchFields", searchFields()),
                optionalQueryParam("highlight", highlight()),
                optionalQueryParam("highlightPreTag", highlightPreTag()),
                optionalQueryParam("highlightPostTag", highlightPostTag()),
                optionalQueryParam("scoringProfile", scoringProfile()),
                optionalQueryParam("$top", top()),
                optionalQueryParam("$skip", skip()),
                optionalQueryParam("minimumCoverage", minimumCoverage())
        };

        Arrays.stream(optionalQueryParams).forEach(sb::append);
        facets().stream()
                .map(Optional::of) // Helper expects Optional
                .map(o -> optionalQueryParam("facet", o)) // Create query param
                .forEach(sb::append); //Append to StringBuilder
        scoringParameters().stream()
                .map(Optional::of) // Helper expects Optional
                .map(o -> optionalQueryParam("scoringParameter", o)) // Create query param
                .forEach(sb::append); //Append to StringBuilder

        if (requireAllTerms()) {
            sb.append("&searchMode=all");
        }

        if (includeCount().isPresent()) {
            sb.append(optionalQueryParam("$count", includeCount()));
        }

        return sb.toString();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder includeCount(boolean includeCount);

        public abstract Builder filter(String filter);

        public abstract Builder orderBy(String orderBy);

        public abstract Builder select(String select);

        public abstract Builder searchFields(String searchFields);

        public abstract Builder facets(List<String> facets);

        public abstract Builder highlight(String highlight);

        public abstract Builder highlightPreTag(String highlightPreTag);

        public abstract Builder highlightPostTag(String highlightPostTag);

        public abstract Builder scoringProfile(String scoringProfile);

        public abstract Builder scoringParameters(List<String> scoringParameters);

        public abstract Builder top(Integer top);

        public abstract Builder skip(Integer skip);

        public abstract Builder requireAllTerms(Boolean requireAllTerms);

        public abstract Builder minimumCoverage(Double minimumCoverage);

        public abstract SearchOptions build();
    }
}
