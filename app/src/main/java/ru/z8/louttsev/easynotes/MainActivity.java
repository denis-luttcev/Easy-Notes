package ru.z8.louttsev.easynotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ru.z8.louttsev.easynotes.security.Protector;

public class MainActivity extends AppCompatActivity {
    private Protector mProtector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProtector = App.getProtector();

        if (mProtector.isProtectionNotConfigured()) {
            Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingIntent);
        } else {
            if (mProtector.isProtectionEnabled()) {
                mProtector.checkAuthorization(getSupportFragmentManager(),
                        new Protector.ResultListener() {
                            @Override
                            public void onProtectionResultSuccess() {
                                // ignored
                            }

                            @Override
                            public void onProtectionResultFailure() {
                                Toast.makeText(MainActivity.this,
                                        getString(R.string.access_denied_toast_message),
                                        Toast.LENGTH_LONG)
                                        .show();
                                finish();
                            }
                        });
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            //TODO newInstance()
            fragment = new NotesListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        /*Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new NoteFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
