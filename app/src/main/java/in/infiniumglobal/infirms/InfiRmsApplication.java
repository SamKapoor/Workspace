package in.infiniumglobal.infirms;

import android.app.Application;

import java.io.IOException;

import in.infiniumglobal.infirms.db.DatabaseHandler;

/**
 * Created by admin on 1/24/2016.
 */
public class InfiRmsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHandler dbHelper = DatabaseHandler.getInstance(this);
        try {
            dbHelper.copyDataBaseFromAsset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dbHelper.openDataBase();
    }
}
