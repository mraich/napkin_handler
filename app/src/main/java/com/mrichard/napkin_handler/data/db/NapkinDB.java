package com.mrichard.napkin_handler.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mrichard.napkin_handler.data.model.picture.Picture;

@Database(
    entities = {
        Picture.class
    }
    , version = 2
    , exportSchema = false
)
public abstract class
    NapkinDB
    extends
    RoomDatabase
{

    protected static final String DB_NAME = "NapkinDB";

    private static NapkinDB INSTANCE;

    public static NapkinDB GetInstance(Context context)
    {
        if (
                INSTANCE == null
        )
        {
            {
                // The initial version is 1 because I can't create version 0.
                // In the initial Sophie version I used a migration between the 0 and 1 versions, but this was wrong move.
                // I had to put on the 1 to 2 migrations the fail safe version of that migration code which checks if
                // that first table already exists or not before creating.
                InitialDBCreator initialDBCreator = new InitialDBCreator(
                        context
                        , DB_NAME
                );
            }
            INSTANCE =
                Room.databaseBuilder(
                    context.getApplicationContext()
                    , NapkinDB.class
                    , DB_NAME
                    )
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return
                INSTANCE;
    }

    public static void destroyInstance()
    {
        INSTANCE = null;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2)
    {

        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.execSQL("PRAGMA foreign_keys = 0;");

            database.execSQL("CREATE TABLE picture (id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT NOT NULL, attributes TEXT NOT NULL, timestamp INTEGER NOT NULL DEFAULT (strftime('%s', 'now')));");

            database.execSQL("PRAGMA foreign_keys = 1;");
        }

    };

}
