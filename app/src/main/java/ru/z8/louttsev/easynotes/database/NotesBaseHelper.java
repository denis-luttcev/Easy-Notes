package ru.z8.louttsev.easynotes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import ru.z8.louttsev.easynotes.database.NotesDBSchema.NotesTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.CategoriesTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.TaggingTable;
import ru.z8.louttsev.easynotes.database.NotesDBSchema.TagsTable;

class NotesBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "notesBase.db";

    NotesBaseHelper(@NonNull Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL("create table " + NotesTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                NotesTable.Cols.UUID + ", " +
                NotesTable.Cols.TYPE + ", " +
                NotesTable.Cols.TITLE + ", " +
                NotesTable.Cols.CATEGORY + ", " +
                NotesTable.Cols.COLOR + ", " +
                NotesTable.Cols.DEADLINE + ", " +
                NotesTable.Cols.LAST_MODIFICATION + ", " +
                NotesTable.Cols.CONTENT +")"
        );
        db.execSQL("create table " + CategoriesTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CategoriesTable.Cols.UUID + ", " +
                CategoriesTable.Cols.TITLE + ")"
        );
        db.execSQL("create table " + TagsTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TagsTable.Cols.UUID + ", " +
                TagsTable.Cols.TITLE + ")"
        );
        db.execSQL("create table " + TaggingTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TaggingTable.Cols.UUID + ", " +
                TaggingTable.Cols.NOTE + ", " +
                TaggingTable.Cols.TAG + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // update to migrate on new DB version
    }
}
