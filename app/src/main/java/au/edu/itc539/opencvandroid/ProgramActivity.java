package au.edu.itc539.opencvandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by duane on 30/11/2017.
 */

public class ProgramActivity extends Activity implements View.OnClickListener {

    private ListView lv;

    private Context context;

    private ImageButton launchCameraActivityButton;

    private static final String DEBUG_TAG = "ProgramActivity";

    static Map<String, Integer> data;

    static {

        data = new LinkedHashMap<>();

        data.put("Banana", R.drawable.unidentified);

    }

    static Map<String, Integer> info;

    static {

        info = new HashMap<>();

        info.put("Banana", R.drawable.banana);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(DEBUG_TAG, "Called onCreate of ProgramActivity...");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_program);

        context = this;

        lv = findViewById(R.id.listView);

        lv.setAdapter(new CustomAdapter(data));

        launchCameraActivityButton = findViewById(R.id.nextButton);

        launchCameraActivityButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {


        Intent intent = new Intent(this, MainActivity.class);

        List<String> l = new ArrayList<String>(data.keySet());

        intent.putExtra("fruit", "banana");

        startActivity(intent);

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

}