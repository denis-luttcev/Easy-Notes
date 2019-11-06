package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;

import java.util.Set;
import java.util.UUID;

public interface NotesKeeper {
    @NonNull
    Set<Category> getCategories();

    void addCategory(@NonNull String title);

    void removeCategory(@NonNull String title);

    @NonNull
    Category getCategory(@NonNull String title) throws IllegalAccessException;

    @NonNull
    Set<Tag> getTags();

    void addTag(@NonNull String title);

    void removeTag(@NonNull String title);

    @NonNull
    Tag getTag(@NonNull String title) throws IllegalAccessException;

    void addNote(@NonNull Note note);

    void removeNote(@NonNull UUID id);

    boolean containNote(@NonNull UUID id);

    @NonNull
    Note getNote(@NonNull UUID id) throws IllegalAccessException;

    @NonNull
    Note getNote(int position);

    int getNotesCount();
}
