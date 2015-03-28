package com.example.suyashl.whybills;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends Activity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    private TransactionDB TransDBHelper;
    private ExpensesDB ExpenseDBHelper;
    private Calendar mCal;
    private ExpensesDB expDBHandler = new ExpensesDB(this);

    private Button button;
    private TextView monthly_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TransDBHelper = new TransactionDB(this);
        ExpenseDBHelper = new ExpensesDB(this);

        button = (Button) findViewById(R.id.ExpenseTracker);
        button.setOnClickListener(ExpenseTrackerListener);

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        double sum = expDBHandler.monthlyExpenses(month, year);
        monthly_total = (TextView)findViewById(R.id.TotaltextView);
        monthly_total.setText("" + sum);

        mHandler.postDelayed(mUpdateUITimerTask, 1000);
    }

    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            System.out.println("Total Amount Update.");
            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            double sum = expDBHandler.monthlyExpenses(month, year);
            monthly_total = (TextView)findViewById(R.id.TotaltextView);
            monthly_total.setText("" + sum);

            mHandler.postDelayed(this, 1000);
        }
    };
    private final Handler mHandler = new Handler();

    public void scanQR(View v) {
        try {
            // Start the scanning activity from the com.google.zxing.client.android.SCAN intent.
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            // On catch, show the download dialog.
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    // Displays an alert dialog in case of no QR Code Scanner.
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException e) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    // Processes the input from the QR Code and saves it into the database.
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                Toast toast = Toast.makeText(this, "Scan Complete & Bill Added.", Toast.LENGTH_LONG);
                toast.show();

                mCal = Calendar.getInstance();
                String timestamp = String.valueOf(mCal.get(Calendar.MONTH)) + String.valueOf(mCal.get(Calendar.YEAR));

                String[] records = contents.split("<>");
                String[] fields;
                float price;
                int q;
                float total = 0;

                ExpenseDBHelper.open();
                TransDBHelper.open();

                fields = records[0].split("\t");

                for(int c = 0; c < (records.length - 1); c++) {
                    fields = records[c].split("\t");

                    price = Float.parseFloat(fields[2]);
                    q = Integer.parseInt(fields[3]);

                    total = total + (price * q);
                }

                System.out.println("Timestamp = " + timestamp);
                System.out.println("Total = " + total);
                long table_name = ExpenseDBHelper.enterTransaction(timestamp, fields[0], total);


                for(int c = 0; c < (records.length - 1); c++){
                    fields = records[c].split("\t");

                    price = Float.parseFloat(fields[2]);
                    q = Integer.parseInt(fields[3]);

                    System.out.println(fields[0]);
                    System.out.println(fields[1]);
                    System.out.println(fields[2]);
                    System.out.println(fields[3]);

                    TransDBHelper.AddItems(timestamp, fields[0], fields[1], price, q, (int) table_name);
                }
            }
        }
    }

    private Button.OnClickListener ExpenseTrackerListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            expensetracker();
        }};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void expensetracker() {
        Intent i = new Intent(this, ExpenseActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}