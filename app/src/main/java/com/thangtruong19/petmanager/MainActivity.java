package com.thangtruong19.petmanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.thangtruong19.petmanager.data.PetContract;
import com.thangtruong19.petmanager.data.PetDbHelper;

public class MainActivity extends AppCompatActivity {

    PetDbHelper mDbHelper = new PetDbHelper(this);
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
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection={
                BaseColumns._ID,
                PetContract.PetEntry.COLUMN_NAME,
                PetContract.PetEntry.COLUMN_BREED
        };

        Cursor cursor=getContentResolver().query(PetContract.PetEntry.CONTENT_URI,projection,null,null,null);
        // Display the number of rows in the Cursor (which reflects the number of rows in the
        // pets table in the database).
        ListView listView=findViewById(R.id.list_view_pet);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        PetCursorAdapter adapter=new PetCursorAdapter(this,cursor);
        listView.setAdapter(adapter);
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
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    }

