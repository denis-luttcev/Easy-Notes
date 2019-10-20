package ru.z8.louttsev.easynotes;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.Tag;

class NotesAdapter extends BaseAdapter {
    private List<Note> mNotes;
    private LayoutInflater mInflater;
    private Context mContext;

    public NotesAdapter(List<Note> notes, Context context) {
        mContext = context;
        mNotes = notes;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return mNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View notePreView, ViewGroup viewGroup) {
        if (notePreView == null) {
            notePreView = mInflater.inflate(R.layout.note_list_item, viewGroup, false);
        }

        Note note = mNotes.get(position);

        if (note.isColored()) {
            applyNoteViewColor(notePreView, note);
        }

        TextView titleView = notePreView.findViewById(R.id.note_title);
        if (note.isTitled()) {
            titleView.setText(note.getTitle());
        } else titleView.setVisibility(View.GONE);

        note.fillContentPreView((FrameLayout) notePreView.findViewById(R.id.content_preview), mContext);

        ImageView categoryIcon = notePreView.findViewById(R.id.category_icon);
        TextView categoryView = notePreView.findViewById(R.id.note_category);
        if (note.isCategorized()) {
            categoryView.setText(Objects.requireNonNull(note.getCategory()).getTitle());
        } else {
            categoryIcon.setVisibility(View.GONE);
            categoryView.setVisibility(View.GONE);
        }

        TextView tagsView = notePreView.findViewById(R.id.note_tags);
        if (note.isTagged()) {
            showTags(note, tagsView);
        } else tagsView.setVisibility(View.GONE);

        TextView deadlineView = notePreView.findViewById(R.id.note_deadline);
        if (note.isDeadlined()) {
            showDeadline(note, deadlineView);
        } else deadlineView.setVisibility(View.GONE);

        return notePreView;
    }

    private void applyNoteViewColor(@NonNull View noteView, @NonNull Note note) {
        int color = ContextCompat.getColor(mContext, R.color.colorNoneNote);
        switch (note.getColor()) {
            case URGENT:
                color = ContextCompat.getColor(mContext, R.color.colorUrgentNote);
                break;
            case ATTENTION:
                color =ContextCompat.getColor(mContext, R.color.colorAttentionNote);
                break;
            case NORMAL:
                color = ContextCompat.getColor(mContext, R.color.colorNormalNote);
                break;
            case QUIET:
                color = ContextCompat.getColor(mContext, R.color.colorQuietNote);
                break;
            case ACCESSORY:
                color = ContextCompat.getColor(mContext, R.color.colorAccessoryNote);
                break;
            default: // ignored
        }
        noteView.setBackgroundColor(color);
    }

    private void showTags(@NonNull Note note, @NonNull TextView tagsView) {
        final String SOFT_HYPHEN = "­";

        Set<Tag> noteTags = note.getTags();
        StringBuilder tagsLine = new StringBuilder();
        Iterator<Tag> tags = Objects.requireNonNull(noteTags).iterator();
        while (tags.hasNext()){
            tagsLine.append("#");
            tagsLine.append(tags.next().getTitle());
            if (tags.hasNext()) {
                tagsLine.append(" ");
                tagsLine.append(SOFT_HYPHEN);
            }
        }
        tagsView.setText(tagsLine.toString());
    }

    private void showDeadline(@NonNull Note note, @NonNull TextView deadlineView) {
        Calendar deadline = note.getDeadline();
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        deadlineView.setText(dateFormat.format(Objects.requireNonNull(deadline).getTime()));
        applyDeadlineColor(note, deadlineView);
    }

    private void applyDeadlineColor(@NonNull Note note, @NonNull TextView deadlineView) {
        int color = ContextCompat.getColor(mContext, R.color.colorDeadlineAhead);
        switch (note.getDeadlineStatus(Calendar.getInstance())) {
            case OVERDUE:
                color = ContextCompat.getColor(mContext, R.color.colorDeadlineOverdue);
                deadlineView.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case IMMEDIATE:
                color = ContextCompat.getColor(mContext, R.color.colorDeadlineImmediate);
                deadlineView.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            default:
                deadlineView.setTypeface(Typeface.DEFAULT);
        }
        deadlineView.setTextColor(color);
    }
}
