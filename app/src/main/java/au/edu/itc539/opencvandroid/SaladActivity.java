package au.edu.itc539.opencvandroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
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
 * <b> Detects an orange AND a banana in real time </b> using <br />
 * the camera of an android phone or tablet. Developed on a <br />
 * motorola moto z play. <br />
 *
 * @author Duane McMahon
 * @version 1.0
 * @since 07-01-2018
 */
public class SaladActivity extends AppCompatActivity
    implements CameraBridgeViewBase.CvCameraViewListener2, SensorEventListener {

  private final Scalar ORANGE = new Scalar(255, 140, 0);

  private final Scalar YELLOW = new Scalar(200, 200, 0);

  private static final long TIMEOUT = 1000L;

  private BlockingQueue<CvCameraViewFrame> frames = new LinkedBlockingQueue<>(4);

  private BlockingQueue<Mat> outFrames = new LinkedBlockingQueue<>(4);

  private Hashtable<Integer, Integer> bananaBuckts = new Hashtable<>();

  private Hashtable<Integer, Integer> orangeBuckts = new Hashtable<>();

  private static final int JAVA_DETECTOR = 0;
  // Used for logging success or failure messages
  private static final String TAG = "SaladActivity";

  // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
  private JavaCameraView mOpenCvCameraView;

  private File bananaCascadeFile, orangeCascadeFile;

  private int mDetectorType = JAVA_DETECTOR;

  private String[] mDetectorName;

  private CascadeClassifier bananaJavaDetector, orangeJavaDetector;

  private Mat mRgba, mGray, tmp;

  private float mRelativeFruitSize = 0.2f;

  private int mAbsoluteFruitSize = 0;

  private ImageView portrait_label, rev_landscape_label, iv;

  private TextView landscape_label;

  private ImageButton last;

  private SensorManager mSensorManager;

  private Sensor mRotationSensor;

  private static final int SENSOR_DELAY = 500 * 1000; // 500ms

  private static final int FROM_RADS_TO_DEGS = -57;

  private String banana = "banana";

  private String orange = "orange";

  private String fruit_classifier = "salad";

  private AVLoadingIndicatorView avi;

  private int mWidth, mHeight;

  private Point centre;
  // initialize and load the opencv libraries and modules
  private BaseLoaderCallback mLoaderCallback =
      new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
          switch (status) {
            case LoaderCallbackInterface.SUCCESS: {
              Log.i(TAG, "OpenCV loaded successfully.");

              try {
                // load cascade file from application resources
                InputStream is =
                    getResources()
                        .openRawResource(
                            getResources().getIdentifier(banana, "raw", getPackageName()));

                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                bananaCascadeFile = new File(cascadeDir, banana + ".xml");
                FileOutputStream os = new FileOutputStream(bananaCascadeFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                  os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();

                bananaJavaDetector = new CascadeClassifier(bananaCascadeFile.getAbsolutePath());

                if (bananaJavaDetector.empty()) {
                  Log.e(TAG, "Failed to load banana cascade classifier");
                  bananaJavaDetector = null;
                } else {
                  Log.i(
                      TAG,
                      "Loaded cascade classifier from " + bananaCascadeFile.getAbsolutePath());
                }

              } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to load banana detection cascade. Exception thrown: " + e);
              }

              try {
                // load cascade file from application resources
                InputStream is =
                    getResources()
                        .openRawResource(
                            getResources().getIdentifier(orange, "raw", getPackageName()));

                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                orangeCascadeFile = new File(cascadeDir, orange + ".xml");
                FileOutputStream os = new FileOutputStream(orangeCascadeFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                  os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();

                orangeJavaDetector = new CascadeClassifier(orangeCascadeFile.getAbsolutePath());

                if (orangeJavaDetector.empty()) {
                  Log.e(TAG, "Failed to load orange cascade classifier");
                  orangeJavaDetector = null;
                } else {
                  Log.i(
                      TAG,
                      "Loaded cascade classifier from " + orangeCascadeFile.getAbsolutePath());
                }

              } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to load orange detection cascade. Exception thrown: " + e);
              }

              portrait_label = findViewById(R.id.fruit_target_portrait);

              landscape_label = findViewById(R.id.fruit_target_landscape);

              landscape_label.setText(fruit_classifier);

              rev_landscape_label = findViewById(R.id.fruit_target_reverse_landscape);

              iv = findViewById(R.id.fruitDetectedView);

              last = findViewById(R.id.lastFruits);

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

  public SaladActivity() {

    Log.i(TAG, "Instantiated new " + this.getClass());

    mDetectorName = new String[2];

    mDetectorName[JAVA_DETECTOR] = "Java";
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    finish();
    return super.dispatchTouchEvent(ev);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    Log.i(TAG, "called onCreate");

    super.onCreate(savedInstanceState);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    // Remove title bar
    requestWindowFeature(Window.FEATURE_NO_TITLE);

    // Remove notification bar
    getWindow()
        .setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.show_camera_salad);

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

      if (mSensorManager != null) {
        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
      }

    } catch (Exception e) {

      Toast.makeText(this, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
    }

    avi = findViewById(R.id.avi);

    avi.show();

    mWidth = (this.getResources().getDisplayMetrics().widthPixels) / 2;

    mHeight = (this.getResources().getDisplayMetrics().heightPixels) / 2;

    centre = new Point(mWidth, mHeight);

    Thread worker =
        new Thread() {
          @Override
          public void run() {
            while (!Thread.currentThread().isInterrupted()) {

              CvCameraViewFrame inputFrame = null;

              try {
                inputFrame = frames.poll(TIMEOUT, TimeUnit.MILLISECONDS);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              if (inputFrame == null) {
                // timeout. Also, with a try {} catch block poll can be interrupted via
                // Thread.interrupt() so not to wait for the timeout.
                continue;
              }

              tmp = inputFrame.rgba().clone();

              mRgba = inputFrame.rgba();

              mGray = inputFrame.gray();

              int maxOrangeDetections = 0;

              Rect[] oranges = detectFruit(
                  mGray,
                  mAbsoluteFruitSize,
                  mRelativeFruitSize,
                  orangeJavaDetector
              );

              int maxBananaDetections = 0;

              Rect[] bananas = detectFruit(
                  mGray,
                  mAbsoluteFruitSize,
                  mRelativeFruitSize,
                  bananaJavaDetector
              );

              paintRectangles(oranges, ORANGE, orangeBuckts);

              paintRectangles(bananas, YELLOW, bananaBuckts);

              outFrames.add(mRgba);

              for (Map.Entry<Integer, Integer> e : orangeBuckts.entrySet()) {
                if (e.getValue() > maxOrangeDetections) {
                  maxOrangeDetections = e.getValue();
                }
              }


              for (Map.Entry<Integer, Integer> e : bananaBuckts.entrySet()) {
                if (e.getValue() > maxBananaDetections) {
                  maxBananaDetections = e.getValue();
                }
              }
              // if both objects are detected consistently in five successive frames...
              if ((maxBananaDetections >= 5)
                  && (maxOrangeDetections >= 5)) {

                new AsyncTask<Mat, Void, Bitmap>() {
                  @Override
                  protected Bitmap doInBackground(Mat... mats) {

                    Mat m = mats[0];

                    Imgproc.putText(
                        m,
                        "...nice SALAD! Tap to exit the demo.",
                        new Point(30, 80),
                        Core.FONT_HERSHEY_SCRIPT_SIMPLEX,
                        2.2,
                        YELLOW,
                        2);

                    // convert to bitmap:
                    Bitmap bm = Bitmap.createBitmap(m.cols(), m.rows(), Config.ARGB_8888);

                    Utils.matToBitmap(m, bm);

                    return bm;
                  }

                  // update ui
                  @Override
                  protected void onPostExecute(Bitmap aVoid) {

                    landscape_label.setVisibility(View.GONE);

                    portrait_label.setVisibility(View.GONE);

                    rev_landscape_label.setVisibility(View.GONE);

                    iv.setImageBitmap(aVoid);

                    iv.setVisibility(View.VISIBLE);

                    mOpenCvCameraView.disableView();

                    last.setVisibility(View.VISIBLE);

                  }
                }.execute(tmp);

                orangeBuckts.clear();

                bananaBuckts.clear();
              }
            }
          }
        };
    worker.start();
  }

  @Override
  public void onPause() {

    super.onPause();

    if (mOpenCvCameraView != null) {
      mOpenCvCameraView.disableView();
    }

    mSensorManager.unregisterListener(this);
  }

  @Override
  public void onResume() {

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

  public void onDestroy() {

    super.onDestroy();

    if (mOpenCvCameraView != null) {
      mOpenCvCameraView.disableView();
    }
  }

  /**
   * This method is invoked when camera preview has started. After this method is invoked the
   * frames will start to be delivered to client via the onCameraFrame() callback.
   *
   * @param width -  the width of the frames that will be delivered
   * @param height - the height of the frames that will be delivered
   */
  @Override
  public void onCameraViewStarted(int width, int height) {

    mGray = new Mat();

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
  }

  /**
   * This method is invoked when delivery of the frame needs to be done. The returned values -
   * is a modified frame which needs to be displayed on the screen.
   * @param inputFrame
   * @return a mat object
   */
  @Override
  public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

    final Mat rgba = inputFrame.rgba();
    // This is the producer of the blocking queue
    try {

      frames.put(inputFrame);

    } catch (InterruptedException e) {

      Log.d(TAG, "No frame captured.");
    }

    try {

      return outFrames.take();

    } catch (InterruptedException e) {

      return rgba;
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

  private Rect[] detectFruit(
      Mat gray,
      int abs,
      float rel,
      CascadeClassifier classifier) {

    Mat mGray = gray;

    int mAbsoluteFruitSize = abs;

    if (mAbsoluteFruitSize == 0) {

      int height = mGray.rows();

      if (Math.round(height * rel) > 0) {

        mAbsoluteFruitSize = Math.round(height * rel);
      }
    }

    MatOfRect fruit = new MatOfRect();

    if (classifier != null) {

      classifier.detectMultiScale(
          mGray, fruit, 1.05, 2, 2, new Size(mAbsoluteFruitSize, mAbsoluteFruitSize), new Size());
    }

    Rect[] fruitArray = fruit.toArray();

    return fruitArray;

  }


  private void paintRectangles(Rect[] fruitArray, Scalar COLOUR,
      Hashtable<Integer, Integer> rectBuckts) {

    for (Rect aFruitArray : fruitArray) {

      Imgproc.rectangle(mRgba, aFruitArray.tl(), aFruitArray.br(), COLOUR, 3);

      Point quantizedTL =
          new Point(((int) (aFruitArray.tl().x / 100)) * 100, ((int) aFruitArray.tl().y / 100));

      Point quantizedBR =
          new Point(((int) (aFruitArray.br().x / 100)) * 100, ((int) aFruitArray.br().y / 100));

      int bucktID = quantizedTL.hashCode() + quantizedBR.hashCode() * 2;

      if (rectBuckts.containsKey(bucktID)) {

        rectBuckts.put(bucktID, rectBuckts.get(bucktID) + 1);

      } else {

        rectBuckts.put(bucktID, 1);

      }

    }

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
}
