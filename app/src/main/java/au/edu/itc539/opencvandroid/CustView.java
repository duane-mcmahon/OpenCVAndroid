package au.edu.itc539.opencvandroid;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import static au.edu.itc539.opencvandroid.R.drawable.rote180;

/**
 * Created by duane on 29/11/2017.
 */

public class CustView extends android.support.v7.widget.AppCompatImageView {

    public CustView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
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