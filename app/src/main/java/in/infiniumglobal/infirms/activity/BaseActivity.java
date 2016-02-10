package in.infiniumglobal.infirms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.client.MyClientGet;
import in.infiniumglobal.infirms.client.MyClientPost;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;
import in.infiniumglobal.infirms.utils.Common;

/**
 * Created by admin on 1/26/2016.
 */
public class BaseActivity extends AppCompatActivity {

    private MyClientGet myclientget;
    private Context context;
    private ProgressDialog dialog;
    private DatabaseHandler dbHandler;
    private boolean revenueReceiptSync = false;
    Timer timer = new Timer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = BaseActivity.this;
        dbHandler = DatabaseHandler.getInstance(context);

    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_management, menu);
        MenuItem item = menu.findItem(R.id.action_receipt);
        if (context instanceof InstantPayActivity) {
            item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!(context instanceof CustomerManagementActivity))
                    finish();
                break;
            case R.id.action_logout:
                Common.removeAllPrefrences(this, getString(R.string.app_name));
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.action_sync:
//                TODO sync
                if (Common.isNetworkAvailable(context)) {
                    if (Common.isNetworkAvailable(context))
                        showSyncAlert();
                    else
                        Common.showNETWORDDisabledAlert(context);

                } else {
                    Toast.makeText(context, "No internet available.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_receipt:
                Intent intent1 = new Intent(this, ReceiptHistoryActivity.class);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
//                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //    Show sync Dialog
    public void pollForUpdates() {


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Calendar calendar = Calendar.getInstance();
                        String timemilli = Common.getStringPrefrences(context, "SyncTime", getString(R.string.app_name));
                        if (timemilli.length() > 0) {
                            try {
                                if (calendar.getTimeInMillis() - Long.parseLong(timemilli) > 3600000) {
                                    Common.setStringPrefrences(context, "SyncTime", calendar.getTimeInMillis() + "", getString(R.string.app_name));
                                    if (Common.isNetworkAvailable(context)) {
                                        showSyncAlert();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Common.setStringPrefrences(context, "SyncTime", calendar.getTimeInMillis() + "", getString(R.string.app_name));
                            if (Common.isNetworkAvailable(context)) {
                                Cursor receiptCursor = dbHandler.getRevenueReceipt();
                                Cursor adjustmentCursor = dbHandler.getAdjustment();
                                if ((receiptCursor != null && receiptCursor.getCount() > 0) || (adjustmentCursor != null && adjustmentCursor.getCount() > 0)) {
                                    showSyncAlert();
                                }
                            }
                        }
                        Common.setStringPrefrences(context, "SyncTime", calendar.getTimeInMillis() + "", getString(R.string.app_name));
                    }
                });

            }
        }, 0, 1 * 60 * 60 * 1000);
        Log.i(getClass().getSimpleName(), "Timer started.");
    }

    private void showSyncAlert() {
        AlertDialog alertDialog = null;
        if (!((Activity) context).isFinishing()) {
            if (alertDialog == null)
                alertDialog = new AlertDialog.Builder(context).create();
            // Setting Dialog Title
            alertDialog.setTitle("Sync Records");
            // Setting Dialog Message
            alertDialog.setMessage("Do you want to sync records?");
            // Setting OK Button
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    syncCustomers();
                    syncDatabase();
                    dialog.dismiss();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
    }

    private void syncCustomers() {
        Cursor receiptCursor = dbHandler.getCustomers();
        if (receiptCursor != null && receiptCursor.getCount() > 0) {
            receiptCursor.moveToFirst();
            JSONArray receiptArray = new JSONArray();

            try {
                while (!receiptCursor.isAfterLast()) {
                    JSONObject receiptObject = new JSONObject();
                    receiptObject.put("RCustomerId", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_RCUSTOMERID)));
                    receiptObject.put("CustomerNo", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_CUSTOMERNO)));
                    receiptObject.put("CustomerBarcode", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_CUSTOMERBARCODE)));
                    receiptObject.put("RevenueTypeId", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_REVENUETYPEID)));
                    receiptObject.put("BusinessName", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_BUSINESSNAME)));
                    receiptObject.put("OwnerName", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_OWNERNAME)));
                    receiptObject.put("BusinessLicNo", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_BUSINESSLICNO)));
                    receiptObject.put("TINNo", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_TINNO)));
                    receiptObject.put("VNRNo", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_VNRNO)));
                    receiptObject.put("OutstandingAmt", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_OUTSTANDINGAMT)));
                    receiptObject.put("PostalAddress", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_POSTALADDRESS)));
                    receiptObject.put("Postcode", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_POSTCODE)));
                    receiptObject.put("ContactPerson", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_CONTACTPERSON)));
                    receiptObject.put("ContactNo", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_CONTACTNO)));
                    receiptObject.put("AreaId", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_AREAID)));
                    receiptObject.put("LocationId", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_LOCATIONID)));
                    receiptObject.put("Email", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_EMAIL)));
                    receiptObject.put("CreatedBy", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_CREATEDBY)));
                    receiptObject.put("CreatedDate", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_CREATEDDATE)));
                    receiptArray.put(receiptObject);
                    receiptCursor.moveToNext();
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Coll", receiptArray);
                sendData("RevenueCustomerReceive", jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //    Sync Databse tables
    private void syncDatabase() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Cursor receiptCursor = dbHandler.getRevenueReceipt();
        Cursor adjustmentCursor = dbHandler.getAdjustment();
        if (receiptCursor != null && receiptCursor.getCount() > 0) {
            receiptCursor.moveToFirst();
            JSONArray receiptArray = new JSONArray();

            try {
                while (!receiptCursor.isAfterLast()) {
                    JSONObject receiptObject = new JSONObject();
//                    receiptObject.put("RReceiptId", "1");
                    receiptObject.put("RReceiptId", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_ID)));
                    receiptObject.put("RevenueTypeId", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_REVENUETYPEID)));
                    receiptObject.put("RCustomerId", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_RCUSTOMERID)));
                    receiptObject.put("CustomerName", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_CUSTOMERNAME)));
                    receiptObject.put("RReceiptDate", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_RRECEIPTDATE)));
                    receiptObject.put("ReceiptNo", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_RECEIPTNO)));
                    receiptObject.put("ReceiptBarcode", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_RECEIPTBARCODE)));
                    receiptObject.put("RevenueRateID", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_RREVENUERATEID)));
                    receiptObject.put("RevenueRate", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_REVENUERATE)));
                    receiptObject.put("TotalUnit", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_TOTALUNIT)));
                    receiptObject.put("TotalAmount", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_TOTALAMOUNT)));
                    receiptObject.put("OtherChargse", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_OTHERCHARGSE)));
                    receiptObject.put("AdjustmentAmt", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_ADJUSTMENTAMT)));
                    receiptObject.put("PaidAmount", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_PAIDAMOUNT)));
                    receiptObject.put("PayType", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_PAYTYPE)));
                    receiptObject.put("BankName", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_BANKNAME)));
                    receiptObject.put("ChequeNo", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_CHEQUENO)));
                    receiptObject.put("PayRemarks", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_PAYREMARKS)));
                    receiptObject.put("CreatedBy", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_CREATEDBY)));
                    receiptObject.put("CreatedDate", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_CREATEDDATE)));
                    receiptObject.put("LocationId", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_LOCATIONID)));
                    receiptObject.put("AreaId", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_AREAID)));
                    receiptObject.put(DatabaseHandler.KEY_DeviceCode, receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.KEY_DeviceCode)));
//                receiptObject.put("", receiptCursor.getString(receiptCursor.getColumnIndex(DatabaseHandler.)));
                    receiptArray.put(receiptObject);
                    receiptCursor.moveToNext();
                }
                revenueReceiptSync = true;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Coll", receiptArray);
                sendData("RevenueReceiptReceive", jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            JSONObject finalObject = new JSONObject();
//            finalObject.p

        } else if (adjustmentCursor != null && adjustmentCursor.getCount() > 0) {
            JSONArray adjustmentArray = new JSONArray();
            adjustmentCursor.moveToFirst();

            try {
                while (!adjustmentCursor.isAfterLast()) {
                    JSONObject adjustmentObject = new JSONObject();
                    adjustmentObject.put("AdjustmentId", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_ID)));
                    adjustmentObject.put("CustomerId", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_CUSTOMERID)));
                    adjustmentObject.put("RevenueTypeId", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_REVENUE_TYPEID)));
                    adjustmentObject.put("AdjustmentDate", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_ADJUSTMENTDATE)));
                    adjustmentObject.put("AdjustmentType", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_ADJUSTMENTTYPE)));
                    adjustmentObject.put("Amount", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_AMOUNT)));
                    adjustmentObject.put("Remarks", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_REMARKS)));
                    adjustmentObject.put("CreatedBy", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_CREATEDBY)));
                    adjustmentObject.put("CreatedDate", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_CREATEDDATE)));
//                receiptObject.put("", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.)));
                    adjustmentArray.put(adjustmentObject);
                    adjustmentCursor.moveToNext();
                }
                sendData("AdjustmentReceive", adjustmentArray.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLA_User);
            dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_Area);
            dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_Location);
            dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueCustomer);
            dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueRate);
            dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueType);
//            dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_Adjustment);
//            dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueReceipt);
            dbHandler.removeOldRecords(DatabaseHandler.TABLE_TBLR_Adjustment);
            dbHandler.removeOldRecords(DatabaseHandler.TABLE_TBLR_RevenueReceipt);

            getUsers();
        }


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
                        long response = dbHandler.addData(DatabaseHandler.TABLE_TBLA_User, contentValues);
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
                        long response = dbHandler.addData(DatabaseHandler.TABLE_TBLR_Area, contentValues);
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
                        long response = dbHandler.addData(DatabaseHandler.TABLE_TBLR_Location, contentValues);
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
        myclientget.execute(getResources().getString(R.string.api_master) + "RevenueType?DeviceCode=" + AppConfig.DeviceCode);
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
                        contentValues.put(DatabaseHandler.KEY_INSTANTPAY, revenueJsonObject.optString("InstantReceipt"));
                        contentValues.put(DatabaseHandler.KEY_REVENUECODE, revenueJsonObject.optString(DatabaseHandler.KEY_REVENUECODE));
                        contentValues.put(DatabaseHandler.KEY_RECEIPTCODE, revenueJsonObject.optString("ReceiptNo"));
                        long response = dbHandler.addData(DatabaseHandler.TABLE_TBLR_RevenueType, contentValues);
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
                        long response = dbHandler.addData(DatabaseHandler.TABLE_TBLR_RevenueRate, contentValues);
                        System.out.println("response revenuerate:" + response);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (context instanceof RevenueSelectionActivity) {
                ((RevenueSelectionActivity) context).setRevenues();
                ((RevenueSelectionActivity) context).setArea();
                ((RevenueSelectionActivity) context).setLocations(0);
            }

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
                        long response = dbHandler.addData(DatabaseHandler.TABLE_TBLR_RevenueCustomer, contentValues);
                        System.out.println("response revenuerate:" + response);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();
        }
    };


//    Send data from db

    private void sendData(String methodUrl, String jsonSend) {
        System.out.println("Sync Json : " + jsonSend);
        Map<String, String> get_sync_contact_params = new HashMap<String, String>();
        get_sync_contact_params.put("Coll", jsonSend);
        Map<String, Object> api_params = new HashMap<String, Object>();
        api_params.put("url", getResources().getString(R.string.api_master) + methodUrl);
        api_params.put("method_parameters", get_sync_contact_params);
//        System.out.println(api_params.values().toString());
        MyClientPost posting = new MyClientPost(this, "Loading...", onSettingsCallComplete);
        posting.execute(api_params);
//        getUsers(); //TODO remove comments and call method for sync data
    }

    private MyClientPost.OnPostCallComplete onSettingsCallComplete = new MyClientPost.OnPostCallComplete() {
        @Override
        public void response(String result) {
            boolean success = false;
            String errorMsg = "";
            String userId = "";


            try {
                JSONObject jobj = new JSONObject(result);
                String response_code = jobj.getString("result");
                if (response_code.equals("1")) {
                    if (revenueReceiptSync && dbHandler.getAdjustment().getCount() > 0) {
                        revenueReceiptSync = false;
                        Cursor adjustmentCursor = dbHandler.getAdjustment();
                        JSONArray adjustmentArray = new JSONArray();
                        adjustmentCursor.moveToFirst();

                        try {
                            while (!adjustmentCursor.isAfterLast()) {
                                JSONObject adjustmentObject = new JSONObject();
                                adjustmentObject.put("AdjustmentId", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_ID)));
                                adjustmentObject.put("CustomerId", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_CUSTOMERID)));
                                adjustmentObject.put("RevenueTypeId", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_REVENUE_TYPEID)));
                                adjustmentObject.put("AdjustmentDate", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_ADJUSTMENTDATE)));
                                adjustmentObject.put("AdjustmentType", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_ADJUSTMENTTYPE)));
                                adjustmentObject.put("Amount", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_AMOUNT)));
                                adjustmentObject.put("Remarks", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_REMARKS)));
                                adjustmentObject.put("CreatedBy", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_CREATEDBY)));
                                adjustmentObject.put("CreatedDate", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.KEY_CREATEDDATE)));
//                receiptObject.put("", adjustmentCursor.getString(adjustmentCursor.getColumnIndex(DatabaseHandler.)));
                                adjustmentArray.put(adjustmentObject);
                                adjustmentCursor.moveToNext();
                            }
                            sendData("AdjustmentReceive", adjustmentArray.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLA_User);
                        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_Area);
                        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_Location);
                        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueCustomer);
                        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueRate);
//                        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueReceipt);
//                        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_Adjustment);
                        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueType);

                        dbHandler.removeOldRecords(DatabaseHandler.TABLE_TBLR_Adjustment);
                        dbHandler.removeOldRecords(DatabaseHandler.TABLE_TBLR_RevenueReceipt);
                        getUsers();
                    }
                } else {
                    dialog.dismiss();
                }
//                Common.showAlertDialog(context, "", errorMsg, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    };


    public static String getReceiptNo(String devicecode, String revenuecode, String receiptcode) {

        //Parameter value like devicecode="01",  revenuecode="02",  receiptcode="010216027002513"

        String date = Common.getCurrentDate("yyMM");
        String tReceiptCode = "";
        String ttReceiptCode = "";


//        year = DateTime.Now.Date.ToString("yy"); //Result:16, Description : 16 is current year
//        month = DateTime.Now.Date.ToString("MM"); //Result:02, Description : 02 is current month with 2 digit
        tReceiptCode = devicecode + revenuecode + date; //Result:01021602,


        ttReceiptCode = receiptcode.substring(0, 8); //Result:01021602,Description :Receipt first 8 chars
        if (tReceiptCode.equals(ttReceiptCode)) {
            // compare
            int temp = Integer.parseInt(receiptcode.substring(receiptcode.length() - 7)) + 1; //Result:7002513+1=7002514
            String stemp = "000000" + temp; //Result:0000007002514
            stemp = stemp.substring(stemp.length() - 7, 7); // Result: 7002514 ,Description : get last 7 chars
            receiptcode = devicecode + revenuecode + date + stemp;//Result:010216027002514
        } else {
            receiptcode = devicecode + revenuecode + date + "0000001";
        }
        return receiptcode;
    }

}