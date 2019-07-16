package com.microsoft.azure.search.samples.options;

import com.google.auto.value.AutoValue;

import java.util.Optional;

@AutoValue
public abstract class SuggestOptions {
    public abstract Optional<String> filter();

    public abstract Optional<String> orderby();

    public abstract Optional<String> select();

    public abstract Optional<String> searchFields();

    public abstract Optional<String> highlightPreTag();

    public abstract Optional<String> highlightPostTag();

    public abstract Optional<Integer> top();

    public abstract Optional<Double> minimumCoverage();

    public abstract Boolean fuzzy();

    public static Builder builder() {
        return new com.microsoft.azure.search.samples.options.AutoValue_SuggestOptions.Builder().fuzzy(false);
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
