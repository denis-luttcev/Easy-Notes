package ru.z8.louttsev.easynotes.security;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public interface Protector {
    interface ResultListener {
        void onProtectionResultSuccess();
        void onProtectionResultFailure();
    }

    void updateFragmentManager(@NonNull FragmentManager fragmentManager);

    boolean isProtectionNotConfigured();

    boolean isProtectionEnabled();

    void enableProtection(@NonNull ResultListener resultListener);

    void disableProtection();

    void checkAuthorization(@NonNull ResultListener resultListener,
                            boolean forcibly);
}
