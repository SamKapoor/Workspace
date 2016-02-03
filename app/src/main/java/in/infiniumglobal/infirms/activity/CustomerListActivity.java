package in.infiniumglobal.infirms.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.adapter.SearchAdapter;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;

/**
 * Created by Hiral on 1/26/2016.
 */
public class CustomerListActivity extends BaseActivity {

    private ListView lvCustomers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerlist);

        setContext(this);
        lvCustomers = (ListView) findViewById(R.id.customerlist_lv_customers);

        SearchAdapter searchAdapter = new SearchAdapter(this, AppConfig.customerData, 0);
        lvCustomers.setAdapter(searchAdapter);

        lvCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
/*                Cursor cursor1 = ((SearchAdapter) adapterView.getAdapter()).getCursor();
                cursor1.moveToPosition(position);*/
                AppConfig.customerData.moveToPosition(position);
                AppConfig.customerID = AppConfig.customerData.getInt(AppConfig.customerData.getColumnIndex(DatabaseHandler.KEY_RCUSTOMERID));
                startActivity(new Intent(CustomerListActivity.this, CustomerManagementActivity.class));

            }
        });
    }
}