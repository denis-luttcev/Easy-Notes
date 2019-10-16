package ru.z8.louttsev.easynotes.security;

import androidx.annotation.NonNull;

public interface KeyKeeper {
    boolean isProtectionDisabled() throws Exception;
    void disableProtection();
    boolean checkProtection(@NonNull char[] protectionCode) throws Exception;
    void enableProtection(@NonNull char[] protectionCode) throws Exception;
}
