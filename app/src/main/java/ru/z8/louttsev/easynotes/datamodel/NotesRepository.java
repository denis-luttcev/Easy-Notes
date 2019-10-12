package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class NotesRepository implements NotesKeeper {
    private Set<Category> categories;
    private Set<Tag> tags;
    private SortedMap<UUID, Note> notes;

    public NotesRepository() {
        this.categories = new HashSet<>();
        this.tags = new HashSet<>();
        this.notes = new TreeMap<>();
    }

    @Nullable
    @Override
    public Set<Category> getCategories() {
        if (!categories.isEmpty()) {
            return categories;
        }
        return null;
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

    @Nullable
    @Override
    public Set<Tag> getTags() {
        if (!tags.isEmpty()) {
            return tags;
        }
        return null;
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

    @Override
    public void addNote(@NonNull Note note) {
        notes.put(note.getId(), note);
    }

    @Override
    public void removeNote(@NonNull UUID uuid) {
        if (containNote(uuid)) {
            notes.remove(uuid);
        }
    }

    @Override
    public boolean containNote(@NonNull UUID uuid) {
        return notes.containsKey(uuid);
    }

    @Nullable
    @Override
    public Note getNote(@NonNull UUID uuid) {
        if (containNote(uuid)) {
            return notes.get(uuid);
        }
        return null;
    }
}
