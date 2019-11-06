package ru.z8.louttsev.easynotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import ru.z8.louttsev.easynotes.database.NotesDBSchema.CategoriesTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.NotesTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.TaggingTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.TagsTable;
import ru.z8.louttsev.easynotes.datamodel.Category;
import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.Tag;

public class NotesDatabaseStorage implements NotesStorage {
    private final SQLiteDatabase db;

    private Map<String, Category> categories;
    private Map<String, Tag> tags;
    private Map<UUID, Note> notes;

    public NotesDatabaseStorage(@NonNull Context context) {
        db = new NotesBaseHelper(context).getWritableDatabase();
    }

    @NonNull
    private ContentValues getCategoryContentValues(@NonNull Category category) {
        ContentValues values = new ContentValues();

        values.put(CategoriesTable.Cols.UUID, category.getId().toString());
        values.put(CategoriesTable.Cols.TITLE, category.getTitle());

        return values;
    }

    @NonNull
    @Override
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

    @Nullable
    private Category getCategory(@Nullable UUID id) {
        if (id != null) {
            for (Category category : categories.values()) {
                if (category.getId().equals(id)) {
                    return category;
                }
            }
        } return null;
    }

    @Override
    public void insertCategory(@NonNull Category category) {
        db.insert(CategoriesTable.NAME, null, getCategoryContentValues(category));
    }

    @Override
    public void deleteCategory(@NonNull Category category) {
        db.delete(CategoriesTable.NAME,
                CategoriesTable.Cols.UUID + " = ?",
                new String[] { category.getId().toString() });
    }

    @NonNull
    private ContentValues getTagContentValues (@NonNull Tag tag) {
        ContentValues values = new ContentValues();

        values.put(TagsTable.Cols.UUID, tag.getId().toString());
        values.put(TagsTable.Cols.TITLE, tag.getTitle());

        return values;
    }

    @NonNull
    @Override
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
    private Tag getTag(@NonNull UUID id) throws IllegalArgumentException {
        for (Tag tag : tags.values()) {
            if (tag.getId().equals(id)) {
                return tag;
            }
        }
        throw new IllegalArgumentException(); // unreachable
    }

    @Override
    public void insertTag(@NonNull Tag tag) {
        db.insert(TagsTable.NAME, null, getTagContentValues(tag));
    }

    @Override
    public void deleteTag(@NonNull Tag tag) {
        db.delete(TagsTable.NAME,
                TagsTable.Cols.UUID + " = ?",
                new String[] { tag.getId().toString() });
    }

    @NonNull
    private ContentValues getNoteContentValues(@NonNull Note note) {
        ContentValues values = new ContentValues();

        values.put(NotesTable.Cols.UUID, note.getId().toString());
        values.put(NotesTable.Cols.TYPE, note.getType().ordinal());
        values.put(NotesTable.Cols.TITLE, note.getTitle());

        if (note.isCategorized()) {
            values.put(NotesTable.Cols.CATEGORY,
                    Objects.requireNonNull(note.getCategory()).getId().toString()); // checked
        } else values.put(NotesTable.Cols.CATEGORY, "");

        values.put(NotesTable.Cols.COLOR, note.getColor().ordinal());

        if (note.isDeadlined()) {
            values.put(NotesTable.Cols.DEADLINE,
                    Objects.requireNonNull(note.getDeadline()).getTimeInMillis()); // checked
        } else values.put(NotesTable.Cols.DEADLINE, 0);

        values.put(NotesTable.Cols.LAST_MODIFICATION, note.getLastModification().getTimeInMillis());
        note.getContentForDB(NotesTable.Cols.CONTENT, values);

        return values;
    }

    @Override
    @NonNull
    public List<Note> loadNotes() {
        notes = new HashMap<>();

        try (NotesCursorWrapper cursor = queryNotes()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note note = cursor.getNote();

                note.setCategory(getCategory(cursor.getCategoryId()));
                note.setContentFromDB(NotesTable.Cols.CONTENT, cursor);
                note.setLastModification(cursor.getLastModification());

                notes.put(note.getId(), note);

                cursor.moveToNext();
            }
        }

        List<Note> notes = new ArrayList<>(this.notes.values());
        Collections.sort(notes);

        return notes;
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

        return new NotesCursorWrapper(cursor);
    }

    @Override
    public void insertNote(@NonNull Note note) {
        db.insert(NotesTable.NAME, null, getNoteContentValues(note));
    }

    @Override
    public void deleteNote(@NonNull UUID id) {
        db.delete(NotesTable.NAME,
                NotesTable.Cols.UUID + " = ?",
                new String[] { id.toString() });
    }

    @Override
    public void clearCategoryFromNotes(@NonNull Category category) {
        ContentValues values = new ContentValues();

        values.put(NotesTable.Cols.CATEGORY, "");

        db.update(NotesTable.NAME,
                values,
                NotesTable.Cols.CATEGORY + " = ?",
                new String[] { category.getId().toString() });
    }

    @NonNull
    private ContentValues getTaggingContentValues(@NonNull Note note, @NonNull Tag tag) {
        ContentValues values = new ContentValues();

        values.put(TaggingTable.Cols.NOTE, note.getId().toString());
        values.put(TaggingTable.Cols.TAG, tag.getId().toString());

        return values;
    }

    @Override
    public void loadTagging() {
        try (TaggingCursorWrapper cursor = queryTagging()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note note = notes.get(cursor.getNoteId());

                // save sorting criteria
                Calendar lastModification
                        = Objects.requireNonNull(note).getLastModification(); // always set

                Tag tag = getTag(cursor.getTagId());
                note.markTag(tag);

                // restore sorting criteria
                note.setLastModification(lastModification);

                cursor.moveToNext();
            }
        } catch (NullPointerException ignored) {} // impossible
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

        return new TaggingCursorWrapper(cursor);
    }

    @Override
    public void removeTagging(@NonNull Tag tag) {
        db.delete(TaggingTable.NAME,
                TaggingTable.Cols.TAG + " = ?",
                new String[]{tag.getId().toString()});
    }

    @Override
    public void removeTagging(@NonNull UUID noteId) {
        db.delete(TaggingTable.NAME,
                TaggingTable.Cols.NOTE + " = ?",
                new String[] { noteId.toString() });
    }

    @Override
    public void insertTagging(@NonNull Note note, @NonNull Tag tag) {
        db.insert(TaggingTable.NAME, null, getTaggingContentValues(note, tag));
    }
}
