package com.softcrypt.deepkeysmusic.database;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class DabzDataBaseMigrations implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        final RealmSchema schema = realm.getSchema();
        if (oldVersion != newVersion) {
            final RealmObjectSchema post = schema.get("PostLocal");

            if(post != null) {
                post.renameField("filename", "fileName");
            }

            oldVersion++;

        }
    }
}

