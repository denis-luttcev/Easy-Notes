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

import com.google.android.flexbox.FlexboxLayout;

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
    public View getView(int position, View noteItemListView, ViewGroup viewGroup) {
        if (noteItemListView == null) {
            noteItemListView = mInflater.inflate(R.layout.note_list_item, viewGroup, false);
        }

        Note note = mNotes.get(position);

        if (note.isColored()) {
            applyNoteViewColor(noteItemListView, note);
        }

        TextView titleView = noteItemListView.findViewById(R.id.note_title);
        if (note.isTitled()) {
            titleView.setText(note.getTitle());
        } else titleView.setVisibility(View.GONE);

        note.fillContentPreView((FrameLayout) noteItemListView.findViewById(R.id.content_preview), mContext);

        TextView categoryView = noteItemListView.findViewById(R.id.note_category);
        if (note.isCategorized()) {
            categoryView.setText(Objects.requireNonNull(note.getCategory()).getTitle());
        } else {
            categoryView.setVisibility(View.GONE);
        }

        FlexboxLayout tagsLineView = noteItemListView.findViewById(R.id.note_tags);
        if (note.isTagged()) {
            showTags(note, tagsLineView);
        } else {
            tagsLineView.setVisibility(View.GONE);
        }

        ImageView deadlineIcon = noteItemListView.findViewById(R.id.deadline_icon);
        TextView deadlineView = noteItemListView.findViewById(R.id.note_deadline);
        if (note.isDeadlined()) {
            showDeadline(note, deadlineView);
            deadlineIcon.setColorFilter(R.color.colorAccent);
        } else {
            deadlineIcon.setVisibility(View.GONE);
            deadlineView.setVisibility(View.GONE);
        }

        return noteItemListView;
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

    private void showTags(@NonNull Note note, @NonNull FlexboxLayout tagsLineView) {
        Set<Tag> noteTags = note.getTags();
        Iterator<Tag> tags = Objects.requireNonNull(noteTags).iterator();
        while (tags.hasNext()){
            TextView tagView = (TextView) mInflater.inflate(R.layout.tag_list_item, tagsLineView, false);
            tagView.setText(tags.next().getTitle());
            if (!tags.hasNext()) {
                FlexboxLayout.LayoutParams layoutParams = (FlexboxLayout.LayoutParams) tagView.getLayoutParams();
                layoutParams.setMarginEnd(0);
                tagView.setLayoutParams(layoutParams);
            }
            tagsLineView.addView(tagView);
        }
    }

    private void showDeadline(@NonNull Note note, @NonNull TextView deadlineView) {
        Calendar deadline = note.getDeadline();
        deadlineView.setText(formatDeadline(Objects.requireNonNull(deadline)));
        applyDeadlineColor(note, deadlineView);
    }

    @NonNull
    private String formatDeadline(@NonNull Calendar deadline) {
        StringBuilder dateRepresent = new StringBuilder();
        if (isYesterday(deadline)) {
            dateRepresent.append(mContext.getString(R.string.yesterday));
        } else if (isToday(deadline)) {
            dateRepresent.append(mContext.getString(R.string.today));
        } else if (isTomorrow(deadline)) {
            dateRepresent.append(mContext.getString(R.string.tomorrow));
        } else {
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
            dateRepresent.append(dateFormat.format(deadline.getTime()));
            dateRepresent.append(" ");
        }
        DateFormat timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        dateRepresent.append(timeFormat.format(deadline.getTime()));
        return dateRepresent.toString();
    }

    private boolean isToday(@NonNull Calendar date) {
        final Calendar today = Calendar.getInstance();
        return date.get(Calendar.DATE) == today.get(Calendar.DATE)
                && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.YEAR) == today.get(Calendar.YEAR);
    }

    private boolean isYesterday(@NonNull Calendar date) {
        Calendar nextDay = (Calendar) date.clone();
        nextDay.add(Calendar.DATE, 1);
        return isToday(nextDay);
    }

    private boolean isTomorrow(@NonNull Calendar date) {
        Calendar prevDay = (Calendar) date.clone();
        prevDay.add(Calendar.DATE, -1);
        return isToday(prevDay);
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
