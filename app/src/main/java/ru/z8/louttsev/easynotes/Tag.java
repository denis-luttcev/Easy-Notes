package ru.z8.louttsev.easynotes;

import androidx.annotation.NonNull;

import java.util.Objects;

class Tag {
    private String title;

    Tag(@NonNull String title) {
        if (!title.trim().isEmpty()) {
            this.title = title;
        }
    }

    @NonNull String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        Tag tag = (Tag) that;
        return title.equals(tag.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
