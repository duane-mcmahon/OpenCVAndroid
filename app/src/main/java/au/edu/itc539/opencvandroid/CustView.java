package au.edu.itc539.opencvandroid;

import static au.edu.itc539.opencvandroid.R.drawable.rote180;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 *
 * It was necessary to extend the imageview class to show an icon in the right
 * direction that signs that the user should rotate the device 180 degrees.
 * */
public class CustView extends android.support.v7.widget.AppCompatImageView {

  public CustView(Context context) {
    super(context);

  }

  public CustView(Context context, AttributeSet attrs) {
    super(context, attrs);

  }

  public CustView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

  }

  @Override
  protected void onDraw(Canvas canvas) {

    canvas.save();

    float py = this.getHeight() / 2.0f;

    float px = this.getWidth() / 2.0f;

    canvas.rotate(180, px, py);

    canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), rote180), 0, 0, null);

    super.onDraw(canvas);

    canvas.restore();
  }
}
