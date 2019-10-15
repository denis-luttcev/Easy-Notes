package ru.z8.louttsev.easynotes;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

import ru.z8.louttsev.easynotes.datamodel.Category;
import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.Tag;
import ru.z8.louttsev.easynotes.datamodel.TextNote;

public class NotesActivity extends AppCompatActivity {
    private NotesKeeper mNotesKeeper;
    private BaseAdapter mNotesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mNotesKeeper = App.getNotesKeeper();
        initViews();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new NoteFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    private void initViews() {
        Toolbar mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        FloatingActionButton mAddButton = findViewById(R.id.fab);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: change implementation to add note
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        readData();

        //TODO: change implementation to RecyclerView
        /*ListView mListView = findViewById(R.id.notes_list);
        mNotesAdapter = new NotesAdapter(mNotesKeeper.getNotes(), NotesActivity.this);
        mListView.setAdapter(mNotesAdapter);*/
    }

    private void readData() {
        //TODO: change to read from db

        Category category1 = new Category("Holiday");
        Category category2 = new Category("Work");
        Tag tag1 = new Tag("Ideas");
        Tag tag2 = new Tag("Todo");
        Tag tag3 = new Tag("Photo");
        Tag tag4 = new Tag("Smile");
        Tag tag5 = new Tag("Class");
        Tag tag6 = new Tag("Share");
        Tag tag7 = new Tag("Common");
        Tag tag8 = new Tag("Private");
        Tag tag9 = new Tag("Plus");
        Tag tag10 = new Tag("Native");

        Note note;
        note = new TextNote();
        note.setTitle("note1");
        note.setContent("note1 content");
        note.setColor(Note.Color.ATTENTION);
        note.setCategory(category1);
        mNotesKeeper.addNote(note);

        note = new TextNote();
        note.setTitle(null);
        note.setContent("note2 content");
        note.setCategory(category2);
        note.markTag(tag1);
        note.setDeadline(Calendar.getInstance());
        mNotesKeeper.addNote(note);

        note = new TextNote();
        note.setTitle("note3");
        note.setContent("note3 long content: Lorem ipsum dolor sit amet, consectetur adipiscing elit. In varius malesuada neque sed pellentesque. Aenean sit amet luctus justo. Maecenas venenatis lorem sit amet orci ultricies maximus. Morbi sagittis neque vitae risus tristique tincidunt. Ut tellus lectus, tempor vitae iaculis quis, tempor non ex. Maecenas imperdiet pretium ligula ac rutrum. Mauris massa felis, vulputate eget sem et, ullamcorper convallis augue.");
        note.setColor(Note.Color.ACCESSORY);
        note.markTag(tag1);
        note.markTag(tag2);
        note.markTag(tag3);
        note.markTag(tag4);
        note.markTag(tag5);
        note.markTag(tag6);
        note.markTag(tag7);
        note.markTag(tag8);
        note.markTag(tag9);
        note.markTag(tag10);
        mNotesKeeper.addNote(note);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
