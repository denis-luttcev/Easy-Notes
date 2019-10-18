package ru.z8.louttsev.easynotes.security;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public interface Protector {
    boolean isProtectionConfigured();
    boolean isProtectionEnabled();

    /**
     * Tryes to enable protection (possible technical problems or user refusal)
     * @return true if protection enabled successfully
     */
    boolean enableProtection(@NonNull FragmentManager fragmentManager);
    void disableProtection();

    /**
     * Technical problems and user refusal are need considered as authorization denied
     */
    boolean isAccessAllowed(@NonNull FragmentManager fragmentManager);
}
