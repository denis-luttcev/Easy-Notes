package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NotesRepository implements NotesKeeper {
    private Set<Category> categories;
    private Set<Tag> tags;
    private List<Note> notes;

    public NotesRepository() {
        this.categories = new HashSet<>();
        this.tags = new HashSet<>();
        this.notes = new ArrayList<>();
    }

    @NonNull
    @Override
    public Set<Category> getCategories() {
        return categories;
    }

    @Override
    public void addCategory(@NonNull String title) {
        try {
            categories.add(new Category(title));
        } catch (IllegalArgumentException ignored) {}
    }

    @Override
    public void removeCategory(@NonNull String title) {
        try {
            categories.remove(new Category(title));
        } catch (IllegalArgumentException ignored) {}
    }

    @Override
    public boolean containCategory(@NonNull String title) {
        try {
            if (categories.contains(new Category(title))) {
                return true;
            }
        } catch (IllegalArgumentException ignored) {}
        return false;
    }

    @Nullable
    @Override
    public Category getCategory(@NonNull String title) {
        if (containCategory(title)) {
            return new Category(title);
        }
        return null;
    }

    @NonNull
    @Override
    public Set<Tag> getTags() {
        return tags;
    }

    @Override
    public void addTag(@NonNull String title) {
        try {
            tags.add(new Tag(title));
        } catch (IllegalArgumentException ignored) {}
    }

    @Override
    public void removeTag(@NonNull String title) {
        try {
            tags.remove(new Tag(title));
        } catch (IllegalArgumentException ignored) {}
    }

    @Override
    public boolean containTag(@NonNull String title) {
        try {
            if (tags.contains(new Tag(title))) {
                return true;
            }
        } catch (IllegalArgumentException ignored) {}
        return false;
    }

    @Nullable
    @Override
    public Tag getTag(@NonNull String title) {
        if (containTag(title)) {
            return new Tag(title);
        }
        return null;
    }

    @NonNull
    @Override
    public List<Note> getNotes() {
        return notes;
    }

    @Override
    public void addNote(@NonNull Note note) {
        int position = Collections.binarySearch(notes, note);
        if (position < 0) {
            position = -position - 1;
        }
        notes.add(position, note);
    }

    @Override
    public void removeNote(int position) {
        notes.remove(position);
    }

    @Override
    public boolean containNote(@NonNull UUID uuid) {
        for (Note note : notes) {
            if (note.getId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public Note getNote(int position) {
        return notes.get(position);
    }
}
