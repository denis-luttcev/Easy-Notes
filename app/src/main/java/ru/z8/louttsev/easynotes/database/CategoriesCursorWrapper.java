package ru.z8.louttsev.easynotes.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import androidx.annotation.NonNull;

import java.util.UUID;

import ru.z8.louttsev.easynotes.database.NotesDBSchema.CategoriesTable;
import ru.z8.louttsev.easynotes.datamodel.Category;

public class CategoriesCursorWrapper extends CursorWrapper {
    public CategoriesCursorWrapper(@NonNull Cursor cursor) {
        super(cursor);
    }

    public Category getCategory() {
        String uuidString = getString(getColumnIndex(CategoriesTable.Cols.UUID));
        String title = getString(getColumnIndex(CategoriesTable.Cols.TITLE));

        Category category = new Category(UUID.fromString(uuidString));
        category.setTitle(title);

        return category;
    }
}
