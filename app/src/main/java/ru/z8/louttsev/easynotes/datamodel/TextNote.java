package ru.z8.louttsev.easynotes.datamodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ru.z8.louttsev.easynotes.R;

public class TextNote extends Note {
    private String content;

    private final int MAX_LINES_PREVIEW = 3;

    public TextNote() {
        super();
        content = "";
    }

    //TODO: remove
    public void setContent(String content) {
        this.content = content;
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
        if (!content.isEmpty() && !content.equals(this.content)) {
            this.content = content;
            modificationUpdate();
        }
    }
}
