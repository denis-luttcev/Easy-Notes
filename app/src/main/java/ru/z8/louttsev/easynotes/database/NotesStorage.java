package ru.z8.louttsev.easynotes.database;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.z8.louttsev.easynotes.datamodel.Category;
import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.Tag;

public interface NotesStorage {
    @NonNull
    Map<String, Category> loadCategories();

    void insertCategory(@NonNull Category category);

    void deleteCategory(@NonNull Category category);

    @NonNull
    Map<String, Tag> loadTags();

    void insertTag(@NonNull Tag tag);

    void deleteTag(@NonNull Tag tag);

    @NonNull
    List<Note> loadNotes();

    void insertNote(@NonNull Note note);

    void deleteNote(@NonNull UUID id);

    void removeNoteCategory(@NonNull Category category);

    void loadTagging();

    void removeTagging(@NonNull Tag tag);

    void removeTagging(@NonNull UUID noteId);

    void insertTagging(@NonNull Note note, @NonNull Tag tag);
}
