package ru.z8.louttsev.easynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Objects;
import java.util.UUID;

import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.NoteType;
import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;

public class NoteFragment extends Fragment {
    private static final String ARG_NOTE_ID = "note_id";
    private static final String ARG_NOTE_TYPE = "note_type";

    private NotesKeeper mNotesKeeper;
    private Note mNote;

    private EditText mTitle;
    private FrameLayout mContentView;

    @NonNull
    static NoteFragment getInstance(@NonNull UUID noteId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, noteId);
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    static NoteFragment newInstance(@NonNull NoteType noteType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_TYPE, noteType);
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotesKeeper = App.getNotesKeeper();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_NOTE_ID)) { // open exist
                mNote = mNotesKeeper.getNote((UUID) Objects.requireNonNull(args.getSerializable(ARG_NOTE_ID)));
            }
            if (args.containsKey(ARG_NOTE_TYPE)) { // create new
                mNote = mNotesKeeper.createNote((NoteType) args.getSerializable(ARG_NOTE_TYPE));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mNoteLayout = inflater.inflate(R.layout.fragment_note, container, false);

        Toolbar mToolBar = mNoteLayout.findViewById(R.id.note_toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolBar);

        mTitle = mNoteLayout.findViewById(R.id.title_note);

        mContentView = mNoteLayout.findViewById(R.id.content_view);

        mNote.fillContentView(mContentView, getActivity());

        /*mNoteLayout.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNote.setTitle(mTitle.getText().toString());
                mNote.setContent((FrameLayout) mNoteLayout.findViewById(R.id.content_view));
                mNotesKeeper.addNote(mNote);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            }
        });*/

        return mNoteLayout;
    }
}
