package com.microsoft.azure.search.samples.index;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

@AutoValue
public abstract class SimpleIndexField implements IndexField {
    public abstract String name();

    public abstract String type();

    @Nullable
    public abstract String analyzer();

    @Nullable
    public abstract Boolean searchable();

    @Nullable
    public abstract Boolean filterable();

    @Nullable
    public abstract Boolean retrievable();

    @Nullable
    public abstract Boolean sortable();

    @Nullable
    public abstract Boolean facetable();

    @Nullable
    public abstract Boolean key();

    public static Builder builder(String name, String type) {
        return new com.microsoft.azure.search.samples.index.AutoValue_SimpleIndexField.Builder().name(name).type(type);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder name(String name);

        public abstract Builder type(String type);

        public abstract Builder analyzer(String analyzer);

        public abstract Builder searchable(Boolean searchable);

        public abstract Builder filterable(Boolean filterable);

        public abstract Builder retrievable(Boolean retrievable);

        public abstract Builder sortable(Boolean sortable);

        public abstract Builder facetable(Boolean facetable);

        public abstract Builder key(Boolean key);

        public abstract SimpleIndexField build();
    }
}
