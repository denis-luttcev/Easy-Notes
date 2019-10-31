package ru.z8.louttsev.easynotes.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import androidx.annotation.NonNull;

import java.util.UUID;

import ru.z8.louttsev.easynotes.database.NotesDBSchema.TaggingTable;
import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.NotesRepository;
import ru.z8.louttsev.easynotes.datamodel.Tag;

public class TaggingCursorWrapper extends CursorWrapper {
    private NotesRepository repository;

    public TaggingCursorWrapper(@NonNull Cursor cursor, @NonNull NotesRepository repository) {
        super(cursor);
        this.repository = repository;
    }

    public Note getNote() throws IllegalAccessException {
        String uuidString = getString(getColumnIndex(TaggingTable.Cols.NOTE));

        return repository.getNote(UUID.fromString(uuidString));
    }


    public Tag getTag() {
        String uuidString = getString(getColumnIndex(TaggingTable.Cols.TAG));

        return repository.getTag(UUID.fromString(uuidString));
    }
}
