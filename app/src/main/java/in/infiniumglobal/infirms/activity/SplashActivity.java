package in.infiniumglobal.infirms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.utils.Common;

/**
 * Created by Hiral on 1/26/2016.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

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
}