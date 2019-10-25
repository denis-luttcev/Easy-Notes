package ru.z8.louttsev.easynotes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

    private Context mContext;

    private NotesKeeper mNotesKeeper;
    private Note mNote;

    private EditText mTitleView;
    private FrameLayout mContentView;

    private EditText plusTagView;

    @NonNull
    static NoteFragment getInstance(@NonNull UUID noteId) {
        Bundle args = new Bundle();
        NoteFragment fragment = new NoteFragment();

        args.putSerializable(ARG_NOTE_ID, noteId);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    static NoteFragment newInstance(@NonNull NoteType noteType) {
        Bundle args = new Bundle();
        NoteFragment fragment = new NoteFragment();

        args.putSerializable(ARG_NOTE_TYPE, noteType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotesKeeper = App.getNotesKeeper();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_NOTE_ID)) { // open exist
                try {
                    mNote = mNotesKeeper.getNote(
                            (UUID) Objects.requireNonNull(args.getSerializable(ARG_NOTE_ID)));
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

        //TODO: color view add + onClick
        applyNoteLayoutColor(mNoteLayout);

        TextView mCategoryView = mNoteLayout.findViewById(R.id.category_note);
        if (mNote.isCategorized()) {
            mCategoryView.setText(Objects.requireNonNull(mNote.getCategory()).getTitle());
            //TODO: add OnClick
        }

        TextView mDeadlineView = mNoteLayout.findViewById(R.id.deadline_note);
        if (mNote.isDeadlined()) {
            mDeadlineView.setText(mNote.getDeadlineRepresent(mContext));
            applyDeadlineStyle(mDeadlineView);
            //TODO: add OnClick
        }

        mTitleView = mNoteLayout.findViewById(R.id.title_note);
        if (mNote.isTitled()) {
            mTitleView.setText(mNote.getTitle());
        }

        mContentView = mNoteLayout.findViewById(R.id.content_view);
        mNote.fillContentView(mContentView, mContext);

        FlexboxLayout mTagsLayout = mNoteLayout.findViewById(R.id.tags_note);
        showTags(mTagsLayout);

        return mNoteLayout;
    }

    private void applyNoteLayoutColor(@NonNull View noteLayout) {
        int color = R.color.colorNoneNote;

        switch (mNote.getColor()) {
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

    private void applyDeadlineStyle(@NonNull TextView deadlineView) {
        int color = R.color.colorDeadlineAhead;

        switch (mNote.getDeadlineStatus(Calendar.getInstance())) {
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

    private void showTags(@NonNull FlexboxLayout tagsLayout) {
        Set<Tag> allTags = mNotesKeeper.getTags();
        plusTagView = createPlusTagView(tagsLayout);

        for (Tag tag : allTags) {
            CheckBox tagView = createTagView(tagsLayout, tag.getTitle());
            if (mNote.hasTag(tag.getTitle())) {
                tagView.setTextColor(getResources().getColor(R.color.colorLightText));
                tagView.setChecked(true);
            }
            tagsLayout.addView(tagView);
        }
        tagsLayout.addView(plusTagView);
    }

    @NonNull
    private EditText createPlusTagView(@NonNull FlexboxLayout tagsLayout) {
        EditText plusTagView = (EditText) getLayoutInflater()
                .inflate(R.layout.add_new_view, tagsLayout, false);

        plusTagView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        plusTagView.setHint(getString(R.string.add_new_tag_hint));

        plusTagView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView plusTagView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    plusTagView.clearFocus();
                    return true;
                }
                return false;
            }
        });

        plusTagView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View plusTagView, boolean isFocused) {
                if (!isFocused) {
                    addNewTag();
                }
            }
        });

        return plusTagView;
    }

    private void addNewTag() {
        FlexboxLayout tagsLayout = (FlexboxLayout) plusTagView.getParent();

        tagsLayout.removeView(plusTagView);

        String newTagTitle = plusTagView.getText().toString().trim();
        StringBuilder title = new StringBuilder();
        title.append(newTagTitle.substring(0, 1).toUpperCase());
        title.append(newTagTitle.substring(1));
        newTagTitle = title.toString();

        mNotesKeeper.addTag(newTagTitle);
        try {
            mNote.markTag(mNotesKeeper.getTag(newTagTitle));
        } catch (IllegalAccessException ignored) {} // impossible

        CheckBox tagView = createTagView(tagsLayout, newTagTitle);
        tagView.setTextColor(getResources().getColor(R.color.colorLightText));
        tagView.setChecked(true);
        tagsLayout.addView(tagView);
        plusTagView.setText("");

        tagsLayout.addView(plusTagView);
    }

    @NonNull
    private CheckBox createTagView(@NonNull final FlexboxLayout tagsLayout, @NonNull String title) {
        CheckBox tagView = (CheckBox) getLayoutInflater()
                .inflate(R.layout.tag_view, tagsLayout, false);

        tagView.setText(title);
        tagView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton tagCheckbox, boolean isChecked) {
                if (isChecked) {
                    tagCheckbox.setTextColor(getResources().getColor(R.color.colorLightText));
                    try {
                        mNote.markTag(mNotesKeeper.getTag(tagCheckbox.getText().toString()));
                    } catch (IllegalAccessException ignored) {} // impossible
                } else {
                    tagCheckbox.setTextColor(getResources().getColor(R.color.colorPrimaryText));
                    mNote.unmarkTag(tagCheckbox.getText().toString());
                }
            }
        });
        tagView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View tagCheckbox) {
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.delete_tag_dialog_title))
                        .setMessage(getString(R.string.delete_tag_dialog_message))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CompoundButton tagView = (CompoundButton) tagCheckbox;
                                String tagTitle = tagView.getText().toString();
                                if (tagView.isChecked()) {
                                    mNote.unmarkTag(tagTitle);
                                }
                                tagsLayout.removeView(tagCheckbox);
                                mNotesKeeper.removeTag(tagTitle);
                            }
                        })
                        .create()
                        .show();
                return true;
            }
        });

        return tagView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        plusTagView.setOnFocusChangeListener(null); // prevents adding new tag while rotate
    }
}
