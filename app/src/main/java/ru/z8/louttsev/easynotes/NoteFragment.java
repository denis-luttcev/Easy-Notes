package ru.z8.louttsev.easynotes;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.TextNote;

public class NoteFragment extends Fragment {
    private NotesKeeper mNotesKeeper;
    private Note mNote;
    private EditText mTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: refactor for edit too
        mNote = new TextNote();
        mNotesKeeper = App.getNotesKeeper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View noteView = inflater.inflate(R.layout.fragment_note, container, false);

        //TODO: add setText if edit
        mTitle = (EditText) noteView.findViewById(R.id.title_note);

        mNote.fillContentView((FrameLayout) noteView.findViewById(R.id.content_view), getActivity());

        noteView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNote.setTitle(mTitle.getText().toString());
                mNote.setContent((FrameLayout) noteView.findViewById(R.id.content_view));
                mNotesKeeper.addNote(mNote);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            }
        });

        return noteView;
    }
}
