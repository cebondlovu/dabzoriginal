package com.softcrypt.deepkeysmusic.tools.exceptions;

import io.reactivex.annotations.NonNull;

public class RxFirebaseDataCastException extends Exception {

    public RxFirebaseDataCastException() {
    }

    public RxFirebaseDataCastException(@NonNull String detailMessage) {
        super(detailMessage);
    }

    public RxFirebaseDataCastException(@NonNull String detailMessage, @NonNull Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RxFirebaseDataCastException(@NonNull Throwable throwable) {
        super(throwable);
    }
}
