package ru.z8.louttsev.easynotes.security;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public interface Protector {
    interface ResultListener {
        void onProtectionResultSuccess();
        void onProtectionResultFailure();
    }

    boolean isProtectionNotConfigured();

    boolean isProtectionEnabled();

    void enableProtection(@NonNull FragmentManager fragmentManager,
                          @NonNull ResultListener resultListener);
    void disableProtection();

    void checkAuthorization(@NonNull FragmentManager fragmentManager,
                            @NonNull ResultListener resultListener);
}
