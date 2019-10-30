package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

public class Tag {
    private UUID id;
    /**
     * Allowable: any, exclude null ald empty (senselessly)
     */
    private String title;

    /**
     * @throws IllegalArgumentException if title is empty
     */
    public Tag(@NonNull String title) throws IllegalArgumentException {
        if (!title.isEmpty()) {
            id = UUID.randomUUID();
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
        Tag tag = (Tag) that;
        return title.equals(tag.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
