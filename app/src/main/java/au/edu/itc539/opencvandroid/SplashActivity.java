package au.edu.itc539.opencvandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * The opening title screen. Flashes an image of a bowl of oranges and bananas.
 */
public class SplashActivity extends AppCompatActivity {

  private static int SPLASH_TIME_OUT = 2400; // Delay of 2.4 Seconds

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    // Remove title bar

    new Handler()
        .postDelayed(
            new Runnable() {
              @Override
              public void run() {
                // This method will be executed once the timer is over
                // Start home activity
                startActivity(new Intent(SplashActivity.this, OpeningActivity.class));
                // close splash activity
                finish();
              }
            },
            SPLASH_TIME_OUT);
  }
}
