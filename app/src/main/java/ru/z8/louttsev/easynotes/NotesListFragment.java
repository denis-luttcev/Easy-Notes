package ru.z8.louttsev.easynotes;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.Tag;

public class NotesListFragment extends Fragment {
    private NotesKeeper mNotesKeeper;
    private RecyclerView mNotesList;
    private NotesAdapter mNotesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotesKeeper = App.getNotesKeeper();
    }

    private class NoteHolder extends RecyclerView.ViewHolder {
        private LayoutInflater mInflater;

        private TextView mNoteTitleView;
        private TextView mNoteCategoryView;
        private FrameLayout mNoteContentPreView;
        private FlexboxLayout mNoteTagsLineView;
        private ImageView mNoteDeadlineIcon;
        private TextView mNoteDeadlineView;

        public NoteHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.note_list_item, parent, false));

            mInflater = inflater;

            mNoteTitleView = itemView.findViewById(R.id.note_title);
            mNoteCategoryView = itemView.findViewById(R.id.note_category);
            mNoteContentPreView = itemView.findViewById(R.id.content_preview);
            mNoteTagsLineView = itemView.findViewById(R.id.note_tags);
            mNoteDeadlineIcon = itemView.findViewById(R.id.deadline_icon);
            mNoteDeadlineView = itemView.findViewById(R.id.note_deadline);
        }

        public void bindNote(@NonNull Note note) {
            if (note.isColored()) {
                applyNoteViewColor(itemView, note);
            }

            if (note.isTitled()) {
                mNoteTitleView.setText(note.getTitle());
            } else mNoteTitleView.setVisibility(View.GONE);

            if (note.isCategorized()) {
                mNoteCategoryView.setText(Objects.requireNonNull(note.getCategory()).getTitle());
            } else {
                mNoteCategoryView.setVisibility(View.GONE);
            }

            note.fillContentPreView(mNoteContentPreView, getActivity());

            if (note.isTagged()) {
                showTags(note, mNoteTagsLineView);
            } else {
                mNoteTagsLineView.setVisibility(View.GONE);
            }

            if (note.isDeadlined()) {
                mNoteDeadlineView.setText(note.getDeadlineRepresent(getActivity()));
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
            //noteView.setBackgroundColor(getResources().getColor(color));
        }

        private void showTags(@NonNull Note note, @NonNull FlexboxLayout tagsLineView) {
            Set<Tag> noteTags = note.getTags();
            Iterator<Tag> tags = Objects.requireNonNull(noteTags).iterator();
            while (tags.hasNext()){
                TextView tagView = (TextView) mInflater
                        .inflate(R.layout.tag_list_item, tagsLineView, false);
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
    }

    private class NotesAdapter extends RecyclerView.Adapter<NoteHolder> {
        private List<Note> mNotes;

        public NotesAdapter(List<Note> mNotes) {
            this.mNotes = mNotes;
        }

        @NonNull
        @Override
        public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new NoteHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
            holder.bindNote(mNotes.get(position));
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View notesListView = inflater.inflate(R.layout.fragment_notes_list, container, false);

        Toolbar mToolBar = notesListView.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolBar);

        FloatingActionButton mAddNoteButton = notesListView.findViewById(R.id.fab);
        mAddNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: change implementation to add note
                /*FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                fragment = new NoteFragment();
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();*/
            }
        });

        mNotesList = notesListView.findViewById(R.id.notes_list);
        mNotesList.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateNotesList();

        return notesListView;
    }

    private void updateNotesList() {
        mNotesAdapter = new NotesAdapter(mNotesKeeper.getNotes());
        mNotesList.setAdapter(mNotesAdapter);
    }
}
