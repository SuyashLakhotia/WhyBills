package com.suyashlakhotia.WhyBills;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.app.ListActivity;


public class ExpenseActivity extends ListActivity {

    private ExpensesDB dbHelper;
    public static int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        dbHelper = new ExpensesDB(this);
        dbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }


    private void fillData() {
        Cursor remindersCursor = dbHelper.fetchAllExpenses();
        startManagingCursor(remindersCursor);

        String[] from = new String[]{ExpensesDB.KEY_STORE, ExpensesDB.KEY_TOTAL};

        int[] to = new int[]{R.id.text1, R.id.text2};

        SimpleCursorAdapter expenses =
                new SimpleCursorAdapter(this, R.layout.expense_row, remindersCursor, from, to);
        setListAdapter(expenses);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        a = (int) id;
        Intent i = new Intent(this, ReceiptsView.class);
        i.putExtra(ExpensesDB.KEY_EXPENSE, id);
        startActivityForResult(i, 1);
    }
}
