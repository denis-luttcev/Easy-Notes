package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Category {
    private String title;

    public Category(@NonNull String title) throws IllegalArgumentException {
        if (!title.trim().isEmpty()) {
            this.title = title;
        } else throw new IllegalArgumentException();
    }

    @NonNull
    public String getTitle() {
        return title;
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
