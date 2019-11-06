package ru.z8.louttsev.easynotes.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import androidx.annotation.NonNull;

import java.util.UUID;

import ru.z8.louttsev.easynotes.database.NotesDBSchema.TagsTable;
import ru.z8.louttsev.easynotes.datamodel.Tag;

class TagsCursorWrapper extends CursorWrapper {
    TagsCursorWrapper(@NonNull Cursor cursor) {
        super(cursor);
    }

    @NonNull
    Tag getTag() {
        String uuidString = getString(getColumnIndex(TagsTable.Cols.UUID));
        String title = getString(getColumnIndex(TagsTable.Cols.TITLE));

        Tag tag = new Tag(UUID.fromString(uuidString));
        tag.setTitle(title);

        return tag;
    }
}
