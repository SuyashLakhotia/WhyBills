package com.example.suyashl.whybills;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.app.ListActivity;


public class ExpenseActivity extends ListActivity {

    private ExpensesDB dbHelper;

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

        String[] from = new String[]{ExpensesDB.KEY_STORE};

        int[] to = new int[]{R.id.text1};

        SimpleCursorAdapter expenses =
                new SimpleCursorAdapter(this, R.layout.expense_row, remindersCursor, from, to);
        setListAdapter(expenses);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ReceiptsView.class);
        i.putExtra(ExpensesDB.KEY_EXPENSE, id);
        startActivityForResult(i, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
