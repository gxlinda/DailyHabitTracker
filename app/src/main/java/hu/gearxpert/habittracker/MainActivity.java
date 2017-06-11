package hu.gearxpert.habittracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import hu.gearxpert.habittracker.data.HabitContract.HabitEntry;
import hu.gearxpert.habittracker.data.HabitDbHelper;

public class MainActivity extends AppCompatActivity {

    private HabitDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new HabitDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Cursor cursor = readHabitsDatabase();
        displayDatabaseInfo(cursor);
    }

    private Cursor readHabitsDatabase() {
        // Create and/or open a database to read from it, returns a Cursor
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                HabitEntry._ID,
                HabitEntry.COLUMN_HABIT_NAME,
                HabitEntry.COLUMN_HABIT_PLACE,
                HabitEntry.COLUMN_HABIT_TIME_HOUR,
                HabitEntry.COLUMN_HABIT_TIME_MINUTE
        };

        Cursor cursor = db.query(HabitEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        return cursor;
    }

    private void displayDatabaseInfo(Cursor cursor) {
        // Display data we get from Cursor object
        TextView displayView = (TextView) findViewById(R.id.text_view_habit);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The habits table contains <number of rows in Cursor> habits.
            // _id - name - place - hour:minute
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The habits table contains " + cursor.getCount() + " habits.\n\n");
            displayView.append(HabitEntry._ID + " - " +
                    HabitEntry.COLUMN_HABIT_NAME + " - " +
                    HabitEntry.COLUMN_HABIT_PLACE + " - " +
                    HabitEntry.COLUMN_HABIT_TIME_HOUR + ":" +
                    HabitEntry.COLUMN_HABIT_TIME_MINUTE + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(HabitEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_NAME);
            int placeColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_PLACE);
            int hourColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_TIME_HOUR);
            int minuteColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_HABIT_TIME_MINUTE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentPlace = cursor.getString(placeColumnIndex);
                int currentHour = cursor.getInt(hourColumnIndex);
                int currentMinute = cursor.getInt(minuteColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPlace + " - " +
                        pad(currentHour) + ":" +
                        pad(currentMinute));
            }

        } finally {
            // Close the cursor when we're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded habit data into the database. For debugging purposes only.
     */
    private void insertHabit() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Learning Android's attributes are the values.
        ContentValues values = new ContentValues();
        values.put(HabitEntry.COLUMN_HABIT_NAME, "Learning Android");
        values.put(HabitEntry.COLUMN_HABIT_PLACE, "At home");
        values.put(HabitEntry.COLUMN_HABIT_TIME_HOUR, 22);
        values.put(HabitEntry.COLUMN_HABIT_TIME_MINUTE, 0);

        long newRowId = db.insert(HabitEntry.TABLE_NAME, null, values);

        Toast.makeText(this, "Dummy habit with ID " + newRowId + " added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Add dummy habit" menu option
            case R.id.action_insert_dummy_data:
                insertHabit();
                Cursor cursor = readHabitsDatabase();
                displayDatabaseInfo(cursor);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Adds a leading 0 to time if the value is smaller than 10
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}
