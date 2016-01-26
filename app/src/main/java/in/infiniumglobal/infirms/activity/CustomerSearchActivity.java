package in.infiniumglobal.infirms.activity;

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

public class CustomerSearchActivity extends AppCompatActivity {

    EditText edtTinNumber, edtReceiptNumber, edtBusinessNumber, edtCustomerNumber;
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
        edtBusinessNumber = (EditText) findViewById(R.id.edtBusinessNumber);
        edtCustomerNumber = (EditText) findViewById(R.id.edtCustomerNumber);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTinNumber.getText().toString().trim().length() > 0 || edtReceiptNumber.getText().toString().trim().length() > 0
                        || edtBusinessNumber.getText().toString().trim().length() > 0 || edtCustomerNumber.getText().toString().trim().length() > 0) {
                    Cursor customerData = dbHandler.searchCustomer(edtTinNumber.getText().toString().trim(), edtReceiptNumber.getText().toString().trim(), edtBusinessNumber.getText().toString().trim(), edtCustomerNumber.getText().toString().trim());
                    System.out.println("customer size:" + customerData.getCount());
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
                finish();
                break;
        }
        return true;
    }
}