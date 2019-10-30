package ru.z8.louttsev.easynotes.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import ru.z8.louttsev.easynotes.database.CategoriesCursorWrapper;
import ru.z8.louttsev.easynotes.database.NotesBaseHelper;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.CategoriesTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.NotesTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.TaggingTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.TagsTable;

public class NotesRepository implements NotesKeeper {
    private SQLiteDatabase db;

    private Map<String, Category> categories;
    private Map<String, Tag> tags;
    private Map<UUID, Note> notes;

    public NotesRepository(@NonNull Context context) {
        db = new NotesBaseHelper(context).getWritableDatabase();

        categories = new HashMap<>();
        tags = new HashMap<>();
        notes = new HashMap<>();

        readData();
        readSamples();
    }

    /**
     * Creates new note match NoteType
     * New type need declare in NoteType enum
     */
    @NonNull
    @Override
    public Note createNote(NoteType noteType) {
        switch (noteType) {
            case TEXT_NOTE:
                return new TextNote();
            //TODO: New concrete class constructors are placed here
            default:
                return null; // unreachable
        }
    }

    private void readData() {
        readCategories();
    }

    private void readCategories() {
        try (CategoriesCursorWrapper cursor = queryCategories()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Category category = cursor.getCategory();
                categories.put(category.getTitle(), category);
                cursor.moveToNext();
            }
        }
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

    private void readSamples() {
        //TODO: change to read from db

        try {
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
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, 1);
            note.setDeadline(date);
            addNote(note);

            note = new TextNote();
            note.setTitle("");
            note.setContent("note2 content");
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
        for (Note note : notes.values()) {
            if (note.hasCategory(title)) {
                note.setCategory(null);
            }
        }
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
        }

        notes.put(note.getId(), note);
        db.insert(NotesTable.NAME, null, getNoteContentValues(note));
        if (note.isTagged()) {
            doTagging(note);
        }
    }

    private void doTagging(@NonNull Note note) {
        Set<Tag> tags = note.getTags();

        for (Tag tag : tags) {
            ContentValues values = new ContentValues();
            values.put(TaggingTable.Cols.NOTE, note.getId().toString());
            values.put(TaggingTable.Cols.TAG, tag.getId().toString());
            db.insert(TaggingTable.NAME, null, values);
        }
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
    private static ContentValues getNoteContentValues(@NonNull Note note) {
        ContentValues values = new ContentValues();

        values.put(NotesTable.Cols.UUID, note.getId().toString());
        values.put(NotesTable.Cols.TITLE, note.getTitle());
        if (note.isCategorized()) {
            values.put(NotesTable.Cols.CATEGORY,
                    Objects.requireNonNull(note.getCategory()).getId().toString());
        } else {
            values.putNull(NotesTable.Cols.CATEGORY);
        }
        values.put(NotesTable.Cols.COLOR, note.getColor().ordinal());
        if (note.isDeadlined()) {
            values.put(NotesTable.Cols.DEADLINE,
                    Objects.requireNonNull(note.getDeadline()).getTimeInMillis());
        } else {
            values.putNull(NotesTable.Cols.DEADLINE);
        }
        values.put(NotesTable.Cols.LAST_MODIFICATION, note.getLastModification().getTimeInMillis());
        note.putContentForDB(NotesTable.Cols.CONTENT, values);

        return values;
    }

    @NonNull
    @Override
    public Note getNote(int position) {
        //TODO: eliminate this performance bottleneck
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
