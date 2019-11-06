package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

public class Category {
    private final UUID id;
    private String title;

    Category(@NonNull String title) {
        this(UUID.randomUUID());
        this.title = title;
    }

    public Category(@NonNull UUID id) {
        this.id = id;
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        Category category = (Category) that;
        return title.equals(category.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
