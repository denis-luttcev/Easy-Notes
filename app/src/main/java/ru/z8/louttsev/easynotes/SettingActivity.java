package ru.z8.louttsev.easynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import ru.z8.louttsev.easynotes.security.Protector;

public class SettingActivity extends AppCompatActivity {
    private Protector mProtector;
    private FrameLayout mProtectionLayout;
    private Button mSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mProtector = App.getProtector();

        boolean protectionEnabled = false;
        try {
            protectionEnabled = mProtector.isProtectionEnabled();
        } catch (Exception ignored) {}

        Switch protection = findViewById(R.id.protection);
        protection.setChecked(protectionEnabled);
        protection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    switchProtectionToEnabled(compoundButton);
                } else {
                    switchProtectionToDisabled();
                }
            }
        });

        mProtectionLayout = findViewById(R.id.protection_container);
        if (protection.isChecked()) {
            mProtectionLayout.setVisibility(View.VISIBLE);
        } else {
            mProtectionLayout.setVisibility(View.GONE);
        }

        /*mProtectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mProtectionLayout.getLayoutParams();
                params.topMargin = 0;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                mProtectionLayout.setLayoutParams(params);
            }
        });*/

        mSaveBtn = findViewById(R.id.save_setting_btn);
    }

    private void switchProtectionToEnabled(CompoundButton compoundButton) {
        mProtectionLayout.setVisibility(View.VISIBLE);
        mSaveBtn.setVisibility(View.GONE);
        mProtector.enableProtection(mProtectionLayout);
        try {
            if (!mProtector.isProtectionEnabled()) {
                Toast.makeText(SettingActivity.this,
                        getString(R.string.protection_enabled_error_toast_message),
                        Toast.LENGTH_SHORT)
                        .show();
                compoundButton.setChecked(false);
                switchProtectionToDisabled();
            }
        } catch (Exception ignored) {}
    }

    private void switchProtectionToDisabled() {
        mProtectionLayout.setVisibility(View.GONE);
        mSaveBtn.setVisibility(View.VISIBLE);
        mProtector.disableProtection();
    }
}
