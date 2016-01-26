package in.infiniumglobal.infirms.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;

public class CustomerSearchActivity extends AppCompatActivity {

    EditText edtTinNumber, edtReceiptNumber, edtBusinessName, edtCustomerName;
    Button btnSearch;
    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = DatabaseHandler.getInstance(CustomerSearchActivity.this);
        edtTinNumber = (EditText) findViewById(R.id.edtTinNumber);
        edtReceiptNumber = (EditText) findViewById(R.id.edtReceiptNumber);
        edtBusinessName = (EditText) findViewById(R.id.edtBusinessName);
        edtCustomerName = (EditText) findViewById(R.id.edtCustomerName);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTinNumber.getText().toString().trim().length() > 0 || edtReceiptNumber.getText().toString().trim().length() > 0
                        || edtBusinessName.getText().toString().trim().length() > 0 || edtCustomerName.getText().toString().trim().length() > 0) {
                    Cursor customerData = dbHandler.searchCustomer(edtTinNumber.getText().toString().trim(), edtReceiptNumber.getText().toString().trim(), edtBusinessName.getText().toString().trim(), edtCustomerName.getText().toString().trim());
                    System.out.println("customer size:" + customerData.getCount());
                    if (customerData.getCount() == 0) {
                        Toast.makeText(CustomerSearchActivity.this, "Customer not found.", Toast.LENGTH_LONG).show();
                    } else {
                        AppConfig.customerData = customerData;
                    }
                } else {
                    Toast.makeText(CustomerSearchActivity.this, "Please enter values.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(CustomerSearchActivity.this, RevenueSelectionActivity.class));
                finish();
                break;
        }
        return true;
    }
}