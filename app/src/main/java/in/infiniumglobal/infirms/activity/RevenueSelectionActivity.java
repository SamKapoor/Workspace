package in.infiniumglobal.infirms.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;
import in.infiniumglobal.infirms.utils.Common;

public class RevenueSelectionActivity extends BaseActivity {

    private Spinner spinnerLocation, spinnerRevenue, spinnerArea;
    private ArrayList<String> locationList, revenueList, areaList;
    private DatabaseHandler dbHandler;
    private Cursor areaCursor, locationCursor, revenueCursor;
    Button btnNext;
    private boolean isFirst = true;
    private int revenueID = 0, areaID = 0;
    private int locationID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenueselection);

        dbHandler = DatabaseHandler.getInstance(RevenueSelectionActivity.this);
        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_Adjustment);
        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_Area);
        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_Location);
        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueCustomer);
        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueRate);
        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueReceipt);
        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBLR_RevenueType);
//        dbHandler.deleteAllRecords(DatabaseHandler.TABLE_TBL);

        spinnerRevenue = (Spinner) findViewById(R.id.spinnerRevenue);
        spinnerArea = (Spinner) findViewById(R.id.spinnerArea);
        spinnerLocation = (Spinner) findViewById(R.id.spinnerLocation);
        btnNext = (Button) findViewById(R.id.btnNext);

        areaList = new ArrayList<String>();
        revenueList = new ArrayList<String>();
        locationList = new ArrayList<String>();

        setRevenues();
        setArea();
        setLocations(0);

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String areaItem = parent.getItemAtPosition(position).toString();
                areaID = getAreaIdFromCursor(areaItem);
                AppConfig.areaID = areaID;
                AppConfig.areaItem = areaItem;
                if (!isFirst) {
                    setLocations(areaID);
                } else {
                    isFirst = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRevenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String revenueItem = parent.getItemAtPosition(position).toString();
                revenueID = getRevenueIdFromCursor(revenueItem);
                AppConfig.revenueID = revenueID;
                AppConfig.revenueItem = revenueItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String locationItem = parent.getItemAtPosition(position).toString();
                locationID = getLocationIdFromCursor(locationItem);
                AppConfig.locationID = locationID;
                AppConfig.locationItem = locationItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areaCursor != null && locationCursor != null && revenueCursor != null && areaCursor.getCount() > 0 && locationCursor.getCount() > 0 && revenueCursor.getCount() > 0) {
                    startActivity(new Intent(RevenueSelectionActivity.this, CustomerSearchActivity.class));
                    finish();
                } else {
                    Toast.makeText(RevenueSelectionActivity.this, "Please sync..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int getRevenueIdFromCursor(String item) {
        int revenueID = 0;
        revenueCursor.moveToFirst();
        while (!revenueCursor.isAfterLast()) {
            if (revenueCursor.getString(revenueCursor.getColumnIndex(dbHandler.KEY_REVENUE_NAME)).equals(item)) {
                revenueID = revenueCursor.getInt(revenueCursor.getColumnIndex(dbHandler.KEY_REVENUE_TYPEID));
            }
            revenueCursor.moveToNext();
        }

        System.out.println("selected revenue id:" + item + ":" + revenueID);
        return revenueID;
    }

    private int getAreaIdFromCursor(String item) {
        int areaID = 0;
        areaCursor.moveToFirst();
        while (!areaCursor.isAfterLast()) {
            if (areaCursor.getString(areaCursor.getColumnIndex(dbHandler.KEY_AREANAME)).equals(item)) {
                areaID = areaCursor.getInt(areaCursor.getColumnIndex(dbHandler.KEY_AREAID));
            }
            areaCursor.moveToNext();
        }
        System.out.println("selected area id:" + item + ":" + areaID);
        return areaID;
    }

    private int getLocationIdFromCursor(String item) {
        int locationID = 0;
        locationCursor.moveToFirst();
        while (!locationCursor.isAfterLast()) {
            if (locationCursor.getString(locationCursor.getColumnIndex(dbHandler.KEY_LOCATIONNAME)).equals(item)) {
                locationID = locationCursor.getInt(locationCursor.getColumnIndex(dbHandler.KEY_LOCATIONID));
            }
            locationCursor.moveToNext();
        }
        System.out.println("selected location id:" + item + ":" + locationID);
        return locationID;
    }

    private void setArea() {
        areaCursor = dbHandler.getAreas();
        if (areaCursor != null && areaCursor.getCount() > 0) {
            System.out.println("area size:" + areaCursor.getCount());
            areaCursor.moveToFirst();
            while (!areaCursor.isAfterLast()) {
                areaList.add(areaCursor.getString(areaCursor.getColumnIndex(dbHandler.KEY_AREANAME))); //add the item
                areaCursor.moveToNext();
            }

            ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, areaList);
            areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerArea.setAdapter(areaAdapter);
        } else {
//            Toast.makeText(RevenueSelectionActivity.this, "Please sync..", Toast.LENGTH_SHORT).show();
        }
    }

    private void setRevenues() {
        revenueCursor = dbHandler.getRevenues();
        if (revenueCursor != null && revenueCursor.getCount() > 0) {
            System.out.println("revenue size:" + revenueCursor.getCount());
            revenueCursor.moveToFirst();
            while (!revenueCursor.isAfterLast()) {
                revenueList.add(revenueCursor.getString(revenueCursor.getColumnIndex(dbHandler.KEY_REVENUE_NAME))); //add the item
                revenueCursor.moveToNext();
            }

            ArrayAdapter<String> revenueAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, revenueList);
            revenueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRevenue.setAdapter(revenueAdapter);
        } else {
            Toast.makeText(RevenueSelectionActivity.this, "Please sync..", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocations(int locationID) {
        if (locationID != 0) {
            locationCursor = dbHandler.getLocationsByID(locationID);
        } else {
            locationCursor = dbHandler.getLocations();
        }
        if (locationCursor != null && locationCursor.getCount() > 0) {
            System.out.println("location size:" + locationCursor.getCount());
            locationCursor.moveToFirst();
            locationList.clear();
            while (!locationCursor.isAfterLast()) {
                locationList.add(locationCursor.getString(locationCursor.getColumnIndex(dbHandler.KEY_LOCATIONNAME))); //add the item
                locationCursor.moveToNext();
            }

            ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationList);
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLocation.setAdapter(locationAdapter);
        } else {
//            Toast.makeText(RevenueSelectionActivity.this, "Please sync..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}