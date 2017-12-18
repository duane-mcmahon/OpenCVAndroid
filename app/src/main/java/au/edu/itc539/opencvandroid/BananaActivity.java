package au.edu.itc539.opencvandroid;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by duane on 16/12/2017.
 */

public class BananaActivity extends AppCompatActivity {

    Intent ii;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // setTheme(R.style.AppBaseTheme_BananaActivity);


        setContentView(R.layout.show_banana);


        handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {

                ii = new Intent();

                ii.putExtra("fruit", "banana");

                ii.setClass(BananaActivity.this, MainActivity.class);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                startActivity(ii);
                // finish();
            }
        });

    }


}
