package ru.z8.louttsev.easynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;

@SuppressWarnings("WeakerAccess")
public class NotesListFragment extends Fragment {
    private NotesKeeper mNotesKeeper;
    private BaseAdapter mNotesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotesKeeper = App.getNotesKeeper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View notesListView = inflater.inflate(R.layout.fragment_notes_list, container, false);

        Toolbar mToolBar = notesListView.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolBar);

        FloatingActionButton mAddButton = notesListView.findViewById(R.id.fab);
        mAddButton.setOnClickListener(new View.OnClickListener() {
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

        //TODO: change implementation to RecyclerView
        ListView mListView = notesListView.findViewById(R.id.notes_list);
        mNotesAdapter = new NotesAdapter(mNotesKeeper.getNotes(), getActivity());
        mListView.setAdapter(mNotesAdapter);

        return notesListView;
    }
}