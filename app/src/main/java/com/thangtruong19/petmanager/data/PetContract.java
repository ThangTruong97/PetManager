package com.thangtruong19.petmanager.data;

import android.provider.BaseColumns;

/**
 * Created by User on 17/07/2018.
 */

public final class PetContract {
    private PetContract(){}

    public static class PetEntry implements BaseColumns{
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
