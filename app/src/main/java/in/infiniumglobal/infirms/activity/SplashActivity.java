package in.infiniumglobal.infirms.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.client.MyClientGet;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;
import in.infiniumglobal.infirms.utils.Common;

/**
 * Created by Hiral on 1/26/2016.
 */

public class SplashActivity extends Activity {

    private MyClientGet myclientget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);

        AppConfig.ANDROID_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        AppConfig.ANDROID_ID = "5ed9afd105f813fb";

        try {
            exportDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseHandler dbHandler = DatabaseHandler.getInstance(this);
        Cursor company = dbHandler.getCompany();
        if (company != null && company.getCount() > 0) {
            company.moveToFirst();
            AppConfig.DeviceCode = company.getString(company.getColumnIndex(DatabaseHandler.KEY_DeviceCode));
            executeSplash();
        } else {
            if (Common.isNetworkAvailable(this)) {
                dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLB_Company);
                getCompany();
            } else {
                Intent mIntent;
                mIntent = new Intent(SplashActivity.this, LoginActivity.class);
                mIntent.putExtra("showId", true);
                startActivity(mIntent);
                finish();
            }
        }
    }

    public void exportDatabase() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();

                    if (sd.canWrite()) {
                        String currentDBPath = DatabaseHandler.DATABASE_PATH + DatabaseHandler.DATABASE_NAME;
                        String backupDBPath = "RMS_DB_backup.db";
                        File currentDB = new File(currentDBPath);
                        File backupDB = new File(sd, backupDBPath);

                        if (currentDB.exists()) {
                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    private void executeSplash() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent mIntent;
                if (Common.getStringPrefrences(SplashActivity.this, getString(R.string.pref_userName), getString(R.string.app_name)).trim().equals("")) {
                    mIntent = new Intent(SplashActivity.this, LoginActivity.class);
                } else {
                    mIntent = new Intent(SplashActivity.this, RevenueSelectionActivity.class);
                }
                startActivity(mIntent);
                finish();
            }
        }, 3000);
    }


    public void getCompany() {
        myclientget = new MyClientGet(SplashActivity.this, "", onCompanySyncComplete);
        myclientget.execute(getResources().getString(R.string.api_master) + "Company?DeviceUID=" + AppConfig.ANDROID_ID);
    }

    MyClientGet.OnGetCallComplete onCompanySyncComplete = new MyClientGet.OnGetCallComplete() {
        @Override
        public void response(String result) {
            try {
                JSONObject jobj = new JSONObject(result);
                String response_code = jobj.getString("result");
                if (response_code.equals("1")) {
                    JSONArray userArray = new JSONArray();
                    userArray = jobj.getJSONArray("detail");
                    JSONObject userJsonObject;
                    DatabaseHandler dbHandler = DatabaseHandler.getInstance(SplashActivity.this);
                    ContentValues contentValues;

                    if (userArray.length() > 0) {
                        for (int i = 0; i < userArray.length(); i++) {
                            userJsonObject = userArray.getJSONObject(i);
                            contentValues = new ContentValues();
                            contentValues.put(DatabaseHandler.KEY_CompID, userJsonObject.optString(DatabaseHandler.KEY_CompID));
                            contentValues.put(DatabaseHandler.KEY_CompDisplayName, userJsonObject.optString(DatabaseHandler.KEY_CompDisplayName));
                            contentValues.put(DatabaseHandler.KEY_CompAddress, userJsonObject.optString(DatabaseHandler.KEY_CompAddress));
                            contentValues.put(DatabaseHandler.KEY_CompPhone1, userJsonObject.optString(DatabaseHandler.KEY_CompPhone1));
                            contentValues.put(DatabaseHandler.KEY_CompEmail, userJsonObject.optString(DatabaseHandler.KEY_CompEmail));
                            contentValues.put(DatabaseHandler.KEY_CompLogo, userJsonObject.optString(DatabaseHandler.KEY_CompLogo));
                            contentValues.put(DatabaseHandler.KEY_DeviceCode, userJsonObject.optString(DatabaseHandler.KEY_DeviceCode));
                            contentValues.put(DatabaseHandler.KEY_DeviceUID, AppConfig.ANDROID_ID);
                            AppConfig.DeviceCode = userJsonObject.optString(DatabaseHandler.KEY_DeviceCode);
                            long response = dbHandler.addData(DatabaseHandler.TABLE_TBLB_Company, contentValues);
                            System.out.println("response user:" + response);
                            executeSplash();
                        }
                    } else {
                        Intent mIntent;
                        mIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        mIntent.putExtra("showId", true);
                        startActivity(mIntent);
                        finish();
                    }
                } else {
                    Intent mIntent;
                    mIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    mIntent.putExtra("showId", true);
                    startActivity(mIntent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}