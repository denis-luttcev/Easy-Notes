package ru.z8.louttsev.easynotes;

import android.app.Application;

import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.NotesRepository;

public class App extends Application {
    private static NotesKeeper sNotesKeeper;

    @Override
    public void onCreate() {
        super.onCreate();

        sNotesKeeper = new NotesRepository();
    }

    public static NotesKeeper getNotesKeeper() {
        return sNotesKeeper;
    }
}
