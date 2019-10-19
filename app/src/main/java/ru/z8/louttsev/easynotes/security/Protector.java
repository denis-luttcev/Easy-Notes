package ru.z8.louttsev.easynotes.security;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public interface Protector {
    interface OnProtectionResultListener{
        void onProtectionResultSuccess();
        void onProtectionResultFailure();
    }

    boolean isProtectionConfigured();
    boolean isProtectionEnabled();

    /**
     * Tryes to enable protection (possible technical problems or user refusal)
     */
    void enableProtection(@NonNull FragmentManager fragmentManager,
                          @NonNull OnProtectionResultListener resultListener);
    void disableProtection();

    /**
     * Technical problems and user refusal are need considered as authorization denied
     */
    void checkAuthorization(@NonNull FragmentManager fragmentManager,
                            @NonNull OnProtectionResultListener resultListener);
}
