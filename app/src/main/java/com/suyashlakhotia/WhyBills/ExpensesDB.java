package com.suyashlakhotia.WhyBills;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ExpensesDB {
    private static final String DATABASE_NAME = "ExpensesDB";
    private static final String DATABASE_TABLE = "Expenses";
    private static final int DATABASE_VERSION = 6;

    public static final String KEY_EXPENSE = "_id";
    public static final String KEY_TIME = "timeStamp";
    public static final String KEY_STORE = "store";
    public static final String KEY_TOTAL = "total";

    private static final String TAG = "ExpensesDB";
    private DatabaseHelper mDbHelper2;
    private SQLiteDatabase mDb2;

    /**
     * Database creation SQL statement.
     */
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
                    + KEY_EXPENSE + " integer primary key, "
                    + KEY_TIME + " text not null, "
                    + KEY_STORE + " text not null, "
                    + KEY_TOTAL + " float(7,4) not null);";


    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }


    /**
     * Constructor - takes the context to allow the database to be
     * opened/created.
     */
    public ExpensesDB(Context ctx) {
        this.mCtx = ctx;
    }


    /**
     * Opens the database. If it cannot be opened, tries to create a new
     * instance of the database. If it cannot be created, throws an exception to
     * signal the failure.
     */
    public ExpensesDB open() throws SQLException {
        mDbHelper2 = new DatabaseHelper(mCtx);
        mDb2 = mDbHelper2.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper2.close();
    }


    /**
     * Creates a new record.
     */
    public long enterTransaction(String timestamp, String store, float total) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TIME, timestamp);
        initialValues.put(KEY_STORE, store);
        initialValues.put(KEY_TOTAL, total);

        return mDb2.insert(DATABASE_TABLE, null, initialValues);
    }


    /**
     * Deletes the entire expenses table.
     */
    public boolean deleteAllExpenses() {
        mDb2.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        return true;
    }


    /**
     * Returns a Cursor over the list of all the records in the database.
     */
    public Cursor fetchAllExpenses() {
        return mDb2.query(DATABASE_TABLE, new String[] {KEY_EXPENSE,
                KEY_TIME, KEY_STORE, KEY_TOTAL}, null, null, null, null, null, null);
    }


    /**
     * Returns the total amount spent in the current month.
     */
    public double monthlyExpenses(int month, int year)
    {
        String query = "Select sum(total) from " + DATABASE_TABLE + " where timestamp like \'%" + month + "" + year + "%\';";

        DatabaseHelper mDbHelper2;
        mDbHelper2 = new DatabaseHelper(mCtx);
        SQLiteDatabase mDb2 = mDbHelper2.getWritableDatabase();
        Cursor cursor = mDb2.rawQuery(query, null);
        cursor.moveToFirst();
        double sum = cursor.getDouble(0);
        cursor.close();
        return sum;
    }
}
