package com.example.suyashl.whybills;

import android.app.ListActivity;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.app.ListActivity;


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

        int[] to = new int[]{R.id.text2, R.id.text3};

        String[] from = new String[]{TransactionDB.KEY_ITEM_NAME, TransactionDB.KEY_TOTPRICE};

        SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this,R.layout.receipt_row, cursor, from, to);
        setListAdapter(adapter2);
    }
}
