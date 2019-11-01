package ru.z8.louttsev.easynotes.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.UUID;

import ru.z8.louttsev.easynotes.database.NotesDBSchema.NotesTable;
import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.Note.Color;

public class NotesCursorWrapper extends CursorWrapper {

    public NotesCursorWrapper(@NonNull Cursor cursor) {
        super(cursor);
    }

    public Note getNote() {
        String uuidString = getString(getColumnIndex(NotesTable.Cols.UUID));
        int type = (int) getLong(getColumnIndex(NotesTable.Cols.TYPE));
        String title = getString(getColumnIndex(NotesTable.Cols.TITLE));
        int color = (int) getLong(getColumnIndex(NotesTable.Cols.COLOR));
        long deadlineInMillis = getLong(getColumnIndex(NotesTable.Cols.DEADLINE));

        Note note = Note.getInstance(Note.Type.values()[type], UUID.fromString(uuidString));

        note.setTitle(title);

        note.setColor(Color.values()[color]);

        if (deadlineInMillis != 0) {
            Calendar deadline = Calendar.getInstance();
            deadline.setTimeInMillis(deadlineInMillis);
            note.setDeadline(deadline);
        } else note.setDeadline(null);

        return note;
    }

    @Nullable
    public UUID getCategoryId() {
        String categoryUuidString = getString(getColumnIndex(NotesTable.Cols.CATEGORY));
        if (!categoryUuidString.isEmpty()) {
            return UUID.fromString(categoryUuidString);
        } else return null;
    }

    @NonNull
    public Calendar getLastModification() {
        long lastModificationInMillis = getLong(getColumnIndex(NotesTable.Cols.LAST_MODIFICATION));

        Calendar lastModification = Calendar.getInstance();
        lastModification.setTimeInMillis(lastModificationInMillis);

        return lastModification;
    }
}
