package in.infiniumglobal.infirms.activity;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = BaseActivity.this;
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
                    getUsers();
                } else {
                    Toast.makeText(context, "No internet available.", Toast.LENGTH_LONG).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUsers() {
        myclientget = new MyClientGet(context, "Please wait...", onUserSyncComplete);
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
                        dbHandler.addData(dbHandler.TABLE_TBLA_User, contentValues);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void getAreas() {
        myclientget = new MyClientGet(context, "Please wait...", onAreaSyncComplete);
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
                        dbHandler.addData(dbHandler.TABLE_TBLR_Area, contentValues);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void getLocations() {
        myclientget = new MyClientGet(context, "Please wait...", onLocationSyncComplete);
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
                        dbHandler.addData(dbHandler.TABLE_TBLR_Location, contentValues);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void getRevenueType() {
        myclientget = new MyClientGet(context, "Please wait...", onRevenueSyncComplete);
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
                        dbHandler.addData(dbHandler.TABLE_TBLR_RevenueType, contentValues);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void getRevenueRate() {
        myclientget = new MyClientGet(context, "Please wait...", onRevenueRateSyncComplete);
        myclientget.execute(getResources().getString(R.string.api_master) + "RevenueRate");
    }

    MyClientGet.OnGetCallComplete onRevenueRateSyncComplete = new MyClientGet.OnGetCallComplete() {
        @Override
        public void response(String result) {
            try {
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
                        dbHandler.addData(dbHandler.TABLE_TBLR_RevenueRate, contentValues);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}