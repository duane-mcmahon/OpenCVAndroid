package au.edu.itc539.opencvandroid;

import static java.util.Arrays.asList;

import android.app.Application;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duane on 24/12/2017.
 */
public class Detectable extends Application {

  List<String> detectables = new ArrayList<>(asList("orange", "banana"));
}