package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NotesRepository implements NotesKeeper {
    private Map<String, Category> categories;
    private Map<String, Tag> tags;
    private Map<UUID, Note> notes;

    public NotesRepository() {
        categories = new HashMap<>();
        tags = new HashMap<>();
        notes = new HashMap<>();
        readData();
    }

    /**
     * Creates new note match NoteType
     * Concrete class constructors are placed here
     * New type need declare in NoteType enum
     */
    @NonNull
    @Override
    public Note createNote(NoteType noteType) {
        switch (noteType) {
            case TEXT_NOTE:
                return new TextNote();
            default:
                return null; // unreachable
        }
    }

    private void readData() {
        //TODO: change to read from db

        try {
            addCategory("Holiday");
            addCategory("Work");
            addTag("Ideas");
            addTag("Todo");
            addTag("Photo");
            addTag("Smile");
            addTag("Class");
            addTag("Share");
            addTag("Common");
            addTag("Private");
            addTag("Plus");
            addTag("Native");

            Note note;
            note = new TextNote();
            note.setTitle("note1");
            note.setContent("note1 content");
            note.setColor(Note.Color.ATTENTION);
            note.setCategory(getCategory("Holiday"));
            //Calendar date = Calendar.getInstance();
            //note.setDeadline(date);
            addNote(note);

            note = new TextNote();
            note.setTitle(null);
            note.setContent("note2 content");
            note.setCategory(getCategory("Work"));
            note.markTag(getTag("Ideas"));
            //date = Calendar.getInstance();
            //date.add(Calendar.DATE, -1);
            //note.setDeadline(date);
            addNote(note);

            note = new TextNote();
            note.setTitle("note3");
            //noinspection SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection
            note.setContent("note3 long content: Lorem ipsum dolor sit amet, consectetur adipiscing elit. In varius malesuada neque sed pellentesque. Aenean sit amet luctus justo. Maecenas venenatis lorem sit amet orci ultricies maximus. Morbi sagittis neque vitae risus tristique tincidunt. Ut tellus lectus, tempor vitae iaculis quis, tempor non ex. Maecenas imperdiet pretium ligula ac rutrum. Mauris massa felis, vulputate eget sem et, ullamcorper convallis augue.");
            note.setColor(Note.Color.ACCESSORY);
            note.markTag(getTag("Ideas"));
            note.markTag(getTag("Todo"));
            note.markTag(getTag("Photo"));
            note.markTag(getTag("Smile"));
            note.markTag(getTag("Class"));
            note.markTag(getTag("Share"));
            note.markTag(getTag("Common"));
            note.markTag(getTag("Private"));
            note.markTag(getTag("Plus"));
            note.markTag(getTag("Native"));
            //date = Calendar.getInstance();
            //date.add(Calendar.DATE, 1);
            //note.setDeadline(date);
            addNote(note);
        } catch (IllegalAccessException ignored) {}
    }

    @NonNull
    @Override
    public Set<Category> getCategories() {
        return new HashSet<>(categories.values());
    }

    @Override
    public void addCategory(@NonNull String title) {
        try {
            categories.put(title, new Category(title));
        } catch (IllegalArgumentException ignored) {}
    }

    @Override
    public void removeCategory(@NonNull String title) {
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
        try {
            tags.put(title, new Tag(title));
        } catch (IllegalArgumentException ignored) {}
    }

    @Override
    public void removeTag(@NonNull String title) {
        for (Note note : notes.values()) {
            if (note.hasTag(title)) {
                note.unmarkTag(title);
            }
        }
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
        notes.put(note.getId(), note);
    }

    @Override
    public void removeNote(@NonNull UUID uuid) {
        notes.remove(uuid);
    }

    @Override
    public boolean containNote(@NonNull UUID uuid) {
        return notes.containsKey(uuid);
    }

    @NonNull
    @Override
    public Note getNote(@NonNull UUID uuid) throws IllegalAccessException {
        Note note = notes.get(uuid);
        if (note != null) {
            return note;
        } else throw new IllegalAccessException();
    }

    @NonNull
    @Override
    public Note getNote(int position) {
        //TODO: after release eliminate this performance bottleneck
        // (need collection that is auto sortable after changing item)
        Note[] notesArray = notes.values().toArray(new Note[getNotesCount()]);
        Arrays.sort(notesArray);
        return notesArray[position];
    }

    @Override
    public int getNotesCount() {
        return notes.size();
    }
}
