package au.edu.itc539.opencvandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.wang.avi.AVLoadingIndicatorView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * <b> Detects an orange or a banana in real time </b> using <br />
 * the camera of an android phone or tablet. Developed on a <br />
 * motorola moto z play. <br />
 *
 * @author Duane McMahon
 * @version 1.0
 * @since 07-01-2018
 */
public class MainActivity extends AppCompatActivity
    implements CvCameraViewListener2, SensorEventListener {

  private Scalar RED = new Scalar(255, 0, 0);

  private Hashtable<Integer, Integer> rectBuckts = new Hashtable<>();

  private Hashtable<Integer, Rect> rectCue = new Hashtable<>();

  private final int JAVA_DETECTOR = 0;
  // Used for logging success or failure messages
  private static final String TAG = "MainActivity";
  // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
  private JavaCameraView mOpenCvCameraView;

  private File mCascadeFile;

  private int mDetectorType = JAVA_DETECTOR;

  private String[] mDetectorName;

  private CascadeClassifier mJavaDetector;

  private Mat mRgba, mGray, tmp, modified;

  private float mRelativeFruitSize = 0.2f;

  private int mAbsoluteFruitSize, count;

  private ImageView portrait_label, rev_landscape_label, iv, nextButton;

  private TextView landscape_label;

  private ImageButton nxt;

  private SensorManager mSensorManager;

  private Sensor mRotationSensor;

  private final int SENSOR_DELAY = 500 * 1000; // 500ms

  private final int FROM_RADS_TO_DEGS = -57;

  private String fruit_classifier;

  private AVLoadingIndicatorView avi;

  private int mWidth, mHeight;

  private Point centre;

  private Bitmap bm;



  private Detectable undetected;
  // initialize and load the opencv libraries and modules
  private BaseLoaderCallback mLoaderCallback =
      new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
          switch (status) {
            case LoaderCallbackInterface.SUCCESS: {
              Log.i(TAG, "OpenCV loaded successfully");

              Intent iin = getIntent();

              Bundle b = iin.getExtras();

              if (b != null) {
                fruit_classifier = (String) b.get("fruit");
              }

              Log.i(TAG, "OpenCV loaded successfully. Fruit: " + fruit_classifier);

              try {
                // load cascade file from application resources
                InputStream is =
                    getResources()
                        .openRawResource(
                            getResources()
                                .getIdentifier(fruit_classifier, "raw", getPackageName()));

                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);

                mCascadeFile = new File(cascadeDir, fruit_classifier + ".xml");

                FileOutputStream os = new FileOutputStream(mCascadeFile);

                byte[] buffer = new byte[4096];

                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {

                  os.write(buffer, 0, bytesRead);
                }

                is.close();

                os.close();

                mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());

                if (mJavaDetector.empty()) {

                  Log.e(TAG, "Failed to load cascade classifier");

                  mJavaDetector = null;

                } else {
                  Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                }

                cascadeDir.delete();

              } catch (IOException e) {

                e.printStackTrace();

                Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
              }

              portrait_label = findViewById(R.id.fruit_target_portrait);

              landscape_label = findViewById(R.id.fruit_target_landscape);

              landscape_label.setText(fruit_classifier);

              rev_landscape_label = findViewById(R.id.fruit_target_reverse_landscape);

              iv = findViewById(R.id.fruitDetectedView);

              nxt = findViewById(R.id.nextFruit);

              mOpenCvCameraView.enableView();
            }
            break;
            default: {
              super.onManagerConnected(status);
            }
            break;
          }
        }
      };

  public MainActivity() {

    Log.i(TAG, "Instantiated new " + this.getClass());

    mDetectorName = new String[2];

    mDetectorName[JAVA_DETECTOR] = "Java";
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    Log.i(TAG, "...called onCreate.");

    super.onCreate(savedInstanceState);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    // Remove title bar
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    // Remove notification bar
    getWindow()
        .setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.show_camera);

    mOpenCvCameraView = findViewById(R.id.show_camera_activity_java_surface_view);

    mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

    mOpenCvCameraView.setCvCameraViewListener(this);

    mOpenCvCameraView.setCameraIndex(0);
    // Get an instance of the SensorManager
    try {

      mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);

      if (mSensorManager != null) {
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
      }

      mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);

    } catch (Exception e) {

      Toast.makeText(this, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
    }

    avi = findViewById(R.id.avi);

    avi.show();

    mWidth = (this.getResources().getDisplayMetrics().widthPixels) / 2;

    mHeight = (this.getResources().getDisplayMetrics().heightPixels) / 2;

    centre = new Point(mWidth, mHeight);

    nextButton = findViewById(R.id.nextFruit);

    nextButton.setOnClickListener(clickListener);
    // Calling Application class (see application tag in AndroidManifest.xml)
    undetected = (Detectable) getApplicationContext();
  }

  @Override
  public void onPause() {

    Log.i(TAG, "...called onPause.");

    super.onPause();

    if (mOpenCvCameraView != null) {
      mOpenCvCameraView.disableView();
    }

    mSensorManager.unregisterListener(this);
  }

  @Override
  public void onResume() {

    Log.i(TAG, "...called onResume.");

    super.onResume();

    if (!OpenCVLoader.initDebug()) {

      Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");

      OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);

    } else {

      Log.d(TAG, "OpenCV library found inside package. Using it!");

      mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    if (mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {

      Sensor s = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);

      mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    }
  }

  @Override
  public void onDestroy() {

    Log.i(TAG, "...called onDestroy.");

    super.onDestroy();

    if (mOpenCvCameraView != null) {
      mOpenCvCameraView.disableView();
    }
  }

  /**
   * This method is invoked when camera preview has started. After this method is invoked the
   * frames will start to be delivered to client via the onCameraFrame() callback.
   * @param width -  the width of the frames that will be delivered
   * @param height - the height of the frames that will be delivered
   */
  @Override
  public void onCameraViewStarted(int width, int height) {

    mRgba = new Mat(height, width, CvType.CV_8UC4);

  }

  /**
   * This method is invoked when camera preview has been stopped for some reason. No frames will
   * be delivered via onCameraFrame() callback after this method is called.
   */
  @Override
  public void onCameraViewStopped() {

    mGray.release();

    mRgba.release();

    tmp.release();

  }

  /**
   * This method is invoked when delivery of the frame needs to be done. The returned values -
   * is a modified frame which needs to be displayed on the screen.
   * @param inputFrame
   * @return a mat object
   */
  @Override
  public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

    tmp = inputFrame.rgba().clone();

    mRgba = inputFrame.rgba();

    mGray = inputFrame.gray();

    if (mAbsoluteFruitSize == 0) {
      int height = mGray.rows();
      if (Math.round(height * mRelativeFruitSize) > 0) {
        mAbsoluteFruitSize = Math.round(height * mRelativeFruitSize);
      }
    }


    MatOfRect fruit = new MatOfRect();

    if (mDetectorType == JAVA_DETECTOR) {
      if (mJavaDetector != null) {
        mJavaDetector.detectMultiScale(
            mGray, fruit, 1.05, 2, 2, new Size(mAbsoluteFruitSize, mAbsoluteFruitSize), new Size());
        mGray.release();
      }
    } else {
      Log.e(TAG, "Detection method is not selected!");
    }

    Rect[] fruitArray = fruit.toArray();

    for (Rect aFruitArray : fruitArray) {

      Imgproc.rectangle(mRgba, aFruitArray.tl(), aFruitArray.br(), RED, 3);

      Point quantizedTL =
          new Point(((int) (aFruitArray.tl().x / 100)) * 100, ((int) aFruitArray.tl().y / 100));

      Point quantizedBR =
          new Point(((int) (aFruitArray.br().x / 100)) * 100, ((int) aFruitArray.br().y / 100));

      int bucktID = quantizedTL.hashCode() + quantizedBR.hashCode() * 2;

      if (rectBuckts.containsKey(bucktID)) {

        rectBuckts.put(bucktID, rectBuckts.get(bucktID) + 1);

        rectCue.put(bucktID, new Rect(quantizedTL, quantizedBR));

      } else {

        rectBuckts.put(bucktID, 1);
      }
    }

    int maxDetections = 0;

    int maxDetectionsKey = 0;

    for (Map.Entry<Integer, Integer> e : rectBuckts.entrySet()) {
      if (e.getValue() > maxDetections) {
        maxDetections = e.getValue();
        maxDetectionsKey = e.getKey();
      }
    }
    if (maxDetections > 5) {

      new AsyncTask<Mat, Void, Bitmap>() {
        @Override
        protected Bitmap doInBackground(Mat... mats) {

          modified = mats[0];

          Imgproc.putText(
              modified,
              "...nice choice!",
              new Point(30, 80),
              Core.FONT_HERSHEY_PLAIN,
              2.2,
              new Scalar(200, 200, 0),
              2);

          // convert to bitmap:
          bm = Bitmap.createBitmap(modified.cols(), modified.rows(), Bitmap.Config.ARGB_8888);

          Utils.matToBitmap(modified, bm);

          modified.release();

          return bm;

        }

        @Override
        protected void onPostExecute(Bitmap aVoid) {

          // update ui
          iv.setImageBitmap(aVoid);

          iv.setVisibility(View.VISIBLE);

          mOpenCvCameraView.disableView();

          nxt.setVisibility(View.VISIBLE);

          landscape_label.setVisibility(View.GONE);

        }
      }.execute(tmp);

      rectBuckts.clear();

    }

    return mRgba;

  }

  /**
   * Updates the ui with information ("motion 90 degrees") depending on detected rotation.
   * @param vectors e.g. SensorEvent event values
   */
  private void update(float[] vectors) {

    float[] rotationMatrix = new float[9];

    SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);

    int worldAxisX = SensorManager.AXIS_X;

    int worldAxisZ = SensorManager.AXIS_Z;

    float[] adjustedRotationMatrix = new float[9];

    SensorManager.remapCoordinateSystem(
        rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);

    float[] orientation = new float[3];

    SensorManager.getOrientation(adjustedRotationMatrix, orientation);

    //    float pitch = orientation[1] * FROM_RADS_TO_DEGS;

    float roll = orientation[2] * FROM_RADS_TO_DEGS; // relevant

    if ((roll >= 70 && roll <= 135)) {

      portrait_label.setVisibility(View.GONE);

      landscape_label.setVisibility(View.VISIBLE);

      avi.setVisibility(View.VISIBLE);

      rev_landscape_label.setVisibility(View.GONE);
    }

    if (roll >= -180 && roll <= -70) {

      portrait_label.setVisibility(View.GONE);

      landscape_label.setVisibility(View.GONE);

      avi.setVisibility(View.GONE);

      rev_landscape_label.setVisibility(View.VISIBLE);
    }

    if (roll <= -320 || (roll >= 0 && roll <= 45)) {

      portrait_label.setVisibility(View.VISIBLE);

      landscape_label.setVisibility(View.GONE);

      avi.setVisibility(View.GONE);

      rev_landscape_label.setVisibility(View.GONE);
    }
  }

  @Override
  public void onSensorChanged(SensorEvent event) {

    if (event.sensor == mRotationSensor) {
      if (event.values.length > 4) {
        float[] truncatedRotationVector = new float[4];
        System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
        update(truncatedRotationVector);
      } else {
        update(event.values);
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

  View.OnClickListener clickListener =
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {

          int index;

          Log.i("TAG", "Next object to identify");

          if (undetected.detectables.size() > 1) {

            undetected.detectables.remove(fruit_classifier);

            index = undetected.detectables.size() - 1;

            Intent ii = new Intent();

            ii.putExtra("fruit", undetected.detectables.get(index));

            ii.setClass(MainActivity.this, MainActivity.class);

            startActivity(ii);

            finish();

          } else {
            // launch final activity
            Intent ii = new Intent(MainActivity.this, SaladActivity.class);

            startActivity(ii);

            finish();
          }
        }
      }; // end-of-clickListener
}