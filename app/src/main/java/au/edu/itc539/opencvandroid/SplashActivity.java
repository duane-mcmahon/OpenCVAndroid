package au.edu.itc539.opencvandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by duane on 29/11/2017.
 */

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Start home activity
        startActivity(new Intent(SplashActivity.this, OpeningActivity.class));
        // close splash activity
        finish();
    }

}