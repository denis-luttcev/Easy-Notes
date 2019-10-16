package ru.z8.louttsev.easynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Objects;

import ru.z8.louttsev.easynotes.security.KeyKeeper;

public class SettingActivity extends AppCompatActivity {
    private KeyKeeper mKeyKeeper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mKeyKeeper = App.getKeyKeeper();

        final View group = findViewById(R.id.pin_group);

        Switch protection = findViewById(R.id.protection);
        try {
            protection.setChecked(!mKeyKeeper.isProtectionDisabled());
        } catch (Exception ignored) {}
        protection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    group.setVisibility(View.VISIBLE);
                } else {
                    group.setVisibility(View.GONE);
                }
            }
        });
    }
}
