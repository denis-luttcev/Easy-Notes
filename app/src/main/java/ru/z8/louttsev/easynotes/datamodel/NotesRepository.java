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

import ru.z8.louttsev.easynotes.database.NotesStorageDB;

public class NotesRepository implements NotesKeeper {
    private NotesStorageDB storage;

    private Map<String, Category> categories;
    private Map<String, Tag> tags;
    private List<Note> notes;
    private Map<UUID, Note> index;

    public NotesRepository(@NonNull Context context) {
        storage = new NotesStorageDB(context);

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
        if (!containCategory(title)) {
            try {
                Category category = new Category(title);
                categories.put(title, category);
                storage.insertCategory(category);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public void removeCategory(@NonNull String title) {
        for (Note note : notes) {
            if (note.hasCategory(title)) {
                note.setCategory(null);
                try {
                    storage.removeNoteCategory(getCategory(title));
                } catch (IllegalAccessException ignored) {} // impossible
            }
        }
        try {
            storage.deleteCategory(getCategory(title));
        } catch (IllegalAccessException ignored) {} // impossible
        categories.remove(title);
    }

    @Override
    public boolean containCategory(@NonNull String title) {
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
        if (!containTag(title)) {
            try {
                Tag tag = new Tag(title);
                tags.put(title, tag);
                storage.insertTag(tag);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public void removeTag(@NonNull String title) {
        for (Note note : notes) {
            if (note.hasTag(title)) {
                note.unmarkTag(title);
            }
        }
        try {
            storage.removeTagging(getTag(title));
        } catch (IllegalAccessException ignored) {} // impossible
        try {
            storage.deleteTag(getTag(title));
        } catch (IllegalAccessException ignored) {} // impossible
        tags.remove(title);
    }

    @Override
    public boolean containTag(@NonNull String title) {
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
        if (containNote(note.getId())) {
            removeNote(note.getId());
            storage.removeTagging(note.getId());
        }
        putNote(note);
        storage.insertNote(note);
        if (note.isTagged()) {
            makeTagging(note);
        }
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

    @Override
    public boolean containNote(@NonNull UUID id) {
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
