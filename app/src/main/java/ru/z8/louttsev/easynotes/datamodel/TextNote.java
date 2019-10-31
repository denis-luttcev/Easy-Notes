package ru.z8.louttsev.easynotes.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

import ru.z8.louttsev.easynotes.R;
import ru.z8.louttsev.easynotes.database.NotesCursorWrapper;

public class TextNote extends Note {
    private String content;

    TextNote() {
        super();
        content = "";
    }

    public TextNote(@NonNull UUID id) {
        super(id);
        content = "";
    }

    @Override
    public void fillContentPreView(@NonNull FrameLayout contentPreView, Context context) {
        LayoutInflater.from(context).inflate(R.layout.text_note_content_pre_view, contentPreView, true);
        ((TextView) contentPreView.findViewById(R.id.text_note_pre_view)).setText(content);
    }

    @Override
    public void fillContentView(@NonNull FrameLayout contentView, Context context) {
        LayoutInflater.from(context).inflate(R.layout.text_note_content_view, contentView, true);
        ((EditText) contentView.findViewById(R.id.text_note_view)).setText(content);
    }

    @Override
    public void setContent(@NonNull FrameLayout contentView) {
        String content = ((EditText) contentView.findViewById(R.id.text_note_view)).getText().toString();
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
    public void putContentForDB(@NonNull String key, @NonNull ContentValues values) {
        values.put(key, content);
    }

    @Override
    public void setContentFromDB(@NonNull String key, @NonNull NotesCursorWrapper cursor) {
        this.content = Objects.requireNonNull(cursor).getString(cursor.getColumnIndex(key));
    }

    @Override
    public NoteType getType() {
        return NoteType.TEXT_NOTE;
    }

    //TODO: remove
    public void setContent(String content) {
        this.content = content;
    }

    //TODO: remove
    @Override
    public String getContent() {
        return content;
    }
}
