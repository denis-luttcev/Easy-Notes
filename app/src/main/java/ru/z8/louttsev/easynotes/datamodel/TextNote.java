package ru.z8.louttsev.easynotes.datamodel;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ru.z8.louttsev.easynotes.GlobalSettings;

class TextNote extends Note {
    @NonNull
    private String content;

    public TextNote() {
        super();
        content = "";
    }

    @NonNull
    @Override
    public View getContentPreview(@NonNull Context context) {
        TextView contentPreview = new TextView(context);
        contentPreview.setText(content);
        contentPreview.setMaxLines(GlobalSettings.MAX_LINES_IN_PREVIEW);
        contentPreview.setEllipsize(TextUtils.TruncateAt.END);
        return contentPreview;
    }

    @NonNull
    @Override
    public View getContentView(@NonNull Context context) {
        EditText contentView = new EditText(context);
        contentView.setText(content);
        return contentView;
    }

    @Override
    public void setContent(@NonNull Object content) {
        EditText contentView = (EditText) content;
        this.content = contentView.getText().toString();
    }
}
