package ru.z8.louttsev.easynotes.DataModel;

import androidx.annotation.NonNull;

import java.util.Objects;

class Category {
    private String title;

    Category(@NonNull String title) {
        if (!title.trim().isEmpty()) {
            this.title = title;
        }
    }

    @NonNull
    String getTitle() {
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
