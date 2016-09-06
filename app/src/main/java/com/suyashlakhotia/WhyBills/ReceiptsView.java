package com.suyashlakhotia.WhyBills;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;


public class ReceiptsView extends ListActivity {

    private TransactionDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts_view);

        dbHelper = new TransactionDB(this);
        dbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }

    private void fillData() {
        Cursor cursor = dbHelper.fetchItem(ExpenseActivity.a);
        startManagingCursor(cursor);

        int[] to = new int[]{R.id.text3, R.id.text4};

        String[] from = new String[]{TransactionDB.KEY_ITEM_NAME, TransactionDB.KEY_TOTPRICE};

        SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this,R.layout.receipt_row, cursor, from, to);
        setListAdapter(adapter2);
    }
}
