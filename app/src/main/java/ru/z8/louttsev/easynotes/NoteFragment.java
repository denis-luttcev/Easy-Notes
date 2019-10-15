package ru.z8.louttsev.easynotes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.TextNote;

public class NoteFragment extends Fragment {
    private Note mNote;
    private EditText mTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: refactor for edit too
        mNote = new TextNote();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View noteView = inflater.inflate(R.layout.fragment_note, container, false);

        mTitle = (EditText) noteView.findViewById(R.id.title_note);
        //TODO: add setText if edit
        mTitle.addTextChangedListener(new TextWatcher() {
            //TODO: change to save text on button click listener
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // ignored
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mNote.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // ignored
            }
        });

        return noteView;
    }
}
