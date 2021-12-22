package com.softcrypt.deepkeysmusic.tools;

import android.net.Uri;

public interface OnFileCreateResult {
    /**
     * @param created  whether file creation is success or failure
     * @param filePath filepath on disk. null in case of failure
     * @param error    in case file creation is failed . it would represent the cause
     * @param Uri      Uri to the newly created file. null in case of failure
     */
    void onFileCreateResult(boolean created, String filePath, String error, Uri Uri);
}
