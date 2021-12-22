package com.softcrypt.deepkeysmusic.tools.exceptions;

import com.google.firebase.database.DatabaseError;

import io.reactivex.annotations.NonNull;

public class RxFirebaseDataException extends Exception {
    protected DatabaseError error;

    public RxFirebaseDataException(@NonNull DatabaseError error) {
        this.error = error;
    }

    public DatabaseError getError() {
        return error;
    }

    @Override
    public String toString() {
        return "RxFirebaseDataException{" +
                "error=" + error +
                '}';
    }
}
