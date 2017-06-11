package hu.gearxpert.habittracker.data;

import android.provider.BaseColumns;

/**
 * Created by melinda.kostenszki on 2017.06.11..
 */

public class HabitContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private HabitContract() {}

    //Inner class that defines constant values for the habits database table.
    public static final class HabitEntry implements BaseColumns {

        public static final String TABLE_NAME = "habits";

        //Unique ID number for the habit (only for use in the database table)
        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_HABIT_NAME = "name";
        public static final String COLUMN_HABIT_PLACE = "place";
        public static final String COLUMN_HABIT_TIME_HOUR = "hour";
        public static final String COLUMN_HABIT_TIME_MINUTE = "minute";

    }
}
