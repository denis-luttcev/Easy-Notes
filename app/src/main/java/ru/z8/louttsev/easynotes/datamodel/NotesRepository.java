package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
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
        readData();
    }

    private void readData() {
        //TODO: change to read from db

        Category category1 = new Category("Holiday");
        Category category2 = new Category("Work");
        Tag tag1 = new Tag("Ideas");
        Tag tag2 = new Tag("Todo");
        Tag tag3 = new Tag("Photo");
        Tag tag4 = new Tag("Smile");
        Tag tag5 = new Tag("Class");
        Tag tag6 = new Tag("Share");
        Tag tag7 = new Tag("Common");
        Tag tag8 = new Tag("Private");
        Tag tag9 = new Tag("Plus");
        Tag tag10 = new Tag("Native");

        Note note;
        note = new TextNote();
        note.setTitle("note1");
        note.setContent("note1 content");
        note.setColor(Note.Color.ATTENTION);
        note.setCategory(category1);
        addNote(note);

        note = new TextNote();
        note.setTitle(null);
        note.setContent("note2 content");
        note.setCategory(category2);
        note.markTag(tag1);
        addNote(note);

        note = new TextNote();
        //note.setTitle("note3");
        //noinspection SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection,SpellCheckingInspection
        note.setContent("note3 long content: Lorem ipsum dolor sit amet, consectetur adipiscing elit. In varius malesuada neque sed pellentesque. Aenean sit amet luctus justo. Maecenas venenatis lorem sit amet orci ultricies maximus. Morbi sagittis neque vitae risus tristique tincidunt. Ut tellus lectus, tempor vitae iaculis quis, tempor non ex. Maecenas imperdiet pretium ligula ac rutrum. Mauris massa felis, vulputate eget sem et, ullamcorper convallis augue.");
        note.setColor(Note.Color.ACCESSORY);
        note.markTag(tag1);
        note.markTag(tag2);
        note.markTag(tag3);
        note.markTag(tag4);
        note.markTag(tag5);
        note.markTag(tag6);
        note.markTag(tag7);
        note.markTag(tag8);
        note.markTag(tag9);
        note.markTag(tag10);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 1);
        note.setDeadline(date);
        addNote(note);
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
