package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;
import java.util.UUID;

public interface NotesKeeper {
    @Nullable Set<Category> getCategories();
    void addCategory(@NonNull String title);
    void removeCategory(@NonNull String title);
    boolean containCategory(@NonNull String title);
    @Nullable Category getCategory(@NonNull String title);
    @Nullable Set<Tag> getTags();
    void addTag(@NonNull String title);
    void removeTag(@NonNull String title);
    boolean containTag(@NonNull String title);
    @Nullable Tag getTag(@NonNull String title);
    void addNote(@NonNull Note note);
    void removeNote(@NonNull UUID uuid);
    boolean containNote(@NonNull UUID uuid);
    @Nullable Note getNote(@NonNull UUID uuid);
}
