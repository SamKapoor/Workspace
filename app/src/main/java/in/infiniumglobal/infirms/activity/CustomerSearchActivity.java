package in.infiniumglobal.infirms.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;
import in.infiniumglobal.infirms.utils.Common;

public class CustomerSearchActivity extends BaseActivity {

    EditText edtTinNumber, edtReceiptNumber, edtBusinessName, edtCustomerName;
    Button btnSearch, btnAddUser;
    private DatabaseHandler dbHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);

        setContext(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = DatabaseHandler.getInstance(CustomerSearchActivity.this);
        edtTinNumber = (EditText) findViewById(R.id.edtTinNumber);
        edtReceiptNumber = (EditText) findViewById(R.id.edtReceiptNumber);
        edtBusinessName = (EditText) findViewById(R.id.edtBusinessName);
        edtCustomerName = (EditText) findViewById(R.id.edtCustomerName);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnAddUser = (Button) findViewById(R.id.btnAddUser);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerSearchActivity.this, CustomerAddActivity.class));
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTinNumber.getText().toString().trim().length() > 0 || edtReceiptNumber.getText().toString().trim().length() > 0
                        || edtBusinessName.getText().toString().trim().length() > 0 || edtCustomerName.getText().toString().trim().length() > 0) {
                    Cursor customerData = dbHandler.searchCustomer(edtTinNumber.getText().toString().trim(), edtReceiptNumber.getText().toString().trim(), edtBusinessName.getText().toString().trim(), edtCustomerName.getText().toString().trim(), AppConfig.revenueID + "");
                    System.out.println("customer size:" + customerData.getCount());

                    if (customerData != null && customerData.getCount() > 0) {
                        AppConfig.customerData = customerData;
                        startActivity(new Intent(CustomerSearchActivity.this, CustomerListActivity.class));
                    } else {
                        Toast.makeText(CustomerSearchActivity.this, "Customer not found.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CustomerSearchActivity.this, "Please enter values.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}