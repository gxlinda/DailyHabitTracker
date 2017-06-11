package hu.gearxpert.habittracker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import hu.gearxpert.habittracker.data.HabitContract.HabitEntry;
import hu.gearxpert.habittracker.data.HabitDbHelper;

public class EditorActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mPlaceEditText;
    private Button buttonAddTime;
    private TextView defaultTime;
    private int hour;
    private int minute;
    static final int TIME_DIALOG_ID = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_habit_name);
        mPlaceEditText = (EditText) findViewById(R.id.edit_habit_place);

        setDefaultTime();
        addListenerOnButton();
    }

    public void setDefaultTime() {

        defaultTime = (TextView) findViewById(R.id.default_time);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        // set current time into textview
        defaultTime.setText(
                new StringBuilder().append(pad(hour))
                        .append(":").append(pad(minute)));
    }

    //Get user input from edit fields and save new pet into database
    private void insertHabit() {
        String nameString = mNameEditText.getText().toString().trim();
        String placeString = mPlaceEditText.getText().toString().trim();
        int hourInt = hour;
        int minuteInt = minute;

        HabitDbHelper mDbHelper = new HabitDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(HabitEntry.COLUMN_HABIT_NAME, nameString);
        values.put(HabitEntry.COLUMN_HABIT_PLACE, placeString);
        values.put(HabitEntry.COLUMN_HABIT_TIME_HOUR, hourInt);
        values.put(HabitEntry.COLUMN_HABIT_TIME_MINUTE, minuteInt);

        long newRowId = db.insert(HabitEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error saving habit", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Habit saved with ID " + newRowId, Toast.LENGTH_SHORT).show();
        }

    }

    public void addListenerOnButton() {

        buttonAddTime = (Button) findViewById(R.id.button_habit_time);

        buttonAddTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(TIME_DIALOG_ID);

            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                // set time picker as current time
                return new TimePickerDialog(this, timePickerListener, hour, minute, true);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;

                    // set current time into textview
                    defaultTime.setText(new StringBuilder().append(pad(hour))
                            .append(":").append(pad(minute)));
                }
            };

    //adds a leading 0 to time if the value is smaller than 10
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save habit to database if it has a name
                if (mNameEditText.length() > 0) {
                    insertHabit();
                    finish();
                } else {
                    Toast.makeText(this, "Give your habit a name!", Toast.LENGTH_SHORT).show();
                }
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (MainActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
