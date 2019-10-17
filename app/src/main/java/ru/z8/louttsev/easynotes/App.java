package ru.z8.louttsev.easynotes;

import android.app.Application;

import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.NotesRepository;
import ru.z8.louttsev.easynotes.security.Protector;
import ru.z8.louttsev.easynotes.security.PinStore;

public class App extends Application {
    private static NotesKeeper sNotesKeeper;
    private static Protector sProtector;

    @Override
    public void onCreate() {
        super.onCreate();

        sNotesKeeper = new NotesRepository();
        sProtector = new PinStore(this);
    }

    public static NotesKeeper getNotesKeeper() {
        return sNotesKeeper;
    }

    public static Protector getProtector() {
        return sProtector;
    }
}
