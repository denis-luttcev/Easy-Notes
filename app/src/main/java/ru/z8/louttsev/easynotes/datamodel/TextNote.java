package ru.z8.louttsev.easynotes.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.UUID;

import ru.z8.louttsev.easynotes.R;
import ru.z8.louttsev.easynotes.database.NotesCursorWrapper;

public class TextNote extends Note {
    private String content;

    TextNote() {
        this(UUID.randomUUID());
    }

    TextNote(@NonNull UUID id) {
        super(id);
        content = "";
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.TEXT_NOTE;
    }

    @Override
    public void fillContentPreView(@NonNull FrameLayout contentPreView, @NonNull Context context) {
        LayoutInflater.from(context)
                .inflate(R.layout.text_note_content_pre_view, contentPreView, true);

        TextView textNotePreView = contentPreView.findViewById(R.id.text_note_pre_view);
        textNotePreView.setText(content);
    }

    @Override
    public void fillContentView(@NonNull FrameLayout contentView, @NonNull Context context) {
        LayoutInflater.from(context)
                .inflate(R.layout.text_note_content_view, contentView, true);

        EditText textNoteView = contentView.findViewById(R.id.text_note_view);
        textNoteView.setText(content);
    }

    @Override
    public void setContent(@NonNull FrameLayout contentView) {
        EditText textNoteView = contentView.findViewById(R.id.text_note_view);
        String content = textNoteView.getText().toString().trim();

        if (!content.equals(this.content)) {
            this.content = content;
            modificationUpdate();
        }
    }

    @Override
    public boolean isContentEmpty() {
        return content.isEmpty();
    }

    @Override
    public void getContentForDB(@NonNull String key, @NonNull ContentValues values) {
        // it depends on the implementation of NotesStorage, refactoring required in case of change
        values.put(key, content);
    }

    @Override
    public void setContentFromDB(@NonNull String key, @NonNull NotesCursorWrapper cursor) {
        // it depends on the implementation of NotesStorage, refactoring required in case of change
        this.content = cursor.getString(cursor.getColumnIndex(key));
    }
}
