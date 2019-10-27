package ru.z8.louttsev.easynotes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Calendar;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import ru.z8.louttsev.easynotes.datamodel.Category;
import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.NoteType;
import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.Tag;

public class NoteFragment extends Fragment {
    private static final String ARG_NOTE_ID = "note_id";
    private static final String ARG_NOTE_TYPE = "note_type";

    private static final String DIALOG_DATE_PICKER = "date_picker";
    private static final int REQUEST_DATE = 1;
    private static final String DIALOG_TIME_PICKER = "time_picker";
    private static final int REQUEST_TIME = 2;

    private Context mContext;

    private NotesKeeper mNotesKeeper;
    private Note mNote;

    private TextView mDeadlineView;
    private EditText mTitleView;
    private FrameLayout mContentView;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultIntent) {
        if (resultCode == Activity.RESULT_OK && resultIntent != null) {
            switch (requestCode) {
                case REQUEST_DATE:
                    Calendar date = (Calendar) resultIntent
                            .getSerializableExtra(DatePickerDialogFragment.RESULT_DATE);
                    mNote.setDeadline(date);
                    updateDeadlineView();
                    if (mNote.isDeadlined()) { // date was choice
                        requestTime();
                    }
                    break;
                case REQUEST_TIME:
                    Calendar time = (Calendar) resultIntent
                            .getSerializableExtra(TimePickerDialogFragment.RESULT_TIME);
                    mNote.setDeadline(time);
                    updateDeadlineView();
                    break;
                default: // ignored
            }
        }
        if (resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_DATE && mNote.isDeadlined()) {
            requestTime();
        }
    }

    private void requestTime() {
        Calendar deadline = mNote.getDeadline();
        TimePickerDialogFragment timePicker = TimePickerDialogFragment
                .getInstance(Objects.requireNonNull(deadline));
        timePicker.setTargetFragment(NoteFragment.this, REQUEST_TIME);
        timePicker.show(Objects.requireNonNull(getFragmentManager()), DIALOG_TIME_PICKER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mNoteLayout = inflater.inflate(R.layout.fragment_note, container, false);

        //TODO: color view add + onClick
        applyNoteLayoutColor(mNoteLayout);

        final FlexboxLayout mCategoriesLayout = mNoteLayout.findViewById(R.id.categories_choice);
        final TextView mCategoryView = mNoteLayout.findViewById(R.id.category_note);
        applyCategoryViewStyle(mCategoryView);
        mCategoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategoriesLayout.removeAllViews(); // prevents refilling
                showCategories(mCategoriesLayout, mCategoryView);
            }
        });

        mDeadlineView = mNoteLayout.findViewById(R.id.deadline_note);
        updateDeadlineView();
        mDeadlineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View deadlineView) {
                DatePickerDialogFragment datePicker;

                if (mNote.isDeadlined()) {
                    Calendar deadline = mNote.getDeadline();
                    datePicker = DatePickerDialogFragment
                            .getInstance(Objects.requireNonNull(deadline));
                } else {
                    datePicker = DatePickerDialogFragment.newInstance();
                }

                datePicker.setTargetFragment(NoteFragment.this, REQUEST_DATE);
                datePicker.show(Objects.requireNonNull(getFragmentManager()), DIALOG_DATE_PICKER);
            }
        });

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

    private void applyCategoryViewStyle(TextView categoryView) {
        if (mNote.isCategorized()) {
            categoryView.setText(Objects.requireNonNull(mNote.getCategory()).getTitle());
            categoryView.setBackground(getResources().getDrawable(R.drawable.rounded_fill_field));
            categoryView.setTextColor(getResources().getColor(R.color.colorLightText));
        } else {
            categoryView.setText("");
            categoryView.setBackground(getResources().getDrawable(R.drawable.rounded_not_fill_field));
            categoryView.setTextColor(getResources().getColor(R.color.colorPrimaryText));
        }
    }

    private void showCategories(@NonNull final FlexboxLayout categoriesLayout,
                                @NonNull final TextView categoryView) {

        Set<Category> allCategories = mNotesKeeper.getCategories();

        final EditText plusCategoryView = createPlusItemView(categoriesLayout,
                getString(R.string.add_new_category_hint),
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView plusView, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            String title = addNewCategory((EditText) plusView);
                            categoryView.setText(title);
                            applyCategoryViewStyle(categoryView);
                            categoriesLayout.removeAllViews();
                            return true;
                        }
                        return false;
                    }
                });

        for (Category category : allCategories) {
            CheckBox categoryItemView =
                    createCategoryView(categoriesLayout, category.getTitle(), categoryView);

            if (mNote.hasCategory(category.getTitle())) {
                categoryItemView.setTextColor(getResources().getColor(R.color.colorLightText));
                categoryItemView.setChecked(true);
            }
            categoriesLayout.addView(categoryItemView);
        }
        categoriesLayout.addView(plusCategoryView);
    }

    @NonNull
    private EditText createPlusItemView(@NonNull FlexboxLayout panelLayout,
                                        @NonNull String itemHint,
                                        @NonNull TextView.OnEditorActionListener onActionDoneListener) {

        EditText plusItemView = (EditText) getLayoutInflater()
                .inflate(R.layout.plus_item_view, panelLayout, false);

        plusItemView.setHint(itemHint);
        plusItemView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        plusItemView.setOnEditorActionListener(onActionDoneListener);

        return plusItemView;
    }

    @NonNull
    private String addNewCategory(@NonNull EditText plusItemView) {
        String title = plusItemView.getText().toString().trim();
        title = upFirstLetter(title);

        mNotesKeeper.addCategory(title);
        try {
            mNote.setCategory(mNotesKeeper.getCategory(title));
        } catch (IllegalAccessException ignored) {} // impossible

        return title;
    }

    @NonNull
    private CheckBox createCategoryView(@NonNull final FlexboxLayout categoriesLayout,
                                        @NonNull final String title,
                                        @NonNull final TextView categoryView) {

        return createPanelItemView(categoriesLayout, title,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View categoryItemView) {
                        CompoundButton category = (CompoundButton) categoryItemView;
                        try {
                            mNote.setCategory(mNotesKeeper.getCategory((category.getText().toString())));
                            categoryView.setText(title);
                            applyCategoryViewStyle(categoryView);
                            categoriesLayout.removeAllViews();
                        } catch (IllegalAccessException ignored) {} // impossible
                    }
                },
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return false;
                    }
                });

        /*categoryView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View categoryCheckbox) {
                //TODO
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.delete_tag_dialog_title))
                        .setMessage(getString(R.string.delete_tag_dialog_message))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CompoundButton tagView = (CompoundButton) categoryCheckbox;
                                String tagTitle = tagView.getText().toString();
                                if (tagView.isChecked()) {
                                    mNote.unmarkTag(tagTitle);
                                }
                                categoriesLayout.removeView(categoryCheckbox);
                                mNotesKeeper.removeTag(tagTitle);
                                Toast.makeText(mContext,
                                        getString(R.string.delete_tag_toast_message),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                        .create()
                        .show();
                return true;
            }
        });*/

    }

    @NonNull
    private CheckBox createPanelItemView(@NonNull FlexboxLayout panelLayout,
                                         @NonNull String title,
                                         @NonNull View.OnClickListener onClickListener,
                                         @NonNull View.OnLongClickListener onLongClickListener) {

        CheckBox itemView = (CheckBox) getLayoutInflater()
                .inflate(R.layout.panel_item_view, panelLayout, false);

        itemView.setText(title);
        itemView.setOnClickListener(onClickListener);
        itemView.setOnLongClickListener(onLongClickListener);

        return itemView;
    }

    private void updateDeadlineView() {
        if (mNote.isDeadlined()) {
            mDeadlineView.setText(mNote.getDeadlineRepresent(mContext));
        } else mDeadlineView.setText("");
        applyDeadlineViewStyle();
    }

    private void applyDeadlineViewStyle() {
        int color;

        if (mNote.isDeadlined()) {
            color = R.color.colorDeadlineLightAhead;
            mDeadlineView.setBackground(getResources().getDrawable(R.drawable.rounded_fill_field));
        } else {
            color = R.color.colorDeadlineAhead;
            mDeadlineView.setBackground(getResources().getDrawable(R.drawable.rounded_not_fill_field));
        }

        switch (mNote.getDeadlineStatus(Calendar.getInstance())) {
            case OVERDUE:
                color = R.color.colorDeadlineOverdue;
                mDeadlineView.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case IMMEDIATE:
                color = R.color.colorDeadlineImmediate;
                mDeadlineView.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            default:
                mDeadlineView.setTypeface(Typeface.DEFAULT);
        }

        mDeadlineView.setTextColor(getResources().getColor(color));
    }

    private void showTags(@NonNull final FlexboxLayout tagsLayout) {
        Set<Tag> allTags = mNotesKeeper.getTags();

        final EditText plusTagView = createPlusItemView(tagsLayout,
                getString(R.string.add_new_tag_hint),
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView plusView, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            tagsLayout.removeView(plusView);
                            String title = addNewTag((EditText) plusView);

                            CheckBox tagView = createTagView(tagsLayout, title);
                            tagView.setTextColor(getResources().getColor(R.color.colorLightText));
                            tagView.setChecked(true);
                            tagsLayout.addView(tagView);
                            plusView.setText("");

                            tagsLayout.addView(plusView);
                            return true;
                        }
                        return false;
                    }
                });

        for (Tag tag : allTags) {
            CheckBox tagItemView = createTagView(tagsLayout, tag.getTitle());
            if (mNote.hasTag(tag.getTitle())) {
                tagItemView.setTextColor(getResources().getColor(R.color.colorLightText));
                tagItemView.setChecked(true);
            }
            tagsLayout.addView(tagItemView);
        }

        tagsLayout.addView(plusTagView);
    }

    @NonNull
    private String addNewTag(@NonNull EditText plusItemView) {
        String title = plusItemView.getText().toString().trim();
        title = upFirstLetter(title);

        mNotesKeeper.addTag(title);
        try {
            mNote.markTag(mNotesKeeper.getTag(title));
        } catch (IllegalAccessException ignored) {} // impossible

        return title;
    }

    @NonNull
    private CheckBox createTagView(@NonNull final FlexboxLayout tagsLayout, @NonNull String title) {
        return createPanelItemView(tagsLayout, title,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View tagView) {
                        CheckBox tag = (CheckBox) tagView;
                        if (tag.isChecked()) { // while click check was toggle
                            tag.setTextColor(getResources().getColor(R.color.colorLightText));
                            try {
                                mNote.markTag(mNotesKeeper.getTag(tag.getText().toString()));
                            } catch (IllegalAccessException ignored) {} // impossible
                        } else {
                            tag.setTextColor(getResources().getColor(R.color.colorPrimaryText));
                            mNote.unmarkTag(tag.getText().toString());
                        }
                    }
                },
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final CompoundButton tag = (CompoundButton) view;
                        new AlertDialog.Builder(mContext)
                                .setTitle(getString(R.string.delete_tag_dialog_title))
                                .setMessage(getString(R.string.delete_tag_dialog_message))
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String title = tag.getText().toString();
                                                if (tag.isChecked()) {
                                                    mNote.unmarkTag(title);
                                                }
                                                tagsLayout.removeView(tag);
                                                mNotesKeeper.removeTag(title);
                                                Toast.makeText(mContext,
                                                        getString(R.string.delete_tag_toast_message),
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                                .create()
                                .show();
                        return true;
                    }
                });
    }

    @NonNull
    private String upFirstLetter(@NonNull String string) {
        if (!string.isEmpty()) {
            string = string.substring(0, 1).toUpperCase() + string.substring(1);
        }
        return string;
    }
}
