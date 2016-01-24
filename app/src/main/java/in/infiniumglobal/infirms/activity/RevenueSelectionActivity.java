package in.infiniumglobal.infirms.activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenueselection);

        dbHandler = DatabaseHandler.getInstance(RevenueSelectionActivity.this);
        spinnerRevenue = (Spinner) findViewById(R.id.spinnerRevenue);
        spinnerArea = (Spinner) findViewById(R.id.spinnerArea);
        spinnerLocation = (Spinner) findViewById(R.id.spinnerLocation);

        areaList = new ArrayList<String>();
        revenueList = new ArrayList<String>();
        locationList = new ArrayList<String>();

        setRevenues();
        setArea();
        setLocations();

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected area: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRevenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected revenue: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected location: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setArea() {
        areaCursor = dbHandler.getAreas();
        areaList.add("Areas");
        areaList.add("Automobile");
        areaList.add("Business Services");
        areaList.add("Computers");
        areaList.add("Education");
        areaList.add("Personal");
        areaList.add("Travel");

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, areaList);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(areaAdapter);
    }

    private void setRevenues() {
        revenueCursor = dbHandler.getRevenues();
        for (int i = 0; i < revenueCursor.getCount(); i++) {
            System.out.println("revenue:" + revenueCursor.getString(revenueCursor.getColumnIndex(dbHandler.KEY_REVENUE_NAME)));
//            revenueList.add(revenueCursor.getString(revenueCursor.getColumnIndex(dbHandler.KEY_REVENUE_NAME)));
        }
//        revenueList.add("Revenue Type");
//        revenueList.add("Automobile");
//        revenueList.add("Business Services");
//        revenueList.add("Computers");
//        revenueList.add("Education");
//        revenueList.add("Personal");
//        revenueList.add("Travel");

        ArrayAdapter<String> revenueAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, revenueList);
        revenueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRevenue.setAdapter(revenueAdapter);
    }

    private void setLocations() {
        dbHandler.getLocations();
        locationList.add("Locations");
        locationList.add("Automobile");
        locationList.add("Business Services");
        locationList.add("Computers");
        locationList.add("Education");
        locationList.add("Personal");
        locationList.add("Travel");

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);
    }
}