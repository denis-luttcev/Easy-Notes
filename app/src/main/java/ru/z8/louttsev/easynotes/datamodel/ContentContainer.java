package ru.z8.louttsev.easynotes.datamodel;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public interface ContentContainer {
    @NonNull
    View getContentPreview(@NonNull Context context);

    @NonNull
    View getContentView(@NonNull Context context);

    void setContent(@NonNull Object content);
}
