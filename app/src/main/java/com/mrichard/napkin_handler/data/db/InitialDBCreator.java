package com.mrichard.napkin_handler.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class
    InitialDBCreator
    extends
    SQLiteOpenHelper
{

    protected static int INITIAL_DB_VERSION = 1;

    /**
     * I create az initial DB, because I want to run migrations even when I start with a new database.
     * Room won't use migrations if on the initial creation of the database, so I create one with version 1.
     * The initial version is 1 because I can't create version 0.
     *
     * There are two cases here.
     * - If we already had a database then we cannot create one, so it will end with an SQLiteException.
     *   In this case Room will call migrations so it is OK to do like this.
     * - The other option that it is an initial creation of the database.
     *   In this case we will create a new database in the constructor of the InitialDBCreator class.
     */
    public InitialDBCreator(
            Context context
            , String dBName
    )
    {
        super(
                context
                , dBName
                , null
                , INITIAL_DB_VERSION
        );

        {
            // Creating the initial database if it is possible.
            try
            {
                SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            } catch (
                    Exception e
            )
            {
                // Expect an Exception like this:
                // android.database.sqlite.SQLiteException: Can't downgrade database from version @Database::version to 1
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(
            SQLiteDatabase db
    )
    {
        Log.i("DBHandler", "Creating DB");
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db
            , int oldVersion
            , int newVersion
    )
    {
        // We upgrade it.
        Log.i("DBHandler::Upgrade", "From " + oldVersion + " to " + newVersion);
    }

}
