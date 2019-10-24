package ru.z8.louttsev.easynotes;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Calendar;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.NoteType;
import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.Tag;

public class NoteFragment extends Fragment {
    private static final String ARG_NOTE_ID = "note_id";
    private static final String ARG_NOTE_TYPE = "note_type";

    private NotesKeeper mNotesKeeper;
    private Note mNote;

    private TextView mCategory;
    private TextView mDeadline;
    private EditText mTitle;
    private FrameLayout mContentView;
    private FlexboxLayout mTags;

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
                try {
                    mNote = mNotesKeeper.getNote((UUID) Objects.requireNonNull(args.getSerializable(ARG_NOTE_ID)));
                } catch (IllegalAccessException ignored) {}
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

        if (mNote.isColored()) {
            applyNoteLayoutColor(mNoteLayout, mNote);
        }

        mCategory = mNoteLayout.findViewById(R.id.category_note);
        if (mNote.isCategorized()) {
            mCategory.setText(Objects.requireNonNull(mNote.getCategory()).getTitle());
        }

        mDeadline = mNoteLayout.findViewById(R.id.deadline_note);
        if (mNote.isDeadlined()) {
            mDeadline.setText(mNote.getDeadlineRepresent(Objects.requireNonNull(getActivity())));
            applyDeadlineColor(mNote, mDeadline);
        }

        mTitle = mNoteLayout.findViewById(R.id.title_note);
        mTitle.setText(mNote.getTitle());

        mContentView = mNoteLayout.findViewById(R.id.content_view);
        mNote.fillContentView(mContentView, getActivity());

        mTags = mNoteLayout.findViewById(R.id.tags_note);
        showTags(mNote, mTags);

        EditText newTag = (EditText) Objects.requireNonNull(getActivity()).getLayoutInflater()
                .inflate(R.layout.add_new_view, mTags, false);
        newTag.setHint(getString(R.string.add_new_tag_hint));
        newTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    addNewTag(textView);
                    return true;
                }
                return false;
            }
        });
        newTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (!isFocused) {
                    addNewTag(view);
                }
            }
        });
        mTags.addView(newTag);

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

    private void applyNoteLayoutColor(@NonNull View noteLayout, @NonNull Note note) {
        int color = R.color.colorNoneNote;
        switch (note.getColor()) {
            case URGENT:
                color = R.color.colorUrgentNote;
                break;
            case ATTENTION:
                color = R.color.colorAttentionNote;
                break;
            case NORMAL:
                color = R.color.colorNormalNote;
                break;
            case QUIET:
                color = R.color.colorQuietNote;
                break;
            case ACCESSORY:
                color = R.color.colorAccessoryNote;
                break;
            default: // ignored
        }
        noteLayout.setBackgroundColor(getResources().getColor(color));
    }

    private void applyDeadlineColor(@NonNull Note note, @NonNull TextView deadlineView) {
        int color = R.color.colorDeadlineAhead;
        switch (note.getDeadlineStatus(Calendar.getInstance())) {
            case OVERDUE:
                color = R.color.colorDeadlineOverdue;
                deadlineView.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case IMMEDIATE:
                color = R.color.colorDeadlineImmediate;
                deadlineView.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            default:
                deadlineView.setTypeface(Typeface.DEFAULT);
        }
        deadlineView.setTextColor(getResources().getColor(color));
    }

    private void showTags(@NonNull Note note, @NonNull FlexboxLayout tagsLineView) {
        Set<Tag> allTags = mNotesKeeper.getTags();
        for (Tag tag : allTags) {
            CheckBox tagView = (CheckBox) Objects.requireNonNull(getActivity()).getLayoutInflater()
                    .inflate(R.layout.tag_view, tagsLineView, false);
            tagView.setText(tag.getTitle());
            if (mNote.hasTag(tag)) {
                tagView.setTextColor(getResources().getColor(R.color.colorWhiteText));
                tagView.setChecked(true);
            }
            tagView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton tagCheckbox, boolean isChecked) {
                    if (isChecked) {
                        tagCheckbox.setTextColor(getResources().getColor(R.color.colorWhiteText));
                        try {
                            mNote.markTag(mNotesKeeper.getTag(tagCheckbox.getText().toString()));
                        } catch (IllegalAccessException ignored) {} // impossible
                    } else {
                        tagCheckbox.setTextColor(getResources().getColor(R.color.colorPrimaryText));
                        try {
                            mNote.unmarkTag(mNotesKeeper.getTag(tagCheckbox.getText().toString()));
                        } catch (IllegalAccessException ignored) {}
                    }
                }
            });
            tagsLineView.addView(tagView);
        }
    }

    private void addNewTag(@NonNull View view) {
        EditText field = (EditText) view;
        String newTag = field.getText().toString();
        mNotesKeeper.addTag(newTag);
        CheckBox tagView = (CheckBox) Objects.requireNonNull(getActivity()).getLayoutInflater()
                .inflate(R.layout.tag_view, tagsLineView, false);
        tagView.setText(tag.getTitle());
    }
}
