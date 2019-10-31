package ru.z8.louttsev.easynotes.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.UUID;

import ru.z8.louttsev.easynotes.database.NotesDBSchema.NotesTable;
import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.Note.Color;
import ru.z8.louttsev.easynotes.datamodel.NotesRepository;
import ru.z8.louttsev.easynotes.datamodel.TextNote;

public class NotesCursorWrapper extends CursorWrapper {
    private NotesRepository repository;

    public NotesCursorWrapper(@NonNull Cursor cursor, @NonNull NotesRepository repository) {
        super(cursor);
        this.repository = repository;
    }

    public Note getNote() {
        String uuidString = getString(getColumnIndex(NotesTable.Cols.UUID));
        int type = (int) getLong(getColumnIndex(NotesTable.Cols.TYPE));
        String title = getString(getColumnIndex(NotesTable.Cols.TITLE));
        String categoryUuidString = getString(getColumnIndex(NotesTable.Cols.CATEGORY));
        int color = (int) getLong(getColumnIndex(NotesTable.Cols.COLOR));
        long deadlineInMillis = getLong(getColumnIndex(NotesTable.Cols.DEADLINE));
        long lastModificationInMillis = getLong(getColumnIndex(NotesTable.Cols.LAST_MODIFICATION));

        Note note = new TextNote(UUID.fromString(uuidString));

        note.setTitle(title);

        if (!categoryUuidString.isEmpty()) {
            note.setCategory(repository.getCategory(UUID.fromString(categoryUuidString)));
        } else note.setCategory(null);

        note.setColor(Color.values()[color]);

        if (deadlineInMillis != 0) {
            Calendar deadline = Calendar.getInstance();
            deadline.setTimeInMillis(deadlineInMillis);
            note.setDeadline(deadline);
        } else note.setDeadline(null);

        Calendar lastModification = Calendar.getInstance();
        lastModification.setTimeInMillis(lastModificationInMillis);
        note.setLastModification(lastModification);

        note.setContentFromDB(NotesTable.Cols.CONTENT, this);

        return note;
    }
}
