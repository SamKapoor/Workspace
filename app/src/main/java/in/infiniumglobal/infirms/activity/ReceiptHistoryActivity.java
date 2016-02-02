package in.infiniumglobal.infirms.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.adapter.HistoryAdapter;
import in.infiniumglobal.infirms.adapter.SearchAdapter;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;
import in.infiniumglobal.infirms.utils.Common;

public class ReceiptHistoryActivity extends AppCompatActivity {

    private ListView lvHistory;
    private DatabaseHandler dbHandler;
    private TextView tvTotalAmount;
    private TextView tvPaidAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_history);

        getSupportActionBar().setTitle(AppConfig.revenueItem);

        tvTotalAmount = (TextView) findViewById(R.id.history_tv_totalAmt);
        tvPaidAmount = (TextView) findViewById(R.id.history_tv_paidAmt);
        lvHistory = (ListView) findViewById(R.id.history_lv_receipt);
        dbHandler = DatabaseHandler.getInstance(this);
        Cursor totalAmountCursor = dbHandler.getRevenueReceiptTotalAmount(AppConfig.revenueID);
        if (totalAmountCursor != null && totalAmountCursor.getCount() > 0) {
            totalAmountCursor.moveToFirst();
            String total[] = totalAmountCursor.getString(totalAmountCursor.getColumnIndex("Total")).split("-");
            tvTotalAmount.setText("Total Amt \n" + total[0]);
            tvPaidAmount.setText("Paid Amt \n" + total[1]);
        }
        Cursor historyCursor = dbHandler.getRevenueReceipt(AppConfig.revenueID);

        HistoryAdapter historyAdapter = new HistoryAdapter(this, historyCursor, 0);
        lvHistory.setAdapter(historyAdapter);

        /*lvCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
*//*                Cursor cursor1 = ((SearchAdapter) adapterView.getAdapter()).getCursor();
                cursor1.moveToPosition(position);*//*
                AppConfig.customerData.moveToPosition(position);
                AppConfig.customerID = AppConfig.customerData.getInt(AppConfig.customerData.getColumnIndex(DatabaseHandler.KEY_RCUSTOMERID));
                startActivity(new Intent(CustomerListActivity.this, CustomerManagementActivity.class));

            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_receipt_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_instant_pay) {
            finish();
        } else if (id == R.id.action_logout) {
            Common.removeAllPrefrences(this, getString(R.string.app_name));
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
