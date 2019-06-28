package com.microsoft.azure.search.samples.options;

import com.google.auto.value.AutoValue;

import java.util.Optional;

@AutoValue
public abstract class SearchOptions {
    public abstract Optional<Boolean> includeCount();

    public abstract Optional<String> filter();

    public abstract Optional<String> orderBy();

    public abstract Optional<String> select();

    public abstract Optional<Integer> top();

    public static Builder builder() {
        return new com.microsoft.azure.search.samples.options.AutoValue_SearchOptions.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder includeCount(boolean includeCount);

        public abstract Builder filter(String filter);

        public abstract Builder orderBy(String orderBy);

        public abstract Builder select(String select);

        public abstract Builder top(Integer top);

        public abstract SearchOptions build();
    }
}
