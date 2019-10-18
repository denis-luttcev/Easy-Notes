package ru.z8.louttsev.easynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import ru.z8.louttsev.easynotes.security.Protector;

public class SettingActivity extends AppCompatActivity {
    private Protector mProtector;

    private Switch mProtectionSettingSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mProtector = App.getProtector();

        initViews();
    }

    private void initViews() {
        mProtectionSettingSwitch = findViewById(R.id.protection_setting_switch);
        mProtectionSettingSwitch
                .setChecked(mProtector.isProtectionConfigured() && mProtector.isProtectionEnabled());
        mProtectionSettingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton protectionSettingSwitch, boolean isChecked) {
                //TODO: logged
                if (isChecked) {
                    switchProtectionToEnabled();
                } else {
                    switchProtectionToDisabled();
                }
            }
        });
    }

    private void switchProtectionToEnabled() {
        //TODO: logged
        if (!mProtector.enableProtection(getSupportFragmentManager())) {
            Toast.makeText(SettingActivity.this,
                    getString(R.string.protection_enabled_error_toast_message),
                    Toast.LENGTH_SHORT)
                    .show();
            mProtectionSettingSwitch.setChecked(false);
            switchProtectionToDisabled();
        }
    }

    private void switchProtectionToDisabled() {
        //TODO: Logged
        mProtector.disableProtection();
    }
}
