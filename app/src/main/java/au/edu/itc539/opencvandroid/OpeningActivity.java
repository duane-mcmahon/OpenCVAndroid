package au.edu.itc539.opencvandroid;

import android.app.ActionBar;
import android.app.Activity;

import android.content.Intent;

import android.graphics.Rect;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import java.util.Timer;
import java.util.TimerTask;


public class OpeningActivity extends Activity {

    private static final String DEBUG_TAG = "OpeningActivity";
    private float[] lastTouchDownXY = new float[2];

    private Intent ii;
    private Rect banana, orange;
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

        opening_view.setOnTouchListener(touchListener);

        opening_view.setOnClickListener(clickListener);

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        imageSwitcher.setInAnimation(in);
        //imageSwitcher.setOutAnimation(out);

        // Define clickable areas

        banana = new Rect(115, 450, 380, 920);

        orange = new Rect(538, 462, 750, 643);





    }


    // the purpose of the touch listener is just to store the touch X,Y coordinates
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // save the X,Y coordinates
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lastTouchDownXY[0] = event.getX();
                lastTouchDownXY[1] = event.getY();
            }

            // let the touch event pass on to whoever needs it
            return false;
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // retrieve the stored coordinates
            float x = lastTouchDownXY[0];
            float y = lastTouchDownXY[1];
            Log.i("TAG", "onLongClick: x = " + x + ", y = " + y);

            if (banana.contains((int) x, (int) y)) {

                Log.i("TAG", "Banana Clicked");
                // use the coordinates for whatever
                imageSwitcher.setImageResource(R.drawable.banana_selected);

                state.setText("banana");

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

            if (orange.contains((int) x, (int) y)) {
                // use the coordinates for whatever
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

        }
    };



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