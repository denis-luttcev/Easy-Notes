package ru.z8.louttsev.easynotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import ru.z8.louttsev.easynotes.datamodel.Category;
import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.Tag;

public class NotesStorageDB {
    private SQLiteDatabase db;

    private Map<String, Category> categories;
    private Map<String, Tag> tags;

    public NotesStorageDB(@NonNull Context context) {
        db = new NotesBaseHelper(context).getWritableDatabase();
    }


    @NonNull
    public static ContentValues getCategoryContentValues(@NonNull Category category) {
        ContentValues values = new ContentValues();

        values.put(NotesDBSchema.CategoriesTable.Cols.UUID, category.getId().toString());
        values.put(NotesDBSchema.CategoriesTable.Cols.TITLE, category.getTitle());

        return values;
    }

    @NonNull
    public Map<String, Category> loadCategories() {
        categories = new HashMap<>();

        try (CategoriesCursorWrapper cursor = queryCategories()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Category category = cursor.getCategory();
                categories.put(category.getTitle(), category);
                cursor.moveToNext();
            }
        }

        return categories;
    }

    @NonNull
    private CategoriesCursorWrapper queryCategories() {
        Cursor cursor = db.query(
                NotesDBSchema.CategoriesTable.NAME,
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
    private static ContentValues getTagContentValues (@NonNull Tag tag) {
        ContentValues values = new ContentValues();

        values.put(NotesDBSchema.TagsTable.Cols.UUID, tag.getId().toString());
        values.put(NotesDBSchema.TagsTable.Cols.TITLE, tag.getTitle());

        return values;
    }

    @NonNull
    public Map<String, Tag> loadTags() {
        tags = new HashMap<>();

        try (TagsCursorWrapper cursor = queryTags()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Tag tag = cursor.getTag();
                tags.put(tag.getTitle(), tag);
                cursor.moveToNext();
            }
        }

        return tags;
    }

    @NonNull
    private TagsCursorWrapper queryTags() {
        Cursor cursor = db.query(
                NotesDBSchema.TagsTable.NAME,
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
    private static ContentValues getNoteContentValues(@NonNull Note note) {
        ContentValues values = new ContentValues();

        values.put(NotesDBSchema.NotesTable.Cols.UUID, note.getId().toString());
        values.put(NotesDBSchema.NotesTable.Cols.TYPE, note.getType().ordinal());
        values.put(NotesDBSchema.NotesTable.Cols.TITLE, note.getTitle());
        if (note.isCategorized()) {
            values.put(NotesDBSchema.NotesTable.Cols.CATEGORY,
                    Objects.requireNonNull(note.getCategory()).getId().toString());
        } else values.put(NotesDBSchema.NotesTable.Cols.CATEGORY, "");

        values.put(NotesDBSchema.NotesTable.Cols.COLOR, note.getColor().ordinal());
        if (note.isDeadlined()) {
            values.put(NotesDBSchema.NotesTable.Cols.DEADLINE,
                    Objects.requireNonNull(note.getDeadline()).getTimeInMillis());
        } else values.put(NotesDBSchema.NotesTable.Cols.DEADLINE, 0);

        values.put(NotesDBSchema.NotesTable.Cols.LAST_MODIFICATION, note.getLastModification().getTimeInMillis());

        note.putContentForDB(NotesDBSchema.NotesTable.Cols.CONTENT, values);

        return values;
    }

    @NonNull
    public List<Note> loadNotes() {
        List<Note> notes = new ArrayList<>();
        //TODO: now
        try (NotesCursorWrapper cursor = queryNotes()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note note = cursor.getNote();
                UUID categoryId = cursor.getCategoryId();
                if (categoryId != null) {
                    note.setCategory(getCategory(categoryId));
                } else note.setCategory(null);
                note.setContentFromDB(NotesDBSchema.NotesTable.Cols.CONTENT, cursor);
                note.setLastModification(cursor.getLastModification());
                notes.add(note);
                cursor.moveToNext();
            }
        }

        return notes;
    }

    @NonNull
    private NotesCursorWrapper queryNotes() {
        Cursor cursor = db.query(
                NotesDBSchema.NotesTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return new NotesCursorWrapper(cursor);
    }

    @NonNull
    private static ContentValues getTaggingContentValues(@NonNull Note note, @NonNull Tag tag) {
        ContentValues values = new ContentValues();

        values.put(NotesDBSchema.TaggingTable.Cols.NOTE, note.getId().toString());
        values.put(NotesDBSchema.TaggingTable.Cols.TAG, tag.getId().toString());

        return values;
    }

    public void loadTagging() {
        try (TaggingCursorWrapper cursor = queryTagging()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note note = getNote(cursor.getNoteId());
                Calendar lastModification = note.getLastModification(); // save sorting criteria
                Tag tag = getTag(cursor.getTagId());
                note.markTag(tag);
                note.setLastModification(lastModification); // restore sorting criteria
                cursor.moveToNext();
            }
        } catch (IllegalAccessException ignored) {} // impossible
    }


    @NonNull
    private TaggingCursorWrapper queryTagging() {
        Cursor cursor = db.query(
                NotesDBSchema.TaggingTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return new TaggingCursorWrapper(cursor);
    }
}
