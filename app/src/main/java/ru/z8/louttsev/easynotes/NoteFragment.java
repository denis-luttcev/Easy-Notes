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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Calendar;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import ru.z8.louttsev.easynotes.datamodel.Category;
import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.Tag;

@SuppressWarnings("WeakerAccess")
public class NoteFragment extends Fragment {
    private static final String FRAGMENT_TAG = "note_fragment";
    private static final String ARG_NOTE_ID = "note_id";
    private static final String ARG_NOTE_TYPE = "note_type";

    private static final String DIALOG_DATE_PICKER = "date_picker";
    private static final int REQUEST_DATE = 1;
    private static final String DIALOG_TIME_PICKER = "time_picker";
    private static final int REQUEST_TIME = 2;

    private Context mContext;
    private FragmentManager mFragmentManager;

    private NotesKeeper mNotesKeeper;
    private Note mNote;

    private View mNoteLayout;

    private TextView mNoteColorView;
    private TextView mDeadlineView;
    private TextView mCategoryView;

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

    @SuppressWarnings("SameParameterValue")
    @NonNull
    static NoteFragment newInstance(@NonNull Note.Type noteType) {
        Bundle args = new Bundle();
        NoteFragment fragment = new NoteFragment();

        args.putSerializable(ARG_NOTE_TYPE, noteType);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    static String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mContext = context;
        mFragmentManager = getFragmentManager();
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
                            // checked
                            (UUID) Objects.requireNonNull(args.getSerializable(ARG_NOTE_ID)))
                            .clone();
                } catch (IllegalAccessException | CloneNotSupportedException ignored) {}
            }

            if (args.containsKey(ARG_NOTE_TYPE)) { // create new
                mNote = Note.newInstance(
                        // checked
                        (Note.Type) Objects.requireNonNull(args.getSerializable(ARG_NOTE_TYPE)));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultIntent) {
        // processes deadline date and time query results
        if (resultCode == Activity.RESULT_OK && resultIntent != null) {

            switch (requestCode) {
                case REQUEST_DATE:
                    Calendar date = (Calendar) resultIntent
                            .getSerializableExtra(DatePickerDialogFragment.RESULT_DATE);

                    if (date != null) {
                        mNote.setDeadline(date);
                        requestTime();
                    } else {
                        if (mNote.isDeadlined()) {
                            mNote.setDeadline(null);
                        }
                    }

                    updateDeadlineView();
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

        if (resultCode == Activity.RESULT_CANCELED
                && requestCode == REQUEST_DATE
                && mNote.isDeadlined()) {

            requestTime();
        }
    }

    private void requestTime() {
        Calendar deadline = mNote.getDeadline();

        TimePickerDialogFragment timePicker = TimePickerDialogFragment
                .getInstance(Objects.requireNonNull(deadline)); // was set

        timePicker.setTargetFragment(NoteFragment.this, REQUEST_TIME);

        timePicker.show(mFragmentManager, DIALOG_TIME_PICKER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mNote = mNotesKeeper.getTemporarilySavedNote();
        }

        mNoteLayout = inflater.inflate(R.layout.fragment_note, container, false);

        colorInstallation();
        deadlineInstallation();
        categoryInstallation();

        mTitleView = mNoteLayout.findViewById(R.id.title_note);
        mTitleView.setText(mNote.getTitle());

        mContentView = mNoteLayout.findViewById(R.id.content_view);
        mNote.fillContentView(mContentView, mContext);

        tagsInstallation();

        Button mBackButton = mNoteLayout.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View backButton) {
                saveNote();
                mFragmentManager.popBackStack();
            }
        });

        return mNoteLayout;
    }

    private void colorInstallation() {
        final Group mColorPalette = mNoteLayout.findViewById(R.id.palette_color);

        View.OnClickListener onColorChangeListener = new View.OnClickListener() {
            @Override
            public void onClick(View palette) {
                switch (palette.getId()) {
                    case R.id.color_none:
                        mNote.setColor(Note.Color.NONE);
                        break;
                    case R.id.color_accessory:
                        mNote.setColor(Note.Color.ACCESSORY);
                        break;
                    case R.id.color_quiet:
                        mNote.setColor(Note.Color.QUIET);
                        break;
                    case R.id.color_normal:
                        mNote.setColor(Note.Color.NORMAL);
                        break;
                    case R.id.color_attention:
                        mNote.setColor(Note.Color.ATTENTION);
                        break;
                    case R.id.color_urgent:
                        mNote.setColor(Note.Color.URGENT);
                        break;
                    default: // ignored
                }
                applyNoteColor();
                mColorPalette.setVisibility(View.GONE);
            }
        };

        mNoteLayout.findViewById(R.id.color_none).setOnClickListener(onColorChangeListener);
        mNoteLayout.findViewById(R.id.color_accessory).setOnClickListener(onColorChangeListener);
        mNoteLayout.findViewById(R.id.color_quiet).setOnClickListener(onColorChangeListener);
        mNoteLayout.findViewById(R.id.color_normal).setOnClickListener(onColorChangeListener);
        mNoteLayout.findViewById(R.id.color_attention).setOnClickListener(onColorChangeListener);
        mNoteLayout.findViewById(R.id.color_urgent).setOnClickListener(onColorChangeListener);

        mNoteColorView = mNoteLayout.findViewById(R.id.color_note);
        mNoteColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View noteColorView) {
                toggleVisibility(mColorPalette);
            }
        });

        applyNoteColor();
    }

    private void toggleVisibility(@NonNull Group hiddenGroup) {
        if (hiddenGroup.getVisibility() == View.GONE) {
            hiddenGroup.setVisibility(View.VISIBLE);
        } else {
            hiddenGroup.setVisibility(View.GONE);
        }
    }

    private void applyNoteColor() {
        int color;
        int palette;

        switch (mNote.getColor()) {
            case URGENT:
                color = R.color.colorUrgentNote;
                palette = R.drawable.palette_color_urgent;
                break;
            case ATTENTION:
                color = R.color.colorAttentionNote;
                palette = R.drawable.palette_color_attention;
                break;
            case NORMAL:
                color = R.color.colorNormalNote;
                palette = R.drawable.palette_color_normal;
                break;
            case QUIET:
                color = R.color.colorQuietNote;
                palette = R.drawable.palette_color_quiet;
                break;
            case ACCESSORY:
                color = R.color.colorAccessoryNote;
                palette = R.drawable.palette_color_accessory;
                break;
            default:
                color = R.color.colorNoneNote;
                palette = R.drawable.palette_color_none;
        }

        mNoteLayout.setBackgroundColor(getResources().getColor(color));
        mNoteColorView.setBackground(getResources().getDrawable(palette));
    }

    private void deadlineInstallation() {
        mDeadlineView = mNoteLayout.findViewById(R.id.deadline_note);

        mDeadlineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View deadlineView) {
                requestDate();
            }
        });

        updateDeadlineView();
    }

    private void requestDate() {
        DatePickerDialogFragment datePicker;

        if (mNote.isDeadlined()) {
            Calendar deadline = mNote.getDeadline();
            datePicker = DatePickerDialogFragment
                    .getInstance(Objects.requireNonNull(deadline)); // checked
        } else {
            datePicker = DatePickerDialogFragment.newInstance();
        }

        datePicker.setTargetFragment(NoteFragment.this, REQUEST_DATE);
        datePicker.show(mFragmentManager, DIALOG_DATE_PICKER);
    }

    private void updateDeadlineView() {
        if (mNote.isDeadlined()) {
            mDeadlineView.setText(mNote.getDeadline(mContext));
        } else mDeadlineView.setText("");

        applyDeadlineViewStyles();
    }

    private void applyDeadlineViewStyles() {
        int color;

        if (mNote.isDeadlined()) {
            color = R.color.colorDeadlineLightAhead;
            mDeadlineView.setBackground(getResources().getDrawable(R.drawable.rounded_fill_field));
        } else {
            color = R.color.colorDeadlineAhead;
            mDeadlineView.setBackground(getResources().getDrawable(R.drawable.rounded_not_fill_field));
        }

        switch (mNote.getStatus()) {
            case OVERDUE:
                color = R.color.colorDeadlineOverdue;
                mDeadlineView.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case IMMEDIATE:
                mDeadlineView.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            default:
                mDeadlineView.setTypeface(Typeface.DEFAULT);
        }

        mDeadlineView.setTextColor(getResources().getColor(color));
    }

    private void categoryInstallation() {
        final Group mCategoriesPanel = mNoteLayout.findViewById(R.id.categories_panel);

        final FlexboxLayout mCategoriesLayout = mNoteLayout.findViewById(R.id.categories_note);
        showCategories(mCategoriesPanel, mCategoriesLayout);

        mCategoryView = mNoteLayout.findViewById(R.id.category_note);
        mCategoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(mCategoriesPanel);
                mCategoriesLayout.removeAllViews();
                showCategories(mCategoriesPanel, mCategoriesLayout);
            }
        });

        updateCategoryView();
    }

    private void showCategories(@NonNull final Group categoriesPanel,
                                @NonNull final FlexboxLayout categoriesLayout) {

        Set<Category> allCategories = mNotesKeeper.getCategories();

        final EditText plusCategoryView = createPlusItemView(categoriesLayout,
                getString(R.string.add_new_category_hint),
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView plusItemView,
                                                  int actionId,
                                                  KeyEvent keyEvent) {

                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            addNewCategory((EditText) plusItemView);
                            updateCategoryView();

                            categoriesLayout.removeAllViews();
                            categoriesPanel.setVisibility(View.GONE);

                            return true;
                        }
                        return false;
                    }
                });

        if (mNote.isCategorized()) {
            TextView withoutCategory = createPanelItemView(categoriesLayout,
                    getString(R.string.note_category_hint),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View categoryItemView) {
                            mNote.setCategory(null);
                            updateCategoryView();

                            categoriesLayout.removeAllViews();
                            categoriesPanel.setVisibility(View.GONE);
                        }
                    },
                    null);
            categoriesLayout.addView(withoutCategory);
        }

        for (Category category : allCategories) {
            CheckBox categoryItemView =
                    createCategoryView(categoriesPanel, categoriesLayout, category.getTitle());

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
                                        @NonNull String plusItemHint,
                                        @NonNull TextView.OnEditorActionListener onActionDoneListener) {

        EditText plusItemView = (EditText) getLayoutInflater()
                .inflate(R.layout.plus_item_view, panelLayout, false);

        plusItemView.setHint(plusItemHint);
        plusItemView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        plusItemView.setOnEditorActionListener(onActionDoneListener);

        return plusItemView;
    }

    private void addNewCategory(@NonNull EditText plusItemView) {
        String title = plusItemView.getText().toString().trim();
        title = upFirstLetter(title);

        mNotesKeeper.addCategory(title);
        try {
            mNote.setCategory(mNotesKeeper.getCategory(title));
        } catch (IllegalAccessException ignored) {} // impossible
    }

    private void updateCategoryView() {
        if (mNote.isCategorized()) {
            mCategoryView.setText(Objects.requireNonNull(mNote.getCategory()).getTitle()); // checked
        } else {
            mCategoryView.setText("");
        }

        applyCategoryViewStyles();
    }

    private void applyCategoryViewStyles() {
        if (mNote.isCategorized()) {
            mCategoryView.setBackground(getResources().getDrawable(R.drawable.rounded_fill_field));
            mCategoryView.setTextColor(getResources().getColor(R.color.colorLightText));
        } else {
            mCategoryView.setBackground(getResources().getDrawable(R.drawable.rounded_not_fill_field));
            mCategoryView.setTextColor(getResources().getColor(R.color.colorPrimaryText));
        }
    }

    @NonNull
    private CheckBox createCategoryView(@NonNull final Group categoriesPanel,
                                        @NonNull final FlexboxLayout categoriesLayout,
                                        @NonNull final String title) {

        return createPanelItemView(categoriesLayout, title,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View categoryItemView) {
                        CompoundButton category = (CompoundButton) categoryItemView;
                        if (!mNote.hasCategory(category.getText().toString())) {
                            try {
                                mNote.setCategory(mNotesKeeper.getCategory((category.getText().toString())));
                                updateCategoryView();
                            } catch (IllegalAccessException ignored) {} // impossible
                        }
                        categoriesLayout.removeAllViews();
                        categoriesPanel.setVisibility(View.GONE);
                    }
                },
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View categoryItemView) {
                        final CompoundButton category = (CompoundButton) categoryItemView;
                        new AlertDialog.Builder(mContext)
                                .setTitle(getString(R.string.delete_category_dialog_title))
                                .setMessage(getString(R.string.delete_category_dialog_message))
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String title = category.getText().toString();
                                                if (mNote.hasCategory(title)) {
                                                    mNote.setCategory(null);
                                                    updateCategoryView();
                                                }
                                                categoriesLayout.removeAllViews();
                                                categoriesPanel.setVisibility(View.GONE);
                                                mNotesKeeper.removeCategory(title);
                                                Toast.makeText(mContext,
                                                        getString(R.string.delete_category_toast_message),
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
    private CheckBox createPanelItemView(@NonNull FlexboxLayout panelLayout,
                                         @NonNull String title,
                                         @NonNull View.OnClickListener onClickListener,
                                         @Nullable View.OnLongClickListener onLongClickListener) {

        CheckBox itemView = (CheckBox) getLayoutInflater()
                .inflate(R.layout.panel_item_view, panelLayout, false);

        itemView.setText(title);
        itemView.setOnClickListener(onClickListener);
        itemView.setOnLongClickListener(onLongClickListener);

        return itemView;
    }

    private void tagsInstallation() {
        FlexboxLayout mTagsLayout = mNoteLayout.findViewById(R.id.tags_note);
        showTags(mTagsLayout);
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

    void saveNote() {
        mNote.setTitle(mTitleView.getText().toString().trim());
        mNote.setContent(mContentView);

        if (mNote.isModified()) {
            if (!mNote.getTitle().isEmpty() || !mNote.isContentEmpty()) {
                mNotesKeeper.addNote(mNote);
                Toast.makeText(mContext,
                        getString(R.string.save_note_toast_message),
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                requestSaveConfirmation();
            }
        }
    }

    private void requestSaveConfirmation() {
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.save_empty_note_dialog_title))
                .setMessage(getString(R.string.save_empty_note_dialog_message))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mNotesKeeper.addNote(mNote);
                                Toast.makeText(mContext,
                                        getString(R.string.save_note_toast_message),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                .create()
                .show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mNotesKeeper.temporarilySaveNote(mNote);
    }
}
