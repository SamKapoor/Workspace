package in.infiniumglobal.infirms.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.client.MyClientGet;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.Common;

/**
 * Created by admin on 1/26/2016.
 */
public class BaseActivity extends AppCompatActivity {

    private MyClientGet myclientget;
    private Context context;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = BaseActivity.this;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_logout:
                Common.removeAllPrefrences(this, getString(R.string.app_name));
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.action_sync:
//                TODO sync
                if (Common.isNetworkAvailable(context)) {
                    dialog = new ProgressDialog(context);
                    dialog.setMessage("Please wait...");
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    getUsers();
                } else {
                    Toast.makeText(context, "No internet available.", Toast.LENGTH_LONG).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUsers() {
        myclientget = new MyClientGet(context, "", onUserSyncComplete);
        myclientget.execute(getResources().getString(R.string.api_master) + "user");
    }

    MyClientGet.OnGetCallComplete onUserSyncComplete = new MyClientGet.OnGetCallComplete() {
        @Override
        public void response(String result) {
            try {
                getRevenueType();
                JSONObject jobj = new JSONObject(result);
                String response_code = jobj.getString("result");
                if (response_code.equals("1")) {
                    JSONArray userArray = new JSONArray();
                    userArray = jobj.getJSONArray("detail");
                    JSONObject userJsonObject;
                    DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
                    ContentValues contentValues;

                    for (int i = 0; i < userArray.length(); i++) {
                        userJsonObject = userArray.getJSONObject(i);
                        contentValues = new ContentValues();
//                        contentValues.put(DatabaseHandler.KEY_ID, userJsonObject.optString(""));
                        contentValues.put(DatabaseHandler.KEY_USERID, userJsonObject.optString("UserID"));
                        contentValues.put(DatabaseHandler.KEY_USERNAME, userJsonObject.optString("UserName"));
                        contentValues.put(DatabaseHandler.KEY_PASSWORD, userJsonObject.optString("UserPassword"));
                        long response = dbHandler.addData(dbHandler.TABLE_TBLA_User, contentValues);
                        System.out.println("response user:" + response);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void getAreas() {
        myclientget = new MyClientGet(context, "", onAreaSyncComplete);
        myclientget.execute(getResources().getString(R.string.api_master) + "area");
    }

    MyClientGet.OnGetCallComplete onAreaSyncComplete = new MyClientGet.OnGetCallComplete() {
        @Override
        public void response(String result) {
            try {
                getLocations();
                JSONObject jobj = new JSONObject(result);
                String response_code = jobj.getString("result");
                if (response_code.equals("1")) {
                    JSONArray areaArray = new JSONArray();
                    areaArray = jobj.getJSONArray("detail");
                    DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
                    ContentValues contentValues;

                    for (int i = 0; i < areaArray.length(); i++) {
                        JSONObject areaJsonObject = areaArray.getJSONObject(i);
                        contentValues = new ContentValues();
//                        contentValues.put(DatabaseHandler.KEY_ID, areaJsonObject.optString(""));
                        contentValues.put(DatabaseHandler.KEY_AREAID, areaJsonObject.optString("AreaId"));
                        contentValues.put(DatabaseHandler.KEY_AREANAME, areaJsonObject.optString("AreaName"));
                        long response = dbHandler.addData(dbHandler.TABLE_TBLR_Area, contentValues);
                        System.out.println("response area:" + response);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void getLocations() {
        myclientget = new MyClientGet(context, "", onLocationSyncComplete);
        myclientget.execute(getResources().getString(R.string.api_master) + "location");
    }

    MyClientGet.OnGetCallComplete onLocationSyncComplete = new MyClientGet.OnGetCallComplete() {
        @Override
        public void response(String result) {
            try {
                getRevenueRate();
                JSONObject jobj = new JSONObject(result);
                String response_code = jobj.getString("result");
                if (response_code.equals("1")) {
                    JSONArray locationArray = new JSONArray();
                    locationArray = jobj.getJSONArray("detail");
                    DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
                    ContentValues contentValues;

                    for (int i = 0; i < locationArray.length(); i++) {
                        JSONObject locationJsonObject = locationArray.getJSONObject(i);
                        contentValues = new ContentValues();
//                        contentValues.put(DatabaseHandler.KEY_ID, locationJsonObject.optString(""));
                        contentValues.put(DatabaseHandler.KEY_LOCATIONID, locationJsonObject.optString("LocationId"));
                        contentValues.put(DatabaseHandler.KEY_AREAID, locationJsonObject.optString("AreaId"));
                        contentValues.put(DatabaseHandler.KEY_LOCATIONNAME, locationJsonObject.optString("LocationName"));
                        long response = dbHandler.addData(dbHandler.TABLE_TBLR_Location, contentValues);
                        System.out.println("response location:" + response);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void getRevenueType() {
        myclientget = new MyClientGet(context, "", onRevenueSyncComplete);
        myclientget.execute(getResources().getString(R.string.api_master) + "RevenueType");
    }

    MyClientGet.OnGetCallComplete onRevenueSyncComplete = new MyClientGet.OnGetCallComplete() {
        @Override
        public void response(String result) {
            try {
                getAreas();
                JSONObject jobj = new JSONObject(result);
                String response_code = jobj.getString("result");
                if (response_code.equals("1")) {
                    JSONArray revenueTypeArray = new JSONArray();
                    revenueTypeArray = jobj.getJSONArray("detail");
                    DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
                    ContentValues contentValues;

                    for (int i = 0; i < revenueTypeArray.length(); i++) {
                        JSONObject revenueJsonObject = revenueTypeArray.getJSONObject(i);
                        System.out.println("revenue: " + i + ":" + revenueJsonObject.optString("RevenueTypeId") + " : " +
                                revenueJsonObject.optString("RevenueName") + " : " + revenueJsonObject.optString("ReceiptType"));
                        contentValues = new ContentValues();
//                        contentValues.put(DatabaseHandler.KEY_ID, revenueJsonObject.optString(""));
                        contentValues.put(DatabaseHandler.KEY_REVENUETYPEID, revenueJsonObject.optString("RevenueTypeId"));
                        contentValues.put(DatabaseHandler.KEY_REVENUENAME, revenueJsonObject.optString("RevenueName"));
                        contentValues.put(DatabaseHandler.KEY_RECEIPTTYPE, revenueJsonObject.optString("ReceiptType"));
                        contentValues.put(DatabaseHandler.KEY_INSTANTPAY, revenueJsonObject.optString(""));
                        long response = dbHandler.addData(dbHandler.TABLE_TBLR_RevenueType, contentValues);
                        System.out.println("response revenuetype:" + response);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void getRevenueRate() {
        myclientget = new MyClientGet(context, "", onRevenueRateSyncComplete);
        myclientget.execute(getResources().getString(R.string.api_master) + "RevenueRate");
    }

    MyClientGet.OnGetCallComplete onRevenueRateSyncComplete = new MyClientGet.OnGetCallComplete() {
        @Override
        public void response(String result) {
            try {
                getRevenueCustomer();
                JSONObject jobj = new JSONObject(result);
                String response_code = jobj.getString("result");
                if (response_code.equals("1")) {
                    JSONArray revenueRateArray = new JSONArray();
                    revenueRateArray = jobj.getJSONArray("detail");
                    DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
                    ContentValues contentValues;

                    for (int i = 0; i < revenueRateArray.length(); i++) {
                        JSONObject revenueRateJsonObject = revenueRateArray.getJSONObject(i);
                        contentValues = new ContentValues();
//                        contentValues.put(DatabaseHandler.KEY_ID, revenueRateJsonObject.optString(""));
                        contentValues.put(DatabaseHandler.KEY_REVENUETYPEID, revenueRateJsonObject.optString("RevenueTypeId"));
                        contentValues.put(DatabaseHandler.KEY_REVENUERATEID, revenueRateJsonObject.optString("RevenueRateId"));
                        contentValues.put(DatabaseHandler.KEY_REVENUEUNIT, revenueRateJsonObject.optString("RevenueUnit"));
                        contentValues.put(DatabaseHandler.KEY_REVENUERATETYPE, revenueRateJsonObject.optString("RevenueRateType"));
                        contentValues.put(DatabaseHandler.KEY_REVENUERATE, revenueRateJsonObject.optString("RevenueRate"));
                        long response = dbHandler.addData(dbHandler.TABLE_TBLR_RevenueRate, contentValues);
                        System.out.println("response revenuerate:" + response);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ((RevenueSelectionActivity) context).setRevenues();
            ((RevenueSelectionActivity) context).setArea();
            ((RevenueSelectionActivity) context).setLocations(0);
            dialog.dismiss();
        }
    };

    private void getRevenueCustomer() {
        myclientget = new MyClientGet(context, "", onRevenueCustomerSyncComplete);
        myclientget.execute(getResources().getString(R.string.api_master) + "RevenueCustomer");
    }

    MyClientGet.OnGetCallComplete onRevenueCustomerSyncComplete = new MyClientGet.OnGetCallComplete() {
        @Override
        public void response(String result) {
            try {
                JSONObject jobj = new JSONObject(result);
                String response_code = jobj.getString("result");
                if (response_code.equals("1")) {
                    JSONArray revenueCustomerArray = new JSONArray();
                    revenueCustomerArray = jobj.getJSONArray("detail");
                    DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
                    ContentValues contentValues;

                    for (int i = 0; i < revenueCustomerArray.length(); i++) {
                        JSONObject revenueCustomerJsonObject = revenueCustomerArray.getJSONObject(i);
                        contentValues = new ContentValues();
                        contentValues.put(DatabaseHandler.KEY_RCUSTOMERID, revenueCustomerJsonObject.optString("RCustomerId"));
                        contentValues.put(DatabaseHandler.KEY_CUSTOMERNO, revenueCustomerJsonObject.optString("CustomerNo"));
                        contentValues.put(DatabaseHandler.KEY_CUSTOMERBARCODE, revenueCustomerJsonObject.optString("CustomerBarcode"));
                        contentValues.put(DatabaseHandler.KEY_REVENUETYPEID, revenueCustomerJsonObject.optString("RevenueTypeId"));
                        contentValues.put(DatabaseHandler.KEY_BUSINESSNAME, revenueCustomerJsonObject.optString("BusinessName"));
                        contentValues.put(DatabaseHandler.KEY_OWNERNAME, revenueCustomerJsonObject.optString("OwnerName"));
                        contentValues.put(DatabaseHandler.KEY_BUSINESSLICNO, revenueCustomerJsonObject.optString("BusinessLicNo"));
                        contentValues.put(DatabaseHandler.KEY_POSTALADDRESS, revenueCustomerJsonObject.optString("PostalAddress"));
                        contentValues.put(DatabaseHandler.KEY_POSTCODE, revenueCustomerJsonObject.optString("Postcode"));
                        contentValues.put(DatabaseHandler.KEY_CONTACTPERSON, revenueCustomerJsonObject.optString("ContactPerson"));
                        contentValues.put(DatabaseHandler.KEY_CONTACTNO, revenueCustomerJsonObject.optString("ContactNo"));
                        contentValues.put(DatabaseHandler.KEY_AREAID, revenueCustomerJsonObject.optString("AreaId"));
                        contentValues.put(DatabaseHandler.KEY_LOCATIONID, revenueCustomerJsonObject.optString("LocationId"));
                        contentValues.put(DatabaseHandler.KEY_EMAIL, revenueCustomerJsonObject.optString("Email"));
                        contentValues.put(DatabaseHandler.KEY_OUTSTANDINGAMT, revenueCustomerJsonObject.optString("OutstandngAmt"));
                        //contentValues.put(DatabaseHandler.KEY_MARKETSTATUS, revenueCustomerJsonObject.optString("MarketStatus"));
                        contentValues.put(DatabaseHandler.KEY_CREATEDBY, revenueCustomerJsonObject.optString("CreatedBy"));
                        contentValues.put(DatabaseHandler.KEY_CREATEDDATE, revenueCustomerJsonObject.optString("CreatedDate"));
                        //contentValues.put(DatabaseHandler.KEY_UPDATEDBY, revenueCustomerJsonObject.optString("UpdatedBy"));
                        //contentValues.put(DatabaseHandler.KEY_UPDATEDBY, revenueCustomerJsonObject.optString("UpdatedByUpdatedBy"));
                        //contentValues.put(DatabaseHandler.KEY_ISEXIST, revenueCustomerJsonObject.optString("IsExist"));
                        long response = dbHandler.addData(dbHandler.TABLE_TBLR_RevenueRate, contentValues);
                        System.out.println("response revenuerate:" + response);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();
        }
    };
}