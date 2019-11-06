package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

public class Tag {
    private final UUID id;
    private String title;

    Tag(@NonNull String title) {
        this(UUID.randomUUID());
        this.title = title;
    }

    public Tag(@NonNull UUID id) {
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
        Tag tag = (Tag) that;
        return title.equals(tag.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
