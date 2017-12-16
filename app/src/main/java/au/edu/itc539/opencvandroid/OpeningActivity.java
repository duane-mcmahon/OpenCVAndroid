package au.edu.itc539.opencvandroid;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;

import at.lukle.clickableareasimage.ClickableArea;
import at.lukle.clickableareasimage.ClickableAreasImage;
import at.lukle.clickableareasimage.OnClickableAreaClickedListener;
import uk.co.senab.photoview.PhotoViewAttacher;


public class OpeningActivity extends Activity implements OnClickableAreaClickedListener {

    private static final String DEBUG_TAG = "OpeningActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(DEBUG_TAG, "Called onCreate of OpeningActivity...");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_opening);


        ImageView image = findViewById(R.id.opening_background);

        // Create your image
        ClickableAreasImage clickableAreasImage = new ClickableAreasImage(new PhotoViewAttacher(image), this);

        // Initialize your clickable area list
        List<ClickableArea> clickableAreas = new ArrayList<>();

        // Define your clickable areas
        // parameter values (pixels): (x coordinate, y coordinate, width, height) and assign an object to it
        clickableAreas.add(new ClickableArea(58, 118, 72, 225, "banana"));
        clickableAreas.add(new ClickableArea(249, 114, 106, 100, "orange"));

        // Set your clickable areas to the image
        clickableAreasImage.setClickableAreas(clickableAreas);


    }

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


    @Override
    public void onClickableAreaTouched(Object item) {

        switch ((String) item) {

            case "banana":

                startActivity(new Intent(this, BananaActivity.class));

                finish();

            case "orange":

                startActivity(new Intent(this, OrangeActivity.class));

                finish();

        }
    }
}