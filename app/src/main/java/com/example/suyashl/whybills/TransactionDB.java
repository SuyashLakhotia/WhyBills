package com.example.suyashl.whybills;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class TransactionDB {


    private static final String DATABASE_NAME = "transactions";

    private static final int DATABASE_VERSION = 3;

    public static String DATABASE_TABLE = "TransactionTable";
    public static final String KEY_TIME = "timeStamp";
    public static final String KEY_STORE = "store";
    public static final String KEY_ITEM_NAME = "itemName";
    public static final String KEY_ITEM_PRICE = "Price";
    public static final String KEY_QTY = "qty";
    public static final String KEY_TOTPRICE = "totprice";
    public static final String KEY_TRANSACTION_ID = "transactionID";

    private static final String TAG = "TransactionDB";
    public DatabaseHelper mDbHelper;
    public SQLiteDatabase mDb;

    /**
     * Database creation SQL statement
     */

    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
                    + KEY_TIME + " text not null, "
                    + KEY_STORE + " text not null, "
                    + KEY_ITEM_NAME + " text not null, "
                    + KEY_ITEM_PRICE + " float(7,4) not null, "
                    + KEY_QTY + " integer not null, "
                    + KEY_TOTPRICE + " float(7,4) not null, "
                    + KEY_TRANSACTION_ID + " integer not null);";


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

    public void createTransaction(SQLiteDatabase db) {


        db.execSQL(DATABASE_CREATE);

    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public TransactionDB(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws android.database.SQLException if the database could be neither opened or created
     */
    public TransactionDB open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new reminder using the title, description and reminder date time provided.
     * If the reminder is successfully created return the new rowId
     * for that reminder, otherwise return a -1 to indicate failure.
     *
     * @param timestamp the time the item was bought
     * @return rowId or -1 if failed
     */
    public void AddItems(String timestamp, String store,String itemName, float price, int qty, int trID) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TIME, timestamp);
        initialValues.put(KEY_STORE, store);
        initialValues.put(KEY_ITEM_NAME, itemName);
        initialValues.put(KEY_ITEM_PRICE, price);
        initialValues.put(KEY_QTY, qty);
        initialValues.put(KEY_TOTPRICE, price*qty);
        initialValues.put(KEY_TRANSACTION_ID, trID);


        long n =  mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the reminder with the given rowId
     *
     *
     * @return true if deleted, false otherwise
     */

    public boolean deleteTransaction() {

        mDb.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        return true;
    }


    /**
     * Return a Cursor over the list of all reminders in the database
     *
     * @return Cursor over all reminders
     */
    public Cursor fetchTransaction() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ITEM_NAME,
                KEY_TIME, KEY_STORE, KEY_ITEM_PRICE, KEY_QTY, KEY_TOTPRICE, KEY_TRANSACTION_ID}, null, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the reminder that matches the given rowId
     *
     * @param itemName name of item to retrieve
     * @return Cursor positioned to matching reminder, if found
     * @throws SQLException if reminder could not be found/retrieved
     */
    public Cursor fetchItem(String itemName) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ITEM_NAME,
                                KEY_TIME, KEY_STORE, KEY_ITEM_PRICE, KEY_QTY, KEY_TOTPRICE, KEY_TRANSACTION_ID},  KEY_ITEM_NAME + "=" + itemName, null,
                        null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

}




