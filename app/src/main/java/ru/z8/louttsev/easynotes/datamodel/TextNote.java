package ru.z8.louttsev.easynotes.datamodel;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class TextNote extends Note {
    @NonNull
    private String content;

    public TextNote() {
        super();
        content = "";
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @NonNull
    @Override
    public View fillContentPreView(@NonNull View contentPreView) {
        ((TextView) contentPreView).setText(content);
        return contentPreView;
    }

    @NonNull
    @Override
    public View fillContentView(@NonNull View contentView) {
        ((EditText) contentView).setText(content);
        return contentView;
    }

    @Override
    public void setContent(@NonNull Object content) {
        this.content = ((EditText) content).getText().toString();
    }
}
