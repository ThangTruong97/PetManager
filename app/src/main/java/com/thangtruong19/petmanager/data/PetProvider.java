package com.thangtruong19.petmanager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by User on 18/07/2018.
 */

public class PetProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetDbHelper mDbHelper;
    /**
     * URI matcher code for the content URI for the pets table
     */
    private static final int PETS = 100;
    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return false;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TODO: Perform database query on pets table
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;

    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(PetContract.PetEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_GENDER);
        if (gender == null || !isGenderValid(gender)) {
            throw new IllegalArgumentException("Pet requires a valid gender");
        }

        Integer weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long newRowID = database.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues);

        if (newRowID == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, newRowID);
    }

    private boolean isGenderValid(int gender) {
        if (gender == PetContract.PetEntry.GENDER_FEMALE || gender == PetContract.PetEntry.GENDER_MALE || gender == PetContract.PetEntry.GENDER_UNKNOWN) {
            return true;
        }
        return false;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                rowDeleted=database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                if(rowDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowDeleted;
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowDeleted=database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                if(rowDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    private int updatePet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(PetContract.PetEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(PetContract.PetEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (contentValues.containsKey(PetContract.PetEntry.COLUMN_GENDER)) {
            Integer gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_GENDER);
            if (gender == null || !isGenderValid(gender)) {
                throw new IllegalArgumentException("Pet requires a valid gender");
            }
        }

        if (contentValues.containsKey(PetContract.PetEntry.COLUMN_WEIGHT)) {
            Integer weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }
        SQLiteDatabase database=mDbHelper.getWritableDatabase();
        int rowUpdated=database.update(PetContract.PetEntry.TABLE_NAME,contentValues,selection,selectionArgs);
        if(rowUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowUpdated;
    }
}
