package ru.z8.louttsev.easynotes;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

import ru.z8.louttsev.easynotes.datamodel.Category;
import ru.z8.louttsev.easynotes.datamodel.Note;
import ru.z8.louttsev.easynotes.datamodel.NotesKeeper;
import ru.z8.louttsev.easynotes.datamodel.NotesRepository;
import ru.z8.louttsev.easynotes.datamodel.Tag;
import ru.z8.louttsev.easynotes.datamodel.TextNote;

public class MainActivity extends AppCompatActivity {

    //TODO: change to singletone
    private NotesKeeper mNotesKeeper = new NotesRepository();
    private BaseAdapter mNoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
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

        //TODO: change to read from db
        readData();

        //TODO: change implementation to RecyclerView
        ListView mListView = findViewById(R.id.notes_list);
        mNoteAdapter = new NoteAdapter(mNotesKeeper.getNotes(), MainActivity.this);
        mListView.setAdapter(mNoteAdapter);
    }

    private void readData() {
        Category category1 = new Category("Holiday");
        Category category2 = new Category("Work");
        Tag tag1 = new Tag("Ideas");
        Tag tag2 = new Tag("Todo");
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
        note.setContent("note3 content");
        note.setColor(Note.Color.ACCESSORY);
        note.markTag(tag1);
        note.markTag(tag2);
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
