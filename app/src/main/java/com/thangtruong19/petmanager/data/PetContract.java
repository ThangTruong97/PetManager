package com.thangtruong19.petmanager.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by User on 17/07/2018.
 */

public final class PetContract {
    private PetContract(){}

    private static final String CONTENT_AUTHORITY="com.thangtruong19.petmanager" ;
    private static final Uri BASE_CONTENT_URI= Uri.parse("context://"+CONTENT_AUTHORITY);
    private static final String PATH_PETS="pets";

    public static class PetEntry implements BaseColumns{
        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);
        public static final String TABLE_NAME="pets";
        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_NAME="name";
        public static final String COLUMN_BREED="breed";
        public static final String COLUMN_GENDER="gender";
        public static final String COLUMN_WEIGHT="weight";

        public static final int GENDER_UNKNOWN=0;
        public static final int GENDER_MALE=1;
        public static final int GENDER_FEMALE=2;

    }
}
