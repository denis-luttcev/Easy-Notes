package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

public class Category {
    /**
     * Unique id, null not allowable
     */
    private UUID id;
    /**
     * Allowable: any, exclude null and empty (senselessly)
     */
    private String title;

    /**
     * @throws IllegalArgumentException if title is empty
     */
    Category(@NonNull String title) throws IllegalArgumentException {
        this(UUID.randomUUID());
        if (!title.isEmpty()) {
            this.title = title;
        } else throw new IllegalArgumentException();
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

    public void setTitle(String title) throws IllegalArgumentException {
        if (!title.isEmpty()) {
            this.title = title;
        } else throw new IllegalArgumentException();
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
