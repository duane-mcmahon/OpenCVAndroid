package au.edu.itc539.opencvandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * A menu - the user selects a fruit from the bowl. <br />
 *
 * @author Duane McMahon
 * @version 1.0
 * @since 07-01-2018
 */
public class OpeningActivity extends Activity {

  private static final String DEBUG_TAG = "OpeningActivity";

  private Intent ii;
  private ImageSwitcher imageSwitcher;
  private ImageView opening_view;
  private TextView state;
  final Handler handler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.show_opening);

    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    // First check android version
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
      // Check if permission is already granted
      if (!hasPermissions(this, PERMISSIONS)) {
        // Request the permission.
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
      }
    }

    state = findViewById(R.id.select);

    imageSwitcher = findViewById(R.id.opening_image);

    imageSwitcher.setFactory(
        new ViewSwitcher.ViewFactory() {
          @Override
          public View makeView() {

            opening_view = new ImageView(getApplicationContext());

            return opening_view;
          }
        });

    imageSwitcher.setImageResource(R.drawable.theme_image);

    Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

    imageSwitcher.setInAnimation(in);

    ImageView bananaButton = findViewById(R.id.banana_button);

    ImageView orangeButton = findViewById(R.id.orange_button);

    bananaButton.setOnClickListener(bananaClickListener);

    orangeButton.setOnClickListener(orangeClickListener);

  }

  View.OnClickListener bananaClickListener =
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {

          // use the coordinates for whatever
          imageSwitcher.setImageResource(R.drawable.banana_selected);

          state.setText(R.string.banana);

          handler.postDelayed(
              new Runnable() {

                @Override
                public void run() {

                  ii = new Intent();

                  ii.putExtra("fruit", "banana");

                  ii.setClass(OpeningActivity.this, MainActivity.class);

                  startActivity(ii);

                  finish();
                }
              },
              1000L);
        }
      }; // end-of-clickListener

  View.OnClickListener orangeClickListener =
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {

          // use the coordinates for whatever
          imageSwitcher.setImageResource(R.drawable.orange_selected);

          state.setText("orange");

          handler.postDelayed(
              new Runnable() {

                @Override
                public void run() {

                  ii = new Intent();

                  ii.putExtra("fruit", "orange");

                  ii.setClass(OpeningActivity.this, MainActivity.class);

                  startActivity(ii);

                  finish();
                }
              },
              1000L);
        }
      }; // end-of-clickListener

  @Override
  protected void onDestroy() {
    super.onDestroy();

  }

  @Override
  protected void onStart() {
    super.onStart();

  }

  @Override
  protected void onRestart() {
    super.onRestart();

  }

  @Override
  protected void onResume() {
    super.onResume();

  }

  @Override
  protected void onPause() {
    super.onPause();

  }

  @Override
  protected void onStop() {
    super.onStop();

  }

  /**
   * Helper method for acquiring permissions to use hardware features
   *
   * @param context i.e this Activity (e.g.: MainActivity.this)
   * @param permissions e.g. CAMERA, WRITE_EXTERNAL_STORAGE
   */
  public static boolean hasPermissions(Context context, String... permissions) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
        && context != null
        && permissions != null) {
      for (String permission : permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission)
            != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
    }
    return true;
  }
}
