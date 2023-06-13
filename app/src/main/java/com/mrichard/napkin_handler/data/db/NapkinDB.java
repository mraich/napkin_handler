package com.mrichard.napkin_handler.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mrichard.napkin_handler.data.model.category.Category;
import com.mrichard.napkin_handler.data.model.category.ICategoryDao;
import com.mrichard.napkin_handler.data.model.picture.IPictureDao;
import com.mrichard.napkin_handler.data.model.picture.Picture;

@Database(
    entities = {
        Picture.class,
        Category.class
    }
    , version = 7
    , exportSchema = false
)
public abstract class
    NapkinDB
    extends
    RoomDatabase
{

    protected static final String DB_NAME = "NapkinDB";

    public abstract ICategoryDao categoryDao();

    public abstract IPictureDao pictureDao();

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
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_6)
                    .addMigrations(MIGRATION_6_7)
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

    public static final Migration MIGRATION_2_3 = new Migration(2, 3)
    {

        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.execSQL("PRAGMA foreign_keys = 0;");

            database.execSQL("ALTER TABLE picture ADD count INTEGER NOT NULL DEFAULT 1;");

            database.execSQL("PRAGMA foreign_keys = 1;");
        }

    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4)
    {

        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.execSQL("PRAGMA foreign_keys = 0;");

            database.execSQL("ALTER TABLE picture ADD is_new INTEGER NOT NULL DEFAULT 1;");

            database.execSQL("PRAGMA foreign_keys = 1;");
        }

    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5)
    {

        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.execSQL("PRAGMA foreign_keys = 0;");

            database.execSQL("CREATE TABLE category (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, timestamp INTEGER NOT NULL DEFAULT (strftime('%s', 'now')));");

            database.execSQL("PRAGMA foreign_keys = 1;");
        }

    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6)
    {

        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.execSQL("PRAGMA foreign_keys = 0;");

            // Creating built in categories.
            database.execSQL("INSERT INTO category(name) VALUES ('Coca-Cola')");
            database.execSQL("INSERT INTO category(name) VALUES ('Pöttyös')");
            database.execSQL("INSERT INTO category(name) VALUES ('Formára vágott')");
            database.execSQL("INSERT INTO category(name) VALUES ('Poháralátét')");
            database.execSQL("INSERT INTO category(name) VALUES ('Fagyis')");
            database.execSQL("INSERT INTO category(name) VALUES ('Egy színű')");

            database.execSQL("PRAGMA foreign_keys = 1;");
        }

    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7)
    {

        @Override
        public void migrate(SupportSQLiteDatabase database)
        {
            database.execSQL("PRAGMA foreign_keys = 0;");

            // Basic category means it's built in the ImageRecognizer.
            // By default every category is not a basic category when the user adds them.
            database.execSQL("ALTER TABLE category ADD basic INTEGER NOT NULL DEFAULT 0;");

            database.execSQL("PRAGMA foreign_keys = 1;");
        }

    };

}
