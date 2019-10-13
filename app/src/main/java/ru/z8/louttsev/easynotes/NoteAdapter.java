package ru.z8.louttsev.easynotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import ru.z8.louttsev.easynotes.datamodel.Note;

class NoteAdapter extends BaseAdapter {
    private List<Note> mNotes;
    private LayoutInflater mInflater;
    private Context mContext;

    public NoteAdapter(List<Note> notes, Context context) {
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.note_item_layout, viewGroup, false);
        }
        Note note = mNotes.get(position);
        ((TextView) view.findViewById(R.id.note_title)).setText(note.getTitle());
        note.fillContentPreView((FrameLayout) view.findViewById(R.id.content_preview), mContext);
        return view;
    }
}
