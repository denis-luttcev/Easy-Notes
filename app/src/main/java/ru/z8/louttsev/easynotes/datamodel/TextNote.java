package ru.z8.louttsev.easynotes.datamodel;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

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
    public boolean isEditable() {
        return true;
    }

    @Override
    public void fillContentPreView(@NonNull FrameLayout contentPreView, Context context) {
        TextView preView = new TextView(context);
        preView.setText(content);
        preView.setMaxLines(MAX_LINES_PREVIEW);
        preView.setEllipsize(TextUtils.TruncateAt.END);
        preView.setTextAppearance(context, R.style.TextAppearance_MaterialComponents_Body2);
        contentPreView.addView(preView);
    }

    @Override
    public void fillContentView(@NonNull FrameLayout contentView, Context context) {
        EditText view = new EditText(context);
        view.setText(content);
        contentView.addView(view);
    }

    @Override
    public void setContent(@NonNull View contentView) {
        content = ((EditText) contentView).getText().toString();
    }
}
