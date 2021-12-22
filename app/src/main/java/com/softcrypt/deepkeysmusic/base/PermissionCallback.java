package com.softcrypt.deepkeysmusic.base;

public interface PermissionCallback {
    void onPermissionGranted(String[] grantedPermissions);
    void onPermissionDenied(String[] deniedPermissions);
    void onPermissionBlocked(String[] blockedPermissions);
}
