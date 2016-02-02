package in.infiniumglobal.infirms;

import android.app.Application;
import android.content.SharedPreferences;
import android.serialport.api.SerialPort;
import android.serialport.api.SerialPortFinder;
import android.util.Log;
import android.view.ViewConfiguration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;

import in.infiniumglobal.infirms.db.DatabaseHandler;

/**
 * Created by admin on 1/24/2016.
 */
public class InfiRmsApplication extends Application {

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort(int databits, int stopbits, int parity) throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
			/* Read serial port parameters */

            SharedPreferences sp = getSharedPreferences("com.example.serialtest_preferences", Application.MODE_PRIVATE);
            String path = sp.getString("DEVICE", "");
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

			/* Check parameters */
            if ( (path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }
        }
        return mSerialPort;
    }

    private void makeActionOverflowMenuShown() {
        //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.d("Tag", e.getLocalizedMessage());
        }
    }

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
        makeActionOverflowMenuShown();
    }
}
