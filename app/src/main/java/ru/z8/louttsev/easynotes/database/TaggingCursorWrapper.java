package ru.z8.louttsev.easynotes.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import androidx.annotation.NonNull;

import java.util.UUID;

import ru.z8.louttsev.easynotes.database.NotesDBSchema.TaggingTable;

class TaggingCursorWrapper extends CursorWrapper {
    TaggingCursorWrapper(@NonNull Cursor cursor) {
        super(cursor);
    }

    @NonNull
    UUID getNoteId() {
        String uuidString = getString(getColumnIndex(TaggingTable.Cols.NOTE));

        return UUID.fromString(uuidString);
    }

    @NonNull
    UUID getTagId() {
        String uuidString = getString(getColumnIndex(TaggingTable.Cols.TAG));

        return UUID.fromString(uuidString);
    }
}
