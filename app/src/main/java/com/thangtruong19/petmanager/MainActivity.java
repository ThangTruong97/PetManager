package com.thangtruong19.petmanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thangtruong19.petmanager.data.PetContract;
import com.thangtruong19.petmanager.data.PetDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    PetDbHelper mDbHelper = new PetDbHelper(this);
    private static final int URL_LOADER = 0;
    PetCursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setup FAB to open EditorActivity

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView listView=findViewById(R.id.list_view_pet);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

         mCursorAdapter=new PetCursorAdapter(this,null);
        listView.setAdapter(mCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,EditorActivity.class);

                Uri currentPetUri= ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI,position+1);

                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(URL_LOADER, null, this);

    }





    private void insertPet(){

        ContentValues values=new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_NAME,"Toto");
        values.put(PetContract.PetEntry.COLUMN_BREED,"Terrier");
        values.put(PetContract.PetEntry.COLUMN_GENDER,1);
        values.put(PetContract.PetEntry.COLUMN_WEIGHT,7);
        // Insert the new row
        Uri newRowUri=getContentResolver().insert(PetContract.PetEntry.CONTENT_URI,values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPet() {
        int rowsDeleted=getContentResolver().delete(PetContract.PetEntry.CONTENT_URI,null,null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection={
                BaseColumns._ID,
                PetContract.PetEntry.COLUMN_NAME,
                PetContract.PetEntry.COLUMN_BREED
        };

        return new CursorLoader(
                this,   // Parent activity context
                PetContract.PetEntry.CONTENT_URI,// Table to query
                projection,     // Projection to return
                null,            // No selection clause
                null,            // No selection arguments
                null             // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}

