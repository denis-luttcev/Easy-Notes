package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NotesKeeper {
    @NonNull Set<Category> getCategories();
    void addCategory(@NonNull String title);
    void removeCategory(@NonNull String title);
    boolean containCategory(@NonNull String title);
    @Nullable Category getCategory(@NonNull String title);
    @NonNull Set<Tag> getTags();
    void addTag(@NonNull String title);
    void removeTag(@NonNull String title);
    boolean containTag(@NonNull String title);
    @Nullable Tag getTag(@NonNull String title);
    @NonNull List<Note> getNotes();
    void addNote(@NonNull Note note);
    void removeNote(int position);
    boolean containNote(@NonNull UUID uuid);
    @Nullable Note getNote(int position);
}
