package ru.z8.louttsev.easynotes.datamodel;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import ru.z8.louttsev.easynotes.database.NotesDatabaseStorage;
import ru.z8.louttsev.easynotes.database.NotesStorage;

public class NotesRepository implements NotesKeeper {
    private final NotesStorage storage; // long term storage

    // short term storage and operations in memory
    private final Map<String, Category> categories;
    private final Map<String, Tag> tags;
    private final List<Note> notes; // sorted collection
    private final Map<UUID, Note> index; // indexed collection

    public NotesRepository(@NonNull Context context) {
        storage = new NotesDatabaseStorage(context);

        categories = storage.loadCategories();
        tags = storage.loadTags();
        notes = storage.loadNotes();

        index = new HashMap<>();
        for (Note note : notes) {
            index.put(note.getId(), note);
        }

        storage.loadTagging();
    }

    @NonNull
    @Override
    public Set<Category> getCategories() {
        return new HashSet<>(categories.values());
    }

    @Override
    public void addCategory(@NonNull String title) {
        if (!title.isEmpty() && !containCategory(title)) {
            Category category = new Category(title);
            categories.put(title, category);
            storage.insertCategory(category);
        }
    }

    @Override
    public void removeCategory(@NonNull String title) {
        try {
            Category category = getCategory(title);

            for (Note note : notes) {
                if (note.hasCategory(title)) {
                    note.setCategory(null);
                    storage.clearCategoryFromNotes(category);
                }
            }

            storage.deleteCategory(category);
            categories.remove(title);

        } catch (IllegalAccessException ignored) {} // impossible
    }

    private boolean containCategory(@NonNull String title) {
        return categories.containsKey(title);
    }

    @NonNull
    @Override
    public Category getCategory(@NonNull String title) throws IllegalAccessException {
        Category category = categories.get(title);

        if (category != null) {
            return category;
        } else throw new IllegalAccessException();
    }

    @NonNull
    @Override
    public Set<Tag> getTags() {
        return new HashSet<>(tags.values());
    }

    @Override
    public void addTag(@NonNull String title) {
        if (!title.isEmpty() && !containTag(title)) {
            Tag tag = new Tag(title);
            tags.put(title, tag);
            storage.insertTag(tag);
        }
    }

    @Override
    public void removeTag(@NonNull String title) {
        try {
            Tag tag = getTag(title);

            for (Note note : notes) {
                if (note.hasTag(title)) {
                    note.unmarkTag(title);
                }
            }

            storage.removeTagging(tag);
            storage.deleteTag(tag);
            tags.remove(title);

        } catch (IllegalAccessException ignored) {} // impossible
    }

    private boolean containTag(@NonNull String title) {
        return tags.containsKey(title);
    }

    @NonNull
    @Override
    public Tag getTag(@NonNull String title) throws IllegalAccessException {
        Tag tag = tags.get(title);

        if (tag != null) {
            return tag;
        } else throw new IllegalAccessException();
    }

    @Override
    public void addNote(@NonNull Note note) {
        UUID id = note.getId();

        if (containNote(id)) {
            removeNote(id);
            storage.removeTagging(id);
        }

        putNote(note);
        storage.insertNote(note);

        if (note.isTagged()) {
            makeTagging(note);
        }

        note.dropModified();
    }

    private void makeTagging(@NonNull Note note) {
        Set<Tag> tags = note.getTags();

        for (Tag tag : tags) {
            storage.insertTagging(note, tag);
        }
    }

    @Override
    public void removeNote(@NonNull UUID id) {
        storage.removeTagging(id);
        notes.remove(index.get(id));
        index.remove(id);
        storage.deleteNote(id);
    }

    private boolean containNote(@NonNull UUID id) {
        return index.containsKey(id);
    }

    @NonNull
    @Override
    public Note getNote(@NonNull UUID id) throws IllegalAccessException {
        Note note = index.get(id);

        if (note != null) {
            return note;
        } else throw new IllegalAccessException();
    }

    @NonNull
    @Override
    public Note getNote(int position) {
        return notes.get(position);
    }

    @Override
    public int getNotesCount() {
        return notes.size();
    }

    private void putNote(Note note) {
        index.put(note.getId(), note);

        int position = Collections.binarySearch(notes, note);
        if (position < 0) {
            position = 0 - (position + 1);
        }

        notes.add(position, note);
    }
}
