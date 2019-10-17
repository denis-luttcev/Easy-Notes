package ru.z8.louttsev.easynotes.security;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public interface Protector {
    boolean isProtectionEnabled() throws Exception;
    void disableProtection();
    boolean checkProtection(@NonNull char[] protectionCode);
    void enableProtection(@NonNull FrameLayout protectionLayout);
}
