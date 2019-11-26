package ru.z8.louttsev.easynotes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        mProtector.updateFragmentManager(getSupportFragmentManager());

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
                    if (!mProtector.isProtectionEnabled()) {
                        switchProtectionToEnabled();
                    }
                } else {
                    mProtector.checkAuthorization(new Protector.ResultListener() {
                        @Override
                        public void onProtectionResultSuccess() {
                            switchProtectionToDisabled();
                        }

                        @Override
                        public void onProtectionResultFailure() {
                            mProtectionSettingSwitch.setChecked(true);
                            Toast.makeText(SettingActivity.this,
                                    getString(R.string.protection_disabled_error_toast_message),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }, true);
                }
            }
        });
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        mProtector.updateFragmentManager(getSupportFragmentManager());
    }

    private void changeProtectionKey() {
        mProtector.checkAuthorization(new Protector.ResultListener() {
            @Override
            public void onProtectionResultSuccess() {
                mProtector
                        .enableProtection(new Protector.ResultListener() {
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

            @Override
            public void onProtectionResultFailure() {
                Toast.makeText(SettingActivity.this,
                        getString(R.string.protection_key_changed_error_toast_message),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }, true);
    }

    private void switchProtectionToEnabled() {
        mProtector
                .enableProtection(new Protector.ResultListener() {
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
