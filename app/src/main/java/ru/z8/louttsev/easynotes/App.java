package ru.z8.louttsev.easynotes;

import android.app.Application;

import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.NotesRepository;
import ru.z8.louttsev.easynotes.security.Protector;
import ru.z8.louttsev.easynotes.security.PinCodeProtector;

@SuppressWarnings("WeakerAccess")
public class App extends Application {
    private static NotesKeeper sNotesKeeper;
    private static Protector sProtector;

    @Override
    public void onCreate() {
        super.onCreate();

        sNotesKeeper = new NotesRepository(this);
        sProtector = new PinCodeProtector(this);
    }

    public static NotesKeeper getNotesKeeper() {
        return sNotesKeeper;
    }

    public static Protector getProtector() {
        return sProtector;
    }
}
