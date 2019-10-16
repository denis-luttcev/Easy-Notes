package ru.z8.louttsev.easynotes;

import android.app.Application;

import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.NotesRepository;
import ru.z8.louttsev.easynotes.security.KeyKeeper;
import ru.z8.louttsev.easynotes.security.KeyStore;

public class App extends Application {
    private static NotesKeeper sNotesKeeper;
    private static KeyKeeper sKeyKeeper;

    @Override
    public void onCreate() {
        super.onCreate();

        sNotesKeeper = new NotesRepository();
        sKeyKeeper = new KeyStore(this);
    }

    public static NotesKeeper getNotesKeeper() {
        return sNotesKeeper;
    }

    public static KeyKeeper getKeyKeeper() {
        return sKeyKeeper;
    }
}
