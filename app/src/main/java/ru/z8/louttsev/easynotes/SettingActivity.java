package ru.z8.louttsev.easynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import ru.z8.louttsev.easynotes.security.Protector;

public class SettingActivity extends AppCompatActivity {
    private Protector mProtector;

    private Switch mProtectionSettingSwitch;
    private Button mProtectionSettingChangeKeyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mProtector = App.getProtector();

        initViews();
    }

    private void initViews() {
        // set default protection status
        if (mProtector.isProtectionNotConfigured()) {
            mProtector.disableProtection();
        }

        mProtectionSettingChangeKeyButton = findViewById(R.id.protection_setting_change_key_button);
        mProtectionSettingChangeKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProtectionKey();
            }
        });

        mProtectionSettingSwitch = findViewById(R.id.protection_setting_switch);
        if (mProtector.isProtectionEnabled()) {
            mProtectionSettingSwitch.setChecked(true);
            mProtectionSettingChangeKeyButton.setVisibility(View.VISIBLE);
        } else {
            mProtectionSettingSwitch.setChecked(false);
            mProtectionSettingChangeKeyButton.setVisibility(View.GONE);
        }
        mProtectionSettingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton protectionSettingSwitch, boolean isChecked) {
                if (isChecked) {
                    switchProtectionToEnabled();
                } else {
                    switchProtectionToDisabled();
                }
            }
        });
    }

    private void changeProtectionKey() {
        mProtector
                .enableProtection(getSupportFragmentManager(), new Protector.ResultListener() {
            @Override
            public void onProtectionResultSuccess() {
                Toast.makeText(SettingActivity.this,
                        getString(R.string.protection_key_changed_success_toast_message),
                        Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onProtectionResultFailure() {
                Toast.makeText(SettingActivity.this,
                        getString(R.string.protection_key_changed_error_toast_message),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void switchProtectionToEnabled() {
        mProtector
                .enableProtection(getSupportFragmentManager(), new Protector.ResultListener() {
            @Override
            public void onProtectionResultSuccess() {
                Toast.makeText(SettingActivity.this,
                        getString(R.string.protection_enabled_success_toast_message),
                        Toast.LENGTH_SHORT)
                        .show();
                mProtectionSettingSwitch.setChecked(true);
                mProtectionSettingChangeKeyButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProtectionResultFailure() {
                Toast.makeText(SettingActivity.this,
                        getString(R.string.protection_enabled_error_toast_message),
                        Toast.LENGTH_SHORT)
                        .show();
                switchProtectionToDisabled();
            }
        });
    }

    private void switchProtectionToDisabled() {
        Toast.makeText(SettingActivity.this,
                getString(R.string.protection_disabled_toast_message),
                Toast.LENGTH_SHORT)
                .show();
        mProtectionSettingSwitch.setChecked(false);
        mProtectionSettingChangeKeyButton.setVisibility(View.GONE);
        mProtector.disableProtection();
    }
}
