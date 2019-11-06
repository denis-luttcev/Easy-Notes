package ru.z8.louttsev.easynotes;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.Tag;

public class NotesListFragment extends Fragment {
    private Context mContext;
    private FragmentManager mFragmentManager;

    private NotesKeeper mNotesKeeper;

    private NotesAdapter mNotesAdapter;

    private TextView mHelpLine;

    @NonNull
    static NotesListFragment newInstance() {
        return new NotesListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotesKeeper = App.getNotesKeeper();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mContext = context;
        mFragmentManager = getFragmentManager();
    }

    private class NoteHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private LayoutInflater mInflater;

        private Note mNote;

        private TextView mNoteTitleView;
        private TextView mNoteCategoryView;
        private FrameLayout mNoteContentPreView;
        private FlexboxLayout mNoteTagsLineView;
        private ImageView mNoteDeadlineIcon;
        private TextView mNoteDeadlineView;

        NoteHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.note_list_item_layout, parent, false));

            mInflater = inflater;

            mNoteTitleView = itemView.findViewById(R.id.note_title);
            mNoteCategoryView = itemView.findViewById(R.id.note_category);
            mNoteContentPreView = itemView.findViewById(R.id.content_pre_view);
            mNoteTagsLineView = itemView.findViewById(R.id.note_tags);
            mNoteDeadlineIcon = itemView.findViewById(R.id.deadline_icon);
            mNoteDeadlineView = itemView.findViewById(R.id.note_deadline);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bindNote(@NonNull Note note) {
            mNote = note;

            if (note.isColored()) {
                applyNoteViewColor(itemView, note);
            }

            if (note.isTitled()) {
                mNoteTitleView.setText(note.getTitle());
            } else mNoteTitleView.setVisibility(View.GONE);

            if (note.isCategorized()) {
                mNoteCategoryView.setText(Objects.requireNonNull(note.getCategory()).getTitle());
            } else mNoteCategoryView.setVisibility(View.GONE);

            note.fillContentPreView(mNoteContentPreView, mContext);

            if (note.isTagged()) {
                showTags(note, mNoteTagsLineView);
            } else mNoteTagsLineView.setVisibility(View.GONE);

            if (note.isDeadlined()) {
                mNoteDeadlineView.setText(note.getDeadline(mContext));
                applyDeadlineColor(note, mNoteDeadlineView);
            } else {
                mNoteDeadlineIcon.setVisibility(View.GONE);
                mNoteDeadlineView.setVisibility(View.GONE);
            }
        }

        private void applyNoteViewColor(@NonNull View noteView, @NonNull Note note) {
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

            ((CardView) noteView).setCardBackgroundColor(getResources().getColor(color));
        }

        private void showTags(@NonNull Note note, @NonNull FlexboxLayout tagsLineView) {
            Set<Tag> noteTags = note.getTags();

            Iterator<Tag> tags = noteTags.iterator();
            while (tags.hasNext()){
                TextView tagView = (TextView) mInflater
                        .inflate(R.layout.tag_pre_view, tagsLineView, false);

                tagView.setText(tags.next().getTitle());

                if (!tags.hasNext()) {
                    FlexboxLayout.LayoutParams layoutParams = (FlexboxLayout.LayoutParams) tagView.getLayoutParams();
                    layoutParams.setMarginEnd(0);
                    tagView.setLayoutParams(layoutParams);
                }

                tagsLineView.addView(tagView);
            }
        }

        private void applyDeadlineColor(@NonNull Note note, @NonNull TextView deadlineView) {
            int color = R.color.colorDeadlineAhead;

            switch (note.getStatus()) {
                case OVERDUE:
                    color = R.color.colorDeadlineOverdue;
                    deadlineView.setTypeface(Typeface.DEFAULT_BOLD);
                    break;
                case IMMEDIATE:
                    deadlineView.setTypeface(Typeface.DEFAULT_BOLD);
                    break;
                default:
                    deadlineView.setTypeface(Typeface.DEFAULT);
            }

            deadlineView.setTextColor(getResources().getColor(color));
        }

        @Override
        public void onClick(View view) {
            Fragment fragment = NoteFragment.getInstance(mNote.getId());
            mFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_container, fragment, NoteFragment.getFragmentTag())
                    .addToBackStack(NoteFragment.getFragmentTag())
                    .commit();
        }

        @Override
        public boolean onLongClick(View view) {
            requestRemoveConfirmation(mNote);
            return true;
        }
    }

    private void requestRemoveConfirmation(@NonNull final Note note) {
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.remove_note_dialog_title))
                .setMessage(getString(R.string.remove_note_dialog_message))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mNotesKeeper.removeNote(note.getId());
                        mNotesAdapter.notifyDataSetChanged();
                        applyHeplLineStyle();
                        Toast.makeText(mContext,
                                getString(R.string.remove_note_toast_message),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .create()
                .show();
    }

    private class NotesAdapter extends RecyclerView.Adapter<NoteHolder> {
        @NonNull
        @Override
        public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new NoteHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
            holder.bindNote(mNotesKeeper.getNote(position));
        }

        @Override
        public int getItemCount() {
            return mNotesKeeper.getNotesCount();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View mNotesListLayout
                = inflater.inflate(R.layout.fragment_notes_list, container, false);

        Toolbar mToolBar = mNotesListLayout.findViewById(R.id.notes_list_toolbar);
        ((AppCompatActivity) mContext).setSupportActionBar(mToolBar);

        FloatingActionButton mAddNoteButton = mNotesListLayout.findViewById(R.id.add_note_button);
        mAddNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: After adding new note types implement choice and another types call
                Fragment fragment = NoteFragment.newInstance(Note.Type.TEXT_NOTE);
                mFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_container, fragment, NoteFragment.getFragmentTag())
                        .addToBackStack(NoteFragment.getFragmentTag())
                        .commit();
            }
        });

        RecyclerView mNotesList = mNotesListLayout.findViewById(R.id.notes_list);
        mNotesList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNotesAdapter = new NotesAdapter();
        mNotesList.setAdapter(mNotesAdapter);

        mHelpLine = mNotesListLayout.findViewById(R.id.notes_list_help_line);
        applyHeplLineStyle();

        return mNotesListLayout;
    }

    private void applyHeplLineStyle() {
        if (mNotesAdapter.getItemCount() == 0) {
            mHelpLine.setVisibility(View.GONE);
        }
    }
}
