package in.infiniumglobal.infirms.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;
import in.infiniumglobal.infirms.utils.Common;

public class CustomerAddActivity extends BaseActivity {

    EditText edtTinNumber, edtBusinessName, edtCustomerName;
    Button btnAddUser;
    private DatabaseHandler dbHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_add);

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
                    contentValues.put(DatabaseHandler.KEY_TINNO, edtTinNumber.getText().toString().trim());
                    contentValues.put(DatabaseHandler.KEY_BUSINESSNAME, edtBusinessName.getText().toString().trim());
                    contentValues.put(DatabaseHandler.KEY_OWNERNAME, edtCustomerName.getText().toString().trim());
                    contentValues.put(DatabaseHandler.KEY_AREAID, AppConfig.areaID);
                    contentValues.put(DatabaseHandler.KEY_LOCATIONID, AppConfig.locationID);
                    contentValues.put(DatabaseHandler.KEY_REVENUETYPEID, AppConfig.revenueID);
                    contentValues.put(DatabaseHandler.KEY_CREATEDBY, Common.getStringPrefrences(CustomerAddActivity.this, getString(R.string.pref_userId), getString(R.string.app_name)));
                    contentValues.put(DatabaseHandler.KEY_CREATEDDATE, Common.getCurrentDate("yyyy-MM-dd hh:mm:ss"));
                    long res = dbHandler.addData(dbHandler.TABLE_TBLR_RevenueCustomer, contentValues);
                    contentValues = new ContentValues();
                    String customerId = AppConfig.DeviceCode + Common.getCurrentDate("MMyy") + res;
                    contentValues.put(DatabaseHandler.KEY_RCUSTOMERID, customerId);
                    dbHandler.updateCustomer(res + "", contentValues);

                    if (res != 0) {
//                        startActivity(new Intent(CustomerAddActivity.this, CustomerManagementActivity.class));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}