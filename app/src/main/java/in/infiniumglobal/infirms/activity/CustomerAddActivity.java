package in.infiniumglobal.infirms.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;

public class CustomerAddActivity extends BaseActivity {

    EditText edtTinNumber, edtBusinessName, edtCustomerName;
    Button btnAddUser;
    private DatabaseHandler dbHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);

        setContext(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = DatabaseHandler.getInstance(CustomerAddActivity.this);
        edtTinNumber = (EditText) findViewById(R.id.edtTinNumber);
        edtBusinessName = (EditText) findViewById(R.id.edtBusinessName);
        edtCustomerName = (EditText) findViewById(R.id.edtCustomerName);
        btnAddUser = (Button) findViewById(R.id.btnAddUser);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTinNumber.getText().toString().trim().length() > 0
                        || edtBusinessName.getText().toString().trim().length() > 0
                        || edtCustomerName.getText().toString().trim().length() > 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseHandler.KEY_USERID, edtTinNumber.getText().toString().trim());
                    contentValues.put(DatabaseHandler.KEY_BUSINESSNAME, edtBusinessName.getText().toString().trim());
                    contentValues.put(DatabaseHandler.KEY_CUSTOMERNAME, edtCustomerName.getText().toString().trim());
                    contentValues.put(DatabaseHandler.KEY_AREAID, AppConfig.areaID);
                    contentValues.put(DatabaseHandler.KEY_LOCATIONID, AppConfig.locationID);
                    contentValues.put(DatabaseHandler.KEY_REVENUETYPEID, AppConfig.revenueID);
                    long res = dbHandler.addData(dbHandler.TABLE_TBLR_RevenueCustomer, contentValues);

                    if (res != 0) {
                        startActivity(new Intent(CustomerAddActivity.this, CustomerManagementActivity.class));
                        finish();
                    } else {
                        Toast.makeText(CustomerAddActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (edtTinNumber.getText().toString().trim().length() == 0) {
                        Toast.makeText(CustomerAddActivity.this, "Please enter TIN number.", Toast.LENGTH_LONG).show();
                    } else if (edtBusinessName.getText().toString().trim().length() == 0) {
                        Toast.makeText(CustomerAddActivity.this, "Please enter business name.", Toast.LENGTH_LONG).show();
                    } else if (edtCustomerName.getText().toString().trim().length() == 0) {
                        Toast.makeText(CustomerAddActivity.this, "Please enter customer name.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

}