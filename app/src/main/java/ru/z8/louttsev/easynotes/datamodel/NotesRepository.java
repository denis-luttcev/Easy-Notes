package ru.z8.louttsev.easynotes.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import ru.z8.louttsev.easynotes.database.CategoriesCursorWrapper;
import ru.z8.louttsev.easynotes.database.NotesBaseHelper;
import ru.z8.louttsev.easynotes.database.NotesCursorWrapper;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.CategoriesTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.NotesTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.TaggingTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.TagsTable;
import ru.z8.louttsev.easynotes.database.TaggingCursorWrapper;
import ru.z8.louttsev.easynotes.database.TagsCursorWrapper;

public class NotesRepository implements NotesKeeper {
    private SQLiteDatabase db;

    private Map<String, Category> categories;
    private Map<String, Tag> tags;
    private List<Note> notes;
    private Map<UUID, Note> index;

    public NotesRepository(@NonNull Context context) {
        db = new NotesBaseHelper(context).getWritableDatabase();

        categories = new HashMap<>();
        tags = new HashMap<>();
        notes = new ArrayList<>();
        index = new HashMap<>();

        loadData();
    }

    private void loadData() {
        loadCategories();
        loadTags();
        loadNotes();
        loadTagging();
    }

    private void loadCategories() {
        try (CategoriesCursorWrapper cursor = queryCategories()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Category category = cursor.getCategory();
                categories.put(category.getTitle(), category);
                cursor.moveToNext();
            }
        }
    }

    private void loadTags() {
        try (TagsCursorWrapper cursor = queryTags()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Tag tag = cursor.getTag();
                tags.put(tag.getTitle(), tag);
                cursor.moveToNext();
            }
        }
    }

    private void loadNotes() {
        try (NotesCursorWrapper cursor = queryNotes()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note note = cursor.getNote();
                putNote(note);
                cursor.moveToNext();
            }
        }
    }


    private void loadTagging() {
        try (TaggingCursorWrapper cursor = queryTagging()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note note = cursor.getNote();
                Calendar lastModification = note.getLastModification(); // save sorting criteria
                Tag tag = cursor.getTag();
                note.markTag(tag);
                note.setLastModification(lastModification); // restore sorting criteria
                cursor.moveToNext();
            }
        } catch (IllegalAccessException ignored) {} // impossible
    }

    @NonNull
    private TaggingCursorWrapper queryTagging() {
        Cursor cursor = db.query(
                TaggingTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return new TaggingCursorWrapper(cursor, this);
    }

    @NonNull
    private NotesCursorWrapper queryNotes() {
        Cursor cursor = db.query(
                NotesTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return new NotesCursorWrapper(cursor, this);
    }

    @NonNull
    private CategoriesCursorWrapper queryCategories() {
        Cursor cursor = db.query(
                CategoriesTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return new CategoriesCursorWrapper(cursor);
    }

    @NonNull
    private TagsCursorWrapper queryTags() {
        Cursor cursor = db.query(
                TagsTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return new TagsCursorWrapper(cursor);
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
                db.insert(CategoriesTable.NAME, null, getCategoryContentValues(category));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public void removeCategory(@NonNull String title) {
        for (Note note : notes) {
            if (note.hasCategory(title)) {
                note.setCategory(null);
                ContentValues values = new ContentValues();
                values.put(NotesTable.Cols.CATEGORY, "");
                try {
                    db.update(NotesTable.NAME,
                            values,
                            NotesTable.Cols.CATEGORY + " = ?",
                            new String[] { getCategory(title).getId().toString() });
                } catch (IllegalAccessException ignored) {} // impossible
            }
        }
        categories.remove(title);
        db.delete(CategoriesTable.NAME,
                CategoriesTable.Cols.TITLE + " = ?",
                new String[] { title });
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
    public Category getCategory(@NonNull UUID id) throws IllegalArgumentException {
        for (Category category : categories.values()) {
            if (category.getId().equals(id)) {
                return category;
            }
        }
        throw new IllegalArgumentException();
    }

    @NonNull
    private static ContentValues getCategoryContentValues (@NonNull Category category) {
        ContentValues values = new ContentValues();

        values.put(CategoriesTable.Cols.UUID, category.getId().toString());
        values.put(CategoriesTable.Cols.TITLE, category.getTitle());

        return values;
    }

    @NonNull
    @Override
    public Set<Tag> getTags() {
        return new HashSet<>(tags.values());
    }

    @NonNull
    private static ContentValues getTaggingContentValues(@NonNull Note note, @NonNull Tag tag) {
        ContentValues values = new ContentValues();

        values.put(TaggingTable.Cols.NOTE, note.getId().toString());
        values.put(TaggingTable.Cols.TAG, tag.getId().toString());

        return values;
    }

    @Override
    public void addTag(@NonNull String title) {
        if (!containTag(title)) {
            try {
                Tag tag = new Tag(title);
                tags.put(title, tag);
                db.insert(TagsTable.NAME, null, getTagContentValues(tag));
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
            db.delete(TaggingTable.NAME,
                    TaggingTable.Cols.TAG + " = ?",
                    new String[] { getTag(title).getId().toString() });
        } catch (IllegalAccessException ignored) {} // impossible
        tags.remove(title);
        db.delete(TagsTable.NAME,
                TagsTable.Cols.TITLE + " = ?",
                new String[] { title });
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

    @NonNull
    public Tag getTag(@NonNull UUID id) throws IllegalArgumentException {
        for (Tag tag : tags.values()) {
            if (tag.getId().equals(id)) {
                return tag;
            }
        }
        throw new IllegalArgumentException();
    }

    @NonNull
    private static ContentValues getTagContentValues (@NonNull Tag tag) {
        ContentValues values = new ContentValues();

        values.put(TagsTable.Cols.UUID, tag.getId().toString());
        values.put(TagsTable.Cols.TITLE, tag.getTitle());

        return values;
    }

    @Override
    public void addNote(@NonNull Note note) {
        if (containNote(note.getId())) {
            removeNote(note.getId());
            db.delete(TaggingTable.NAME,
                    TaggingTable.Cols.NOTE + " = ?",
                    new String[] { note.getId().toString() });
        }
        putNote(note);
        db.insert(NotesTable.NAME, null, getNoteContentValues(note));
        if (note.isTagged()) {
            makeTagging(note);
        }
    }

    private void makeTagging(@NonNull Note note) {
        Set<Tag> tags = note.getTags();

        for (Tag tag : tags) {
            db.insert(TaggingTable.NAME, null, getTaggingContentValues(note, tag));
        }
    }

    @Override
    public void removeNote(@NonNull UUID id) {
        db.delete(TaggingTable.NAME,
                TaggingTable.Cols.NOTE + " = ?",
                new String[] { id.toString() });
        notes.remove(index.get(id));
        index.remove(id);
        db.delete(NotesTable.NAME,
                NotesTable.Cols.UUID + " = ?",
                new String[] { id.toString() });
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
    private static ContentValues getNoteContentValues(@NonNull Note note) {
        ContentValues values = new ContentValues();

        values.put(NotesTable.Cols.UUID, note.getId().toString());
        values.put(NotesTable.Cols.TYPE, note.getType().ordinal());
        values.put(NotesTable.Cols.TITLE, note.getTitle());
        if (note.isCategorized()) {
            values.put(NotesTable.Cols.CATEGORY,
                    Objects.requireNonNull(note.getCategory()).getId().toString());
        } else values.put(NotesTable.Cols.CATEGORY, "");

        values.put(NotesTable.Cols.COLOR, note.getColor().ordinal());
        if (note.isDeadlined()) {
            values.put(NotesTable.Cols.DEADLINE,
                    Objects.requireNonNull(note.getDeadline()).getTimeInMillis());
        } else values.put(NotesTable.Cols.DEADLINE, 0);

        values.put(NotesTable.Cols.LAST_MODIFICATION, note.getLastModification().getTimeInMillis());

        note.putContentForDB(NotesTable.Cols.CONTENT, values);

        return values;
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
