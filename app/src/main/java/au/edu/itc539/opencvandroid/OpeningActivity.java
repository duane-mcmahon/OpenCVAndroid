package au.edu.itc539.opencvandroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;


public class OpeningActivity extends Activity {

    private static final String DEBUG_TAG = "OpeningActivity";


    private Intent ii;
    private ImageSwitcher imageSwitcher;
    private ImageView opening_view;
    private TextView state;
    final Handler handler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Log.i(DEBUG_TAG, "Called onCreate of OpeningActivity...");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_opening);

        state = findViewById(R.id.select);

        imageSwitcher = findViewById(R.id.opening_image);

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
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


    View.OnClickListener bananaClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            Log.i("TAG", "Banana Clicked");
            // use the coordinates for whatever
            imageSwitcher.setImageResource(R.drawable.banana_selected);

            state.setText(R.string.banana);

            handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    ii = new Intent();

                    ii.putExtra("fruit", "banana");

                    ii.setClass(OpeningActivity.this, MainActivity.class);

                    startActivity(ii);

                    finish();

                }

            }, 1000L);


        }

    }; // end-of-clickListener

    View.OnClickListener orangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            // use the coordinates for whatever
            Log.i("TAG", "Orange Clicked");
            imageSwitcher.setImageResource(R.drawable.orange_selected);

            state.setText("orange");

            handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    ii = new Intent();

                    ii.putExtra("fruit", "orange");

                    ii.setClass(OpeningActivity.this, MainActivity.class);

                    startActivity(ii);

                    finish();

                }

            }, 1000L);


            }

    };  // end-of-clickListener


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(DEBUG_TAG, "OnDestroy()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(DEBUG_TAG, "OnStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(DEBUG_TAG, "OnRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(DEBUG_TAG, "OnResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(DEBUG_TAG, "OnPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(DEBUG_TAG, "OnStop()");
    }


}