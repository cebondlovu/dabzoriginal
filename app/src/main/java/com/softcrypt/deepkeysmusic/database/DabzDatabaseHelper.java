package com.softcrypt.deepkeysmusic.database;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.common.DataPaths;
import com.softcrypt.deepkeysmusic.model.PostLocal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.internal.IOException;

public class DabzDatabaseHelper {

    private final File $REALM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    private static final String EXPORT_NAME = DataPaths.$BASE_NAME;
    private static final String IMPORT_NAME = DataPaths.$BASE_NAME;
    private final BaseApplication context;
    private final Realm realm;
    private static boolean resultPostSuccess;

    public DabzDatabaseHelper(BaseApplication context, Realm realm) {
        this.context = context;
        this.realm = realm;
    }

    public int getNextPendingPostKey() {
        if (realm.where(PostLocal.class).count() > 0)
            return Objects.requireNonNull(realm.where(PostLocal.class).max("id")).intValue() + 1;
        else
            return 0;
    }

    public RealmResults<PostLocal> getPendingPosts() {
        return realm.where(PostLocal.class).findAll();
    }

    public boolean insertPendingPost(final PostLocal post) {
        PostLocal modal = realm.where(PostLocal.class)
                .equalTo("postId", post.getPostId())
                .findFirst();

        if (modal == null) {
            realm.beginTransaction();
            PostLocal postLocal = realm.createObject(PostLocal.class, getNextPendingPostKey());
            postLocal.setPostId(post.getPostId());
            postLocal.setAuthorId(post.getAuthorId());
            postLocal.setCaption(post.getCaption());
            postLocal.setFileName(post.getFileName());
            postLocal.setUrl(post.getUrl());
            postLocal.setType(post.getType());
            postLocal.setDateTime(post.getDateTime());
            realm.commitTransaction();
            resultPostSuccess = true;
        }
        realm.close();
        return resultPostSuccess;
    }

    public boolean deletePendingPost(String postId) {
        realm.beginTransaction();
        RealmResults<PostLocal> modal = realm.where(PostLocal.class)
                .equalTo("postId", postId)
                .findAll();
        assert modal != null;
        boolean result = modal.deleteAllFromRealm();
        realm.commitTransaction();

        return result;
    }

    public void backup() {
        File exportRealmFile;
        exportRealmFile = new File(EXPORT_NAME, EXPORT_NAME);
        exportRealmFile.delete();
        realm.writeCopyTo(exportRealmFile);
        String msg = "File exported to Path: " + $REALM_PATH + "/" + EXPORT_NAME;
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    public void restore(String restoreFilePath){
        copyBundledRealmFile(restoreFilePath, IMPORT_NAME);
        Toast.makeText(context,"Done",Toast.LENGTH_LONG).show();


    }

    private String copyBundledRealmFile(String oldFilePath, String outFileName) {
        try {
            File file = new File(context.getApplicationContext().getFilesDir(), outFileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(oldFilePath);

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException | java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
