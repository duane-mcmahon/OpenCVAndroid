package au.edu.itc539.opencvandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by duane on 16/12/2017.
 */

public class OrangeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Start main camera activity
        Intent ii = new Intent();

        ii.putExtra("fruit", "orange");

        ii.setClass(OrangeActivity.this, MainActivity.class);

        startActivity(ii);
        // close splash activity
        finish();
    }

}
