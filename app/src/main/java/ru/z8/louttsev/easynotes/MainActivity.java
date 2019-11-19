package ru.z8.louttsev.easynotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ru.z8.louttsev.easynotes.security.Protector;

public class MainActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Protector mProtector = App.getProtector();
        mFragmentManager = getSupportFragmentManager();

        if (mProtector.isProtectionNotConfigured()) {
            Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingIntent);
        } else {
            if (mProtector.isProtectionEnabled()) {
                mProtector.checkAuthorization(mFragmentManager,
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
                        }, false);
            }
        }

        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = NotesListFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
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

    @Override
    public void onBackPressed() {
        NoteFragment fragment
                = (NoteFragment) mFragmentManager.findFragmentByTag(NoteFragment.getFragmentTag());

        if (fragment != null) {
            fragment.closeNote();
        } else {
            super.onBackPressed();
        }
    }
}
