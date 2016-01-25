package in.infiniumglobal.infirms.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class RevenueSelectionActivity extends AppCompatActivity {

    private Spinner spinnerLocation, spinnerRevenue, spinnerArea;
    private ArrayList<String> locationList, revenueList, areaList;
    private DatabaseHandler dbHandler;
    private Cursor areaCursor, locationCursor, revenueCursor;
    Button btnNext;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenueselection);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = DatabaseHandler.getInstance(RevenueSelectionActivity.this);
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
                String item = parent.getItemAtPosition(position).toString();
                if (!isFirst) {
                    int areaID = getAreaIdFromCursor(item);
                    System.out.println("selected areaid:" + areaID);
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
                String item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RevenueSelectionActivity.this, CustomerSearchActivity.class));
                finish();
            }
        });
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
        return areaID;
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

    private void setArea() {
        areaCursor = dbHandler.getAreas();
        System.out.println("area size:" + areaCursor.getCount());
        areaCursor.moveToFirst();
        while (!areaCursor.isAfterLast()) {
            areaList.add(areaCursor.getString(areaCursor.getColumnIndex(dbHandler.KEY_AREANAME))); //add the item
            areaCursor.moveToNext();
        }

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, areaList);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(areaAdapter);
    }

    private void setRevenues() {
        revenueCursor = dbHandler.getRevenues();
        System.out.println("revenue size:" + revenueCursor.getCount());
        revenueCursor.moveToFirst();
        while (!revenueCursor.isAfterLast()) {
            revenueList.add(revenueCursor.getString(revenueCursor.getColumnIndex(dbHandler.KEY_REVENUE_NAME))); //add the item
            revenueCursor.moveToNext();
        }

        ArrayAdapter<String> revenueAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, revenueList);
        revenueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRevenue.setAdapter(revenueAdapter);
    }

    private void setLocations(int locationID) {
        if (locationID != 0) {
            locationCursor = dbHandler.getLocationsByID(locationID);
        } else {
            locationCursor = dbHandler.getLocations();
        }
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
    }
}