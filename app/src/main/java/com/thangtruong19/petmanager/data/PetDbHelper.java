package com.thangtruong19.petmanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 17/07/2018.
 */


public class PetDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="shelter.db";

    private static final String SQL_CREATE_ENTRIES=
            "CREATE TABLE "+ PetContract.PetEntry.TABLE_NAME+"("
            + PetContract.PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PetContract.PetEntry.COLUMN_NAME+ " TEXT, "
            + PetContract.PetEntry.COLUMN_BREED+ " TEXT, "
            + PetContract.PetEntry.COLUMN_GENDER+ " INTEGER, "
            + PetContract.PetEntry.COLUMN_WEIGHT+ " INTEGER );";

    private static final String SQL_DELETE_ENTRY=
            "DROP TABLE IF EXISTS"+ PetContract.PetEntry.TABLE_NAME;

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
             sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
             sqLiteDatabase.execSQL(SQL_DELETE_ENTRY);
             onCreate(sqLiteDatabase);
    }
}
